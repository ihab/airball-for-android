package com.google.airball.dataproxy.view;

import android.content.Context;

import com.google.airball.dataproxy.data.ActualDataSourceStream;
import com.google.airball.dataproxy.data.ProcessNode;

public class ProcessNodeViewFactory {
  
  public static ProcessNodeView make(ProcessNode node, Context context) {
    if (node instanceof ActualDataSourceStream) {
      return new ActualDataSourceStreamView((ActualDataSourceStream) node, context, null);
    } else {
      return new ProcessNodeView(node, context, null);
    }
  }
}
