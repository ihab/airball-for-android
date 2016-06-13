package org.schmivits.airball.airdata;

import android.content.Context;

/**
 * A N42PEUARTFlightData assumes the Android device is connected to a Prolific USB-to-serial
 * interface that is in turn connected to the RS-232 output port of a Dynon D10A.
 */
public class N42PEUARTFlightData extends DynonSerialFlightData {

  DynonUARTDataSource mDataSource;

  public N42PEUARTFlightData(Context context) {
    super(new N42PEAircraft(), N42PEAircraft.BETA_MODEL_CONFIG);
    mDataSource = new DynonUARTDataSource(
        context,
        N42PEAircraft.D10A_SERIAL_PARAMETERS,
        new HaveData() {
          @Override
          public void line(String line) {
            N42PEUARTFlightData.this.addDataLine(line);
          }

          @Override
          public void status(String status) {
            // TODO(ihab): unimplemented
          }
        });
  }

  @Override public void destroy() {
    mDataSource.destroy();
  }
}
