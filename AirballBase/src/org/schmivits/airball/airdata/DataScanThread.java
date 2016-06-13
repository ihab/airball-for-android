package org.schmivits.airball.airdata;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class DataScanThread extends Thread {
  
  private static final long CONNECTION_TRY_AGAIN_PAUSE = 1000L;
  private static final long READ_TIMEOUT = 5000L;
  private static final long READ_PAUSE = 50L;

  private final BluetoothAdapter mAdapter;
  private final UUID mServiceUuid;
  private final HaveData mHaveData;
  private boolean mRunning = true;
  
  public DataScanThread(BluetoothAdapter adapter, UUID serviceUuid, HaveData haveData) {
    mAdapter = adapter;
    mServiceUuid = serviceUuid;
    mHaveData = haveData;
  }
  
  @Override public final void run() {
    while (mRunning) {
      try {
        runOnce();
      } catch (IOException e) {
        Log.v(getClass().getName(), "Unexpected exception: " + e);
        // Fall through and try again in a while
      }
      try { 
        Thread.sleep(CONNECTION_TRY_AGAIN_PAUSE);
      } catch (InterruptedException e) {
        Log.v(getClass().getName(), "Interrupted while sleeping, quitting");
        mRunning = false;
      }
    }
  }
  
  private void runOnce() throws IOException {
    BluetoothSocket socket = null;
    try {
      for (BluetoothDevice paired : mAdapter.getBondedDevices()) {
        try {
          // We accept the connection to the first device we find
          // that is serving on the proper UUID
          socket = attemptConnection(paired);
          Log.v(getClass().getName(), "Connected to device " + paired.getName());
          break;
        } catch (IOException e) {
          Log.v(getClass().getName(), "Failed to connect to device " + paired.getName());
          // Didn't work; try the next device
        }
      }
    } finally {
      mAdapter.cancelDiscovery();
    }
    if (socket != null) {
      fetchData(socket);
    }
  }
  
  private BluetoothSocket attemptConnection(BluetoothDevice device) throws IOException {
    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(mServiceUuid);
    socket.connect();
    return socket;
  }
  
  private void fetchData(BluetoothSocket socket) throws IOException {
    try {
      Log.v(getClass().getName(), "Processing data");
      NonBlockingLineReader r = new NonBlockingLineReader(socket.getInputStream());
      long timeoutBase = 0L;
      while (true) {
        String line = r.maybeReadLine();
        if (line != null) {
          // Distribute data to client and continue
          mHaveData.line(line);
          timeoutBase = 0L;
        } else {
          if (timeoutBase == 0L) {
            // Begin timeout period
            timeoutBase = System.currentTimeMillis();
          } else {
            // Already within a timeout period
            if (System.currentTimeMillis() - timeoutBase > READ_TIMEOUT) {
              Log.v(getClass().getName(), "Read timeout");
              // Timed out, return and try again to reestablish connection
              return;
            } else {
              // Pause before retrying
              try {
                Thread.sleep(READ_PAUSE);
              } catch (InterruptedException e) {
                Log.v(getClass().getName(), "Interrupted while sleeping, quitting");
                mRunning = false; 
                return;
              }
            }
          }
        }
        if (interrupted()) {
          Log.v(getClass().getName(), "Interrupted while processing data, quitting");
          mRunning = false; 
          return;
        }
      }
    } finally {
      Log.v(getClass().getName(), "Closing socket to server");
      socket.close();
    }
  }
}
