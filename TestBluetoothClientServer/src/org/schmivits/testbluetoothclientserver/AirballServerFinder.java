package org.schmivits.testbluetoothclientserver;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;

public class AirballServerFinder extends RpcThread {

  private final BluetoothAdapter mAdapter;

  public AirballServerFinder(BluetoothAdapter adapter, TextView view) {
    super(view);
    report("Constructed AirballServerFinder");
    mAdapter = adapter;
  }

  @Override
  public void run() {
    Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
    report("Found " + pairedDevices.size() + " devices");
    for (BluetoothDevice device : pairedDevices) {
      report("Found  device " + device.getName() + " - " + device.getAddress() + " - " + device.getBluetoothClass());
      mChildren.add(new AirballListener(device, mView));
    }
    // Cancel discovery because it will slow down the connection
    mAdapter.cancelDiscovery();
    for (Lifecycle t : mChildren) { ((RpcThread) t).start(); }
  }
}