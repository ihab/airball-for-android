package org.schmivits.testbluetoothclientserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.widget.TextView;

public class DataServer extends RpcStates {

  private final BluetoothAdapter mAdapter;
  private final DataSourceStream mSource;
  private BluetoothServerSocket mTargetServerSocket;
  
  public DataServer(TextView view, BluetoothAdapter adapter, DataSourceStream source) {
    super(view, "WaitingForListener");
    mAdapter = adapter;
    mSource = source;
    mChildren.add(source);
  }
  
  @Override
  protected Map<String, ThreadState> buildStates() {
    Map<String, ThreadState> s = new HashMap<String, ThreadState>();
    
    s.put("WaitingForListener", new ThreadState() {
      @Override public void onEnter() throws Exception {
        mTargetServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("Airball Data", AIRBALL_UUID);        
        while (mRunning) {
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
        DataServant servant = new DataServant(mView, mTargetServerSocket.accept(), mSource);        
        mChildren.add(servant);
        servant.start();
      }
      @Override public void onCleanup() { }
    });    
    
    return s;
  }
}
