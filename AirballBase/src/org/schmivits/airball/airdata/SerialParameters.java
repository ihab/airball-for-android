package org.schmivits.airball.airdata;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
* Created by simulator on 5/15/16.
*/
public class SerialParameters {
  public final PL2303Driver.BaudRate mBaudRate;
  public final PL2303Driver.DataBits mDataBits;
  public final PL2303Driver.StopBits mStopBits;
  public final PL2303Driver.Parity mParity;
  public final PL2303Driver.FlowControl mFlowControl;
  public SerialParameters(
      PL2303Driver.BaudRate baudRate,
      PL2303Driver.DataBits dataBits,
      PL2303Driver.StopBits stopBits,
      PL2303Driver.Parity parity,
      PL2303Driver.FlowControl flowControl) {
    mBaudRate = baudRate;
    mDataBits = dataBits;
    mStopBits = stopBits;
    mParity = parity;
    mFlowControl = flowControl;
  }
}
