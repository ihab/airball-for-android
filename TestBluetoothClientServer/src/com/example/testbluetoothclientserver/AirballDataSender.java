package com.example.testbluetoothclientserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

public class AirballDataSender extends RpcThread {

  private final BluetoothSocket mSocket;

  public AirballDataSender(BluetoothSocket socket, TextView view) {
    super(view);
    mSocket = socket;
  }

  @SuppressLint("NewApi")
  @Override
  public void run() {
    report("AirballDataSender run() ...");
    try {
      PrintWriter w = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));
      while (true) {
        if (!mSocket.isConnected()) { return; }
        String msg = "" + System.currentTimeMillis();
        report("AirballDataSender sending " + msg);
        w.println(msg);
        w.flush();
        try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
      }
    } catch (IOException e) {
      report("AirballDataSender run() exiting: " + e);
    }
  }

  @SuppressLint("NewApi")
  @Override
  public void destroy() {
    report("AirballDataSender destroy() ...");
    try { mSocket.close(); } catch (IOException e) { }
  }
}
