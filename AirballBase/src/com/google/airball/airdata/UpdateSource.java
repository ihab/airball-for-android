package com.google.airball.airdata;

public interface UpdateSource {
  void addUpdateListener(Runnable r);
  void removeUpdateListener(Runnable r);  
}
