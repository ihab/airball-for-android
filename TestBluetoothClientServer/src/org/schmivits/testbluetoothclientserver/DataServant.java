package org.schmivits.testbluetoothclientserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

public class DataServant extends RpcThread {

  private static final long WRITE_PAUSE = 20L;
  private static final long WRITE_TIMEOUT = 1000L;  
  
  private final BlockingQueue<String> mData = new LinkedBlockingQueue<String>();
  private final BluetoothSocket mTargetSocket;
  private final DataSourceStream mSource;
  private final DataSourceStream.Listener mListener = new DataSourceStream.Listener() {
    @Override public void data(String s) {
      try { mData.put(s); } catch (InterruptedException e) { }
    }
  }; 
  
  public DataServant(TextView view, BluetoothSocket socket, DataSourceStream source) {
    super(view);
    report("Created DataServant");
    mTargetSocket = socket;
    mSource = source;
    source.addListener(mListener);        
  }
  
  @Override
  public void run() {
    try {
      OutputStream os = mTargetSocket.getOutputStream();
      long timeoutStart = 0L;
      report("DataServant entering servant loop");      
      while (!isInterrupted()) {
        if (mData.size() > 0) {
          String s = null;
          synchronized (mData) {
            if (mData.size() > 0) { s = mData.take(); }
          }
          if (s != null) {
            os.write(s.getBytes());
            os.write('\n');
            os.flush();
            timeoutStart = 0L;
          }
        } else {
          if (timeoutStart == 0L) {
            timeoutStart = System.currentTimeMillis();
          } else if (System.currentTimeMillis() - timeoutStart > WRITE_TIMEOUT) {
            report("DataServant timeout");
            destroy();
            return;
          }
          Thread.sleep(WRITE_PAUSE);
        }
      }
    } catch (InterruptedException e) {
      report("DataServant interrupted");
      destroy();      
      return;
    } catch (IOException e) {
      report("DataServant IOException " + e);
      destroy();
      return;
    }
  }

  @Override
  public void destroy() {
    mSource.removeListener(mListener);
    try { mTargetSocket.close(); } catch (IOException e) { }
    report("Destroyed DataServant");    
    super.destroy();
  }
}
