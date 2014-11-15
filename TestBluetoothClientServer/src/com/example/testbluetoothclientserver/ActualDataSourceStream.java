package com.example.testbluetoothclientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

public class ActualDataSourceStream extends RpcStates implements DataSourceStream {
  
  private final List<Listener> mListeners = new ArrayList<Listener>();
  private final BluetoothAdapter mAdapter;
  private BluetoothDevice mSourceDevice;
  private BluetoothSocket mSourceSocket;
  private BufferedReader mSourceReader;
  
  public ActualDataSourceStream(TextView view, BluetoothAdapter adapter) {
    super(view, "SeekingSourceDevice");
    mAdapter = adapter;
  }

  @Override
  protected Map<String, ThreadState> buildStates() {
    Map<String, ThreadState> s = new HashMap<String, ThreadState>();
    
    s.put("SeekingSourceDevice", new ThreadState() {
      @Override public void onEnter() throws Exception {
        report("About to call getBondedDevices");        
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        report("Called getBondedDevices");
        for (BluetoothDevice device : pairedDevices) {
          if (device.getName().equals(SERIAL_DEVICE_NAME)) {
            mSourceDevice = device;
          }
        }
        mAdapter.cancelDiscovery();
        if (mSourceDevice != null) {
          enter("OpenedSourceSocket");
        }
      }
      @Override public void onCleanup() {
        mSourceDevice = null;
      }
    });

    s.put("OpenedSourceSocket", new ThreadState() {
      @Override public void onEnter() throws Exception {
        mSourceSocket = mSourceDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
        mSourceSocket.connect();
        enter("OpenedSourceReader");
      }
      @Override public void onCleanup() {
        if (mSourceSocket != null) {
          try { mSourceSocket.close(); } catch (IOException ignored) {}
          mSourceSocket = null;
        }
      }
    });

    s.put("OpenedSourceReader", new ThreadState() {
      @Override public void onEnter() throws Exception {
        mSourceReader = new BufferedReader(new InputStreamReader(mSourceSocket.getInputStream()));
        enter("ReadingData");
      }
      @Override public void onCleanup() {
        mSourceReader = null;        
      }
    });
    
    s.put("ReadingData", new ThreadState() {
      @SuppressLint("NewApi")
      @Override public void onEnter() throws Exception {
        while (true) {
          if (!mSourceSocket.isConnected()) {
            // Disconnected sockets means we are aborted
            return;
          }
          String line = mSourceReader.readLine();
          for (Listener l : mListeners) {
            l.data(line);
          }
        }
      }
      @Override public void onCleanup() { }
    });
    
    return s;
  }

  @Override
  public void addListener(Listener l) {
    synchronized (mListeners) {
      mListeners.add(l);
    }
  }

  @Override
  public void removeListener(Listener l) {
    synchronized (mListeners) {
      mListeners.remove(l);
    }
  }
}
