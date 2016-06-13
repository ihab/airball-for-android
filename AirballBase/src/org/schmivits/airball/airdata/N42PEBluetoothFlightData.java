package org.schmivits.airball.airdata;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;

public class N42PEBluetoothFlightData extends DynonSerialFlightData {
  
  private static final UUID AIRBALL_UUID = UUID.fromString("f4a9bf39-1970-4bc8-a297-7596b2dae998");

  private final Thread mThread;

  public N42PEBluetoothFlightData(BluetoothAdapter adapter) {
    super(new N42PEAircraft(), N42PEAircraft.BETA_MODEL_CONFIG);

    mThread = new DataScanThread(adapter, AIRBALL_UUID, new HaveData() {
        @Override public void line(final String line) {
            N42PEBluetoothFlightData.this.addDataLine(line);
        }
        @Override public void status(String status) {
            // TODO(ihab)
        }
    });

    mThread.start();
  }

  @Override
  public void destroy() {
    mThread.interrupt();
  }
}
