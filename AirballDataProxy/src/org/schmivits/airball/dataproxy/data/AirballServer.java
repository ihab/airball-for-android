package org.schmivits.airball.dataproxy.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothServerSocket;
import android.content.Context;

public abstract class AirballServer extends RpcStates {

  private int mServantCount = 0;
  private final DataSourceStream mSource;
  private BluetoothServerSocket mTargetServerSocket;
  
  protected AirballServer(Context context, AbstractProcess parent, String title) {
    super(context, parent, title, "WaitingForListener");
    addChild(mSource = makeDataSourceStream());
  }
  
  @Override
  protected Map<String, ThreadState> buildStates() {
    Map<String, ThreadState> s = new HashMap<String, ThreadState>();
    
    s.put("WaitingForListener", new ThreadState() {
      @Override public void onEnter() throws Exception {
        mTargetServerSocket = findBluetoothAdapter()
            .listenUsingRfcommWithServiceRecord("Airball Data", Constants.AIRBALL_UUID);
        while (isRunning()) {
          enter("CreatingServant");
        }
      }
      @Override public void onCleanup() {
        if (mTargetServerSocket != null) {
          try { mTargetServerSocket.close(); } catch (IOException ignored) {}
          mTargetServerSocket = null;
        }
      }
    });
    
    s.put("CreatingServant", new ThreadState() {
      @Override public void onEnter() throws Exception {
        addChild(new DataServant(
            getContext(), 
            AirballServer.this, 
            "" + mServantCount++,
            mTargetServerSocket.accept(),            
            mSource));        
      }
      @Override public void onCleanup() { }
    });    
    
    return s;
  }
  
  protected abstract DataSourceStream makeDataSourceStream();
}
