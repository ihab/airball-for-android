package org.schmivits.airball.temp;

public interface DataSourceStream extends ProcessNode {
  
  public interface Listener {
    void data(String line);
  }

  void addListener(Listener l);
  
  void removeListener(Listener l);
}
