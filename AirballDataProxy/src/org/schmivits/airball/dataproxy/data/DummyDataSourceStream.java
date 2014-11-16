package org.schmivits.airball.dataproxy.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import org.schmivits.dynonskyview.DummyFlightData;
import org.schmivits.dynonskyview.DynonSerialFormat;

public class DummyDataSourceStream extends ConcurrentProcess implements DataSourceStream {
  
  private static final long DATA_PERIOD = 1000L * 10L;    // ten seconds
  private static final long DATA_INTERVAL = 1000L / 16L;  // sixteen data points per second

  private final List<Listener> mListeners = new ArrayList<Listener>();

  public DummyDataSourceStream(Context context, ProcessNode parent, String title) {
    super(context, parent, title);
  }
  
  @Override
  public void doRun() {
    DummyFlightData data = new DummyFlightData(DATA_PERIOD, System.currentTimeMillis());
    while (isRunning()) {
      String str = DynonSerialFormat.dataToWord(data.getBlock(System.currentTimeMillis()));
      setStatus(str);
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
