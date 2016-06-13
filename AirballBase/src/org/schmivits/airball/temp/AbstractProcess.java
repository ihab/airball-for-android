package org.schmivits.airball.temp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProcess implements ProcessNode {

  private final List<ChangeListener> mListeners = new ArrayList<ChangeListener>();
  private final List<ProcessNode> mChildren = new ArrayList<ProcessNode>();
  private final Context mContext;
  private final ProcessNode mParent;
  private final String mTitle;  
  private RunMode mRunMode = RunMode.NOT_STARTED;
  private String mStatus = "";

  protected AbstractProcess(Context context, ProcessNode parent, String title) {
    mContext = context;
    mParent = parent;
    mTitle = title;
  }
  
  protected void setStatus(String status) {
    mStatus = status;
    for (ChangeListener l : mListeners) {
      l.statusChanged();
    }
  }

  @Override  
  public String getTitle() {
    return mTitle;
  }
  
  @Override
  public String getStatus() {
    return mStatus;
  }
  
  @Override
  public RunMode getRunMode() {
    return mRunMode;
  }
  
  @Override  
  public void addChangeListener(ChangeListener l) {
    mListeners.add(l);
  }
  
  @Override  
  public void removeChangeListener(ChangeListener l) {
    mListeners.remove(l);
  }
  
  @Override
  public ProcessNode[] getChildren() {
    return mChildren.toArray(new ProcessNode[0]);
  }

  @Override
  public void cleanupChild(ProcessNode child) {
    if (mChildren.contains(child)) {
      if (child.getRunMode() == RunMode.FINISHED && child.getChildren().length == 0) {
        removeChild(child);
      }
    }
  }
  
  @Override
  public ProcessNode getParent() {
    return mParent;
  }
  
  @Override  
  public void signal() {
    for (ProcessNode t : mChildren) { t.signal(); }
  }
  
  protected final void addChild(ProcessNode child) {
    mChildren.add(child);
    for (ChangeListener l : mListeners) {
      l.childrenChanged();
    }
  }
  
  protected final void removeChild(ProcessNode child) {
    mChildren.remove(child);
    for (ChangeListener l : mListeners) {
      l.childrenChanged();
    }
  }
  
  protected final void setRunMode(RunMode runMode) {
    mRunMode = runMode;
    for (ChangeListener l : mListeners) {
      l.runModeChanged();
    }
  }
  
  protected final Context getContext() {
    return mContext;
  }
  
  protected BluetoothAdapter findBluetoothAdapter() {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    if (adapter == null) {
      adapter = (BluetoothAdapter) mContext.getSystemService("BLUETOOTH_SERVICE");
    }
    return adapter;
  }
}
