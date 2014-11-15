package com.example.testbluetoothclientserver;

import java.util.Map;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class RpcStates extends RpcThread {

  public interface ThreadState {
    void onEnter() throws Exception;
    void onCleanup();
  }
  
  private static final long TRY_AGAIN_INTERVAL = 1000L;
  private final Map<String, ThreadState> mStates;
  private final String mInitialState;
  protected boolean mRunning = true;
  
  protected RpcStates(TextView view, String initialState) {
    super(view);
    mStates = buildStates();
    mInitialState = initialState;
  }

  @Override
  public final void run() {
    while (mRunning) {
      try {
        enter(mInitialState);
      } catch (InterruptedException e) {
        return;
      } catch (RuntimeException e) {
        report(e.toString());
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
    report("-> entering " + name);
  }
  
  private void reportExit(String name) {
    report("<- exiting " + name);
  }
  
  private void handleException(String name, Exception e) throws InterruptedException {
    if (e instanceof InterruptedException) {
      report(name + " interrupted");
      throw (InterruptedException) e;
    } else if (e instanceof RuntimeException) {
      report(name + " exception " + e.toString());
      throw (RuntimeException) e;
    } else {
      report(name + " exception " + e.toString());      
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void destroy() {
    mRunning = false;
    super.destroy();
  }
}