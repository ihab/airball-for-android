package com.google.airball.glass;

import android.bluetooth.BluetoothAdapter;

import com.google.airball.airdata.FlightData;
import com.google.airball.airdata.N42PEFlightData;

public class AirballServiceActualData extends AirballService {

  @Override protected FlightData getFlightData() {
    return new N42PEFlightData(findBluetoothAdapter());
  }
  
  private BluetoothAdapter findBluetoothAdapter() {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    if (adapter == null) {
      adapter = (BluetoothAdapter) getSystemService("BLUETOOTH_SERVICE");
    }
    return adapter;
  }
}
