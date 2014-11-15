package com.example.testbluetoothclientserver;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

public class AirballServer extends RpcThread {

  private final BluetoothAdapter mAdapter;
  private BluetoothServerSocket mServerSocket = null;
  
  public AirballServer(BluetoothAdapter adapter, TextView view) {
    super(view);
    report("Constructed AirballServer");
    mAdapter = adapter;
  }

  @Override
  public void run() {
    report("AirballServer run() ...");
    try {
      report("Creating server socket");
      mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("Airball Data", AIRBALL_UUID);
      report("Accepting ...");
      while (true) {
        BluetoothSocket socket = mServerSocket.accept();
        report("Accepted a connection; creating a data sender");
        RpcThread sender = new AirballDataSender(socket, mView);
        mChildren.add(sender);
        sender.start();
      }
    } catch (IOException e) {
      report("AirballServer run() exiting: " + e);
    }
  }

  @Override
  public void destroy() {
    report("AirballServer destroy() ...");
    if (mServerSocket != null) {
      try { mServerSocket.close(); } catch (IOException e) { }
    }
    super.destroy();
  }
}
