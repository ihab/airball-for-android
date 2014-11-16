package org.schmivits.airball.airdata;

import java.util.ArrayList;
import java.util.List;

public class UpdateSourceHelper implements UpdateSource {

  private final List<Runnable> mListeners = new ArrayList<Runnable>();
  
  @Override
  public void addUpdateListener(Runnable r) {
    mListeners.add(r);
  }

  @Override
  public void removeUpdateListener(Runnable r) {
    mListeners.remove(r);
  }

  public void fire() {
    for (Runnable r : mListeners) {
      r.run();
    }
  }
}
