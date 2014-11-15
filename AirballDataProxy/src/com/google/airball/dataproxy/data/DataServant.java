package com.google.airball.dataproxy.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.content.Context;


public class DataServant extends ConcurrentProcess {

  private static final long WRITE_PAUSE = 20L;
  private static final long WRITE_TIMEOUT = 1000L;  
  
  private final BlockingQueue<String> mData = new LinkedBlockingQueue<String>();
  private final BluetoothSocket mTargetSocket;
  private final DataSourceStream mSource;
  
  private static final int HACK_COUNT = 4;
  int iHack = 0;

  private final DataSourceStream.Listener mListener = new DataSourceStream.Listener() {
    @Override public void data(String s) {
      iHack++;
      if (iHack == HACK_COUNT) {
        iHack = 0;
        try { mData.put(s); } catch (InterruptedException e) { }
      }
    }
  };

  public DataServant(Context context, ProcessNode parent, String title, BluetoothSocket socket, DataSourceStream source) {
    super(context, parent, title);
    setStatus("Created DataServant");
    mTargetSocket = socket;
    mSource = source;
    source.addListener(mListener);
  }

  @Override
  public void doRun() {
    try {
      OutputStream os = mTargetSocket.getOutputStream();
      long timeoutStart = 0L;
      setStatus("DataServant entering servant loop");      
      while (isRunning()) {
        if (mData.size() > 0) {
          String s = null;
          synchronized (mData) {
            if (mData.size() > 0) { s = mData.take(); }
          }
          if (s != null) {
            setStatus(s);
            os.write(s.getBytes());
            os.write('\n');
            os.flush();
            timeoutStart = 0L;
          }
        } else {
          if (timeoutStart == 0L) {
            timeoutStart = System.currentTimeMillis();
          } else if (System.currentTimeMillis() - timeoutStart > WRITE_TIMEOUT) {
            setStatus("DataServant timeout");
            signal();
            return;
          }
          Thread.sleep(WRITE_PAUSE);
        }
      }
    } catch (InterruptedException e) {
      setStatus("DataServant interrupted");
      signal();      
      return;
    } catch (IOException e) {
      setStatus("DataServant IOException " + e);
      signal();
      return;
    }
  }

  @Override
  public void signal() {
    mSource.removeListener(mListener);
    try { mTargetSocket.close(); } catch (IOException e) { }
    super.signal();
  }
}
