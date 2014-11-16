package org.schmivits.airball.dataproxy.data;

import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class RpcStates extends ConcurrentProcess {

  public interface ThreadState {
    void onEnter() throws Exception;
    void onCleanup();
  }
  
  private static final long TRY_AGAIN_INTERVAL = 1000L;
  private final Map<String, ThreadState> mStates;
  private final String mInitialState;
  
  protected RpcStates(Context context, ProcessNode parent, String title, String initialState) {
    super(context, parent, title);
    mStates = buildStates();
    mInitialState = initialState;
  }

  @Override
  public final void doRun() {
    while (isRunning()) {
      try {
        enter(mInitialState);
      } catch (InterruptedException e) {
        return;
      } catch (RuntimeException e) {
        setStatus(e.toString());
      }
      try {
        Thread.sleep(TRY_AGAIN_INTERVAL);
      } catch (InterruptedException e) {
        return;
      }
    }
  }
  
  protected abstract Map<String, ThreadState> buildStates();
  
  protected void enter(String name) throws InterruptedException {
    try {
      reportEnter(name);
      mStates.get(name).onEnter();
    } catch (Exception e) {
      handleException(name, e);
    } finally {
      mStates.get(name).onCleanup();
      reportExit(name);
    }
  }

  private void reportEnter(String name) {
    setStatus("-> entering " + name);
  }
  
  private void reportExit(String name) {
    setStatus("<- exiting " + name);
  }
  
  private void handleException(String name, Exception e) throws InterruptedException {
    if (e instanceof InterruptedException) {
      setStatus(name + " interrupted");
      throw (InterruptedException) e;
    } else if (e instanceof RuntimeException) {
      setStatus(name + " exception " + e.toString());
      throw (RuntimeException) e;
    } else {
      setStatus(name + " exception " + e.toString());      
      throw new RuntimeException(e);
    }
  }
}