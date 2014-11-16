package org.schmivits.airball.dataproxy.data;

import android.content.Context;

public class RootNode extends AbstractProcess {

  private ProcessNode mCurrentChild = null;
  private int mCount = 0;
  
  public RootNode(Context context) {
    super(context, null, "Root");
  }

  public void serveAirballProxy() {
    serveNothing();
    addChild(mCurrentChild = new AirballActualServer(getContext(), this, "" + mCount++));
  }
  
  public void serveAirballDummy() {
    serveNothing();    
    addChild(mCurrentChild = new AirballDummyServer(getContext(), this, "" + mCount++));    
  }
  
  public void serveNothing() {
    if (mCurrentChild != null) {
      mCurrentChild.signal();
      mCurrentChild = null;
    }
  }
  
  @Override
  public RunMode getRunMode() {
    return RunMode.RUNNING;
  }
  
  @Override
  public void signal() {
    serveNothing();
    super.signal();
  }
}
