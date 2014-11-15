package com.google.airball.dataproxy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.airball.dataproxy.data.RootNode;
import com.google.airball.dataproxy.view.ProcessNodeViewFactory;

public class MainActivity extends Activity {

  private RootNode mRoot;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mRoot = new RootNode(this);
    ((ViewGroup) findViewById(R.id.activity_main_container)).addView(
        ProcessNodeViewFactory.make(mRoot, this));
    
    ((Button) findViewById(R.id.activity_main_serve_nothing)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        mRoot.serveNothing();
      }
    });

    ((Button) findViewById(R.id.activity_main_serve_actual_data)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        mRoot.serveAirballProxy();
      }
    });
    
    ((Button) findViewById(R.id.activity_main_serve_dummy_data)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        mRoot.serveAirballDummy();
      }
    });
  }
}
