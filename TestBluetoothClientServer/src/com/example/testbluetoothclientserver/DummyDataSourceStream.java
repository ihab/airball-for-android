package com.example.testbluetoothclientserver;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import com.google.dynonskyview.DummyFlightData;
import com.google.dynonskyview.DynonSerialFormat;

public class DummyDataSourceStream extends RpcThread implements DataSourceStream {
  
  private static final long DATA_PERIOD = 1000L * 10L;    // ten seconds
  private static final long DATA_INTERVAL = 1000L / 16L;  // sixteen data points per second

  private final List<Listener> mListeners = new ArrayList<Listener>();

  public DummyDataSourceStream(TextView view) {
    super(view);
  }
  
  @Override
  public void run() {
    DummyFlightData data = new DummyFlightData(DATA_PERIOD, System.currentTimeMillis());
    while (true) {
      if (isInterrupted()) { return; }        
      String str = DynonSerialFormat.dataToWord(data.getBlock(System.currentTimeMillis()));
      for (Listener l : mListeners) { l.data(str); }
      try {
        Thread.sleep(DATA_INTERVAL);
      } catch (InterruptedException e) {
        return;
      }
    }
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
