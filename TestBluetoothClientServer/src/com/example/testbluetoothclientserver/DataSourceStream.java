package com.example.testbluetoothclientserver;

public interface DataSourceStream extends Lifecycle {
  
  public interface Listener {
    void data(String line);
  }

  void addListener(Listener l);
  
  void removeListener(Listener l);
}
