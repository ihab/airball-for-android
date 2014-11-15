package com.google.airball.dataproxy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.airball.dataproxy.R;
import com.google.airball.dataproxy.data.ProcessNode;
import com.google.airball.dataproxy.data.ProcessNode.RunMode;

public class ProcessNodeView extends FrameLayout {

  private final ProcessNode mModel;

  private final ProcessNode.ChangeListener mChangeListener = new ProcessNode.ChangeListener() {
    @Override public void statusChanged() {
      post(new Runnable() {
        @Override public void run() {
          ((TextView) findViewById(R.id.process_node_view_status))
              .setText(mModel.getStatus());                    
        }
      });
    }
    @Override public void runModeChanged() {
      post(new Runnable() {
        @Override public void run() {          
          ((TextView) findViewById(R.id.process_node_view_runmode))
              .setText("" + mModel.getRunMode());
          updateRemoveEnabled();          
        }
      });
    }
    @Override public void childrenChanged() {
      final ProcessNode[] nodes = mModel.getChildren();
      post(new Runnable() {
        @Override public void run() {
          ViewGroup subviews = (ViewGroup) findViewById(R.id.process_node_view_subviews);
          subviews.removeAllViews();
          for (ProcessNode node : nodes) {
            subviews.addView(ProcessNodeViewFactory.make(node, getContext()));
          }
          updateRemoveEnabled();
        }
      });
    }
    void updateRemoveEnabled() {
      ((ImageButton) findViewById(R.id.process_node_view_remove)).setEnabled(
            mModel.getParent() != null &&
            mModel.getRunMode() == RunMode.FINISHED && 
            mModel.getChildren().length == 0);
    }
  };
  
  public ProcessNodeView(Context context, AttributeSet attrs) {
    this(null, context, attrs);
  }
  
  public ProcessNodeView(ProcessNode model, Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.process_node_view, this);
    mModel = model;    
    if (!isInEditMode()) {
      ((TextView) findViewById(R.id.process_node_view_title)).setText(
          mModel.getClass().getSimpleName() + " " + mModel.getTitle());
      mChangeListener.statusChanged();      
      mChangeListener.runModeChanged();
      mChangeListener.childrenChanged();
      model.addChangeListener(mChangeListener);
      ((ImageButton) findViewById(R.id.process_node_view_remove)).setOnClickListener(
          new OnClickListener() {
            @Override public void onClick(View view) {
              mModel.getParent().cleanupChild(mModel);
            }
          });
      addAdditional((ViewGroup) findViewById(R.id.process_node_view_additional));
    }
  }
  
  protected void addAdditional(ViewGroup parent) { }
  
  protected ProcessNode getModel() { return mModel; }
  
  @Override
  protected void onVisibilityChanged(View v, int flag) {
    if (flag == View.VISIBLE) {
      mModel.addChangeListener(mChangeListener);
    } else {
      mModel.removeChangeListener(mChangeListener);
    }
  }
}
