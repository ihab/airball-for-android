package org.schmivits.airball.temp;

public interface ProcessNode {

  interface ChangeListener {
    void statusChanged();
    void runModeChanged();
    void childrenChanged();
  }
  
  enum RunMode {
    NOT_STARTED,
    RUNNING,
    SIGNALED,
    FINISHED;
  }
  
  void addChangeListener(ChangeListener l);
  
  void removeChangeListener(ChangeListener l);
  
  ProcessNode[] getChildren();
  
  ProcessNode getParent();
  
  void cleanupChild(ProcessNode child);
  
  String getTitle();
  
  String getStatus();

  RunMode getRunMode();
  
  void signal();
}
