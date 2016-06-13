package org.schmivits.airball.temp;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ConcurrentProcess extends AbstractProcess implements Runnable {

  private static final long DELAY_TILL_CLEANUP = 1000L; 
  
  private final Thread mThread;
  private boolean mRunning = true;
  
  protected ConcurrentProcess(Context context, ProcessNode parent, String title) {
    super(context, parent, title);
    (mThread = new Thread(this)).start();
  }

  @Override
  public final void run() {
    setRunMode(RunMode.RUNNING);
    try { 
      doRun();
    } catch (Exception e) {
      setStatus(stackTrace(e));
    } finally {
      setRunMode(RunMode.FINISHED);
      arrangeCleanup();
    }
  }
  
  private String stackTrace(Exception e) {
    StringWriter sb = new StringWriter();
    e.printStackTrace(new PrintWriter(sb));
    return sb.toString();
  }
  
  private void arrangeCleanup() {
    if (getParent() != null) {
      new Thread(new Runnable() {
        @Override public void run() {
          try { 
            Thread.sleep(DELAY_TILL_CLEANUP);
          } catch (InterruptedException e) { }
          getParent().cleanupChild(ConcurrentProcess.this);
        }
      }).start();
    }
  }
  
  @Override  
  public void signal() {
    // Two ways to tell the thread to shut down: Turn off the
    // flag, and send it an interrupt. Either one should work.
    mRunning = false;
    mThread.interrupt();
    setRunMode(RunMode.SIGNALED);
    super.signal();
  }
  
  protected boolean isRunning() {
    return mRunning;
  }
  
  protected abstract void doRun() throws InterruptedException;  
}
