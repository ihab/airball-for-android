package org.schmivits.testbluetoothclientserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;
import android.widget.TextView;

public abstract class RpcThread extends Thread implements Lifecycle {

  protected static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  protected static final UUID AIRBALL_UUID = UUID.fromString("f4a9bf39-1970-4bc8-a297-7596b2dae998"); 
  
  protected static final String SERIAL_DEVICE_NAME = "Serial Adaptor";
  
  protected final TextView mView;
  protected final List<Lifecycle> mChildren = new ArrayList<Lifecycle>();
  
  protected RpcThread(TextView view) {
    mView = view;
  }

  protected void report(final String msg) {
    Log.v(getClass().getName(), msg);
    mView.post(new Runnable() {
      @Override public void run() {
        mView.append(msg + "\n");
      }
    });
  }

  public void destroy() {
    for (Lifecycle t : mChildren) { t.destroy(); }
    mChildren.clear();
    interrupt();
  }
}
