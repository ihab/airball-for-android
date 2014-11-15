package com.example.testbluetoothclientserver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {

  private RpcThread mThread;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final TextView view = (TextView) findViewById(R.id.log);
    view.setMovementMethod(new ScrollingMovementMethod());

    ((Button) findViewById(R.id.client_server_off)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        destroy();
      }
    });

    ((Button) findViewById(R.id.act_as_client)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        destroy();
        (mThread = new AirballServerFinder(findBluetoothAdapter(), view)).start();
      }
    });

    ((Button) findViewById(R.id.act_as_server)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        destroy();
        (mThread = new AirballServer(findBluetoothAdapter(), view)).start();
      }
    });
    
    ((Button) findViewById(R.id.act_as_skyview_proxy)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        destroy();
        DataSourceStream stream = new ActualDataSourceStream(view, findBluetoothAdapter());
        ((Thread) stream).start();
        (mThread = new DataServer(view, findBluetoothAdapter(), stream)).start();
      }
    });
    
    ((Button) findViewById(R.id.act_as_skyview_dummy_source)).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        destroy();
        DataSourceStream stream = new DummyDataSourceStream(view);
        ((Thread) stream).start();
        (mThread = new DataServer(view, findBluetoothAdapter(), stream)).start();
      }
    });
  }

  @Override
  protected void onResume() {
    ((TextView) findViewById(R.id.log)).setText("");
    super.onResume();
  }

  @Override
  public void onPause() {
    destroy();
    ((RadioButton) findViewById(R.id.client_server_off)).setChecked(true);
    super.onPause();
  }

  private void destroy() {
    if (mThread != null) { mThread.destroy(); }
    mThread = null;
    ((TextView) findViewById(R.id.log)).setText("");
  }

  private BluetoothAdapter findBluetoothAdapter() {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    if (adapter == null) {
      adapter = (BluetoothAdapter) getSystemService("BLUETOOTH_SERVICE");
    }
    return adapter;
  }
}
