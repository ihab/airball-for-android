package org.schmivits.airball.airdata;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class N42PEAircraft implements Aircraft {

  public final static BetaModel.Config BETA_MODEL_CONFIG = new BetaModel.Config();

  static {
    BETA_MODEL_CONFIG.fullScaleBeta = 10f;  // degrees
    BETA_MODEL_CONFIG.lateralArea = 30f;    // square feet
    BETA_MODEL_CONFIG.weight = 1600f;       // pounds
    // NACA 0009 at http://galileo.phys.virginia.edu/classes/311/notes/aero/node4.html
    BETA_MODEL_CONFIG.lateralClPerDegree = 0.11f;
  }

  public final static SerialParameters D10A_SERIAL_PARAMETERS = new SerialParameters(
      PL2303Driver.BaudRate.B115200,
      PL2303Driver.DataBits.D8,
      PL2303Driver.StopBits.S1,
      PL2303Driver.Parity.NONE,
      PL2303Driver.FlowControl.OFF);

    @Override
    public float getVs0() {
        return 35f;
    }

    @Override
    public float getVs1() {
        return 45f;
    }

    @Override
    public float getVfe() {
        return 84f;
    }

    @Override
    public float getVno() {
        return 126f;
    }

    @Override
    public float getVne() {
        return 158f;
    }

    @Override
    public float getAs() {
        return 1.0f;
    }

    @Override
    public float getAmin() {
        return 0.0f;
    }

    @Override
    public float getAx() {
        return 0.7f;
    }

    @Override
    public float getAy() {
        return 0.75f;
    }

    @Override
    public float getAref() {
        return 0.9f;
    }

    @Override
    public float getBfs() {
        return 3.0f;
    }
}
