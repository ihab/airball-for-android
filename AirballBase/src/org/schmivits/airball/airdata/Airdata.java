package org.schmivits.airball.airdata;

import org.schmivits.airball.util.ValueModel;

/**
 * Airdata and related information during flight.<br>
 * Each variable is annotated with information based on Dynon serial output; see:<br>
 * http://www.dynonavionics.com/downloads/User_Manuals/EFIS-D10A_Pilot's_User_Guide_Rev_K.pdf
 */
public interface Airdata {

  /**
   * Airspeed<br>
   * Indicated airspeed in knots<br>
   * 0 through 999
   */
  ValueModel<Float> getAirspeed();

  /**
   * Angle of attack<br>
   * Proportion of stall angle of attack<br>
   * 0 through 1
   */
  ValueModel<Float> getAlpha();

  /**
   * Angle of sideslip<br>
   * Proportion of full scale sideslip angle<br>
   * 0 through 1
   */
  ValueModel<Float> getBeta();

  /**
   * Altitude<br>
   * Actual measured altitude in feet<br>
   * 0 through 99000
   */
  ValueModel<Float> getAltitude();

  /**
   * Climb rate<br>
   * Climb rate in feet per minute<br>
   * -3000 through 3000
   */
  ValueModel<Float> getClimbRate();
}
