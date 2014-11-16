package org.schmivits.airball.airdata;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;

public class N42PEFlightData extends DynonSerialFlightData {
  
  private static final UUID AIRBALL_UUID = UUID.fromString("f4a9bf39-1970-4bc8-a297-7596b2dae998");

  private final static BetaModel.Config config = new BetaModel.Config();

  static {
    config.fullScaleBeta = 10f;  // degrees
    config.lateralArea = 30f;    // square feet
    config.weight = 1600f;       // pounds
    // NACA 0009 at http://galileo.phys.virginia.edu/classes/311/notes/aero/node4.html
    config.lateralClPerDegree = 0.11f;
  }

  public N42PEFlightData(BluetoothAdapter adapter) {
    super(
        new N42PEAircraft(),
        config,
        adapter, 
        AIRBALL_UUID);
  }
}
