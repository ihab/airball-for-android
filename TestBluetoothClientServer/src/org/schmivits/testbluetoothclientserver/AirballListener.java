package org.schmivits.testbluetoothclientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

class AirballListener extends RpcThread {

  private final BluetoothDevice mDevice;
  private BluetoothSocket mSocket;

  public AirballListener(BluetoothDevice device, TextView view) {
    super(view);
    mDevice = device;
  }

  @Override
  public void run() {
    report("AirballListener run() ...");
    try {
      mSocket = mDevice.createRfcommSocketToServiceRecord(AIRBALL_UUID);
      report("Connecting to socket ...");
      mSocket.connect();
      report("Building reader ...");
      BufferedReader r = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
      while (true) {
        report("About to readLine ...");
        for (String line = null; (line = r.readLine()) != null; ) {
          report(line);
        }
      }
    } catch (IOException e) {
      report("AirballListener run() exiting: " + e);
    }
  }

  @Override
  public void destroy() {
    report("AirballListener destroy() ...");
    try { mSocket.close(); } catch (IOException e) { }
  }
}
