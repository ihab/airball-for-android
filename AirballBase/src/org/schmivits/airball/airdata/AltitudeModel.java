package org.schmivits.airball.airdata;

import org.schmivits.airball.util.MathUtils;

public class AltitudeModel {

  // Data from http://www.sablesys.com/baro-altitude.html
  // A quick plot of the points shows that a linear interpolation between
  // them is adequately accurate
  private static final double[][] CORRECTION_TABLE = new double[][] {
    { 24.90,   5000, },
    { 25.37,   4500, },
    { 25.84,   4000, },
    { 26.33,   3500, },
    { 26.82,   3000, },
    { 27.32,   2500, },
    { 27.82,   2000, },
    { 28.33,   1500, },
    { 28.86,   1000, },
    { 29.38,    500, },
    { 29.92,      0, },
    { 30.47,   -500, },
    { 31.02,  -1000, },
    { 31.58,  -1500, },
    { 32.14,  -2000, },
    { 32.70,  -2500, },
    { 33.27,  -3000, },
    { 33.84,  -3500, },
    { 34.42,  -4000, },
    { 35.00,  -4500, },
    { 35.58,  -5000, },
  };

  /**
   * Return the indicated altitude under current conditions.
   * 
   * @param pressureAltitude pressure altitude in feet.
   * @param barometerSetting barometer setting in inches of Hg, e.g., 29.92.
   * @return indicated altitude in feet.
   */
  public static float computeAltitude(
      float pressureAltitude,
      float barometerSetting) {
    try {
      return (float) pressureAltitude 
          - (float) MathUtils.interpolate(CORRECTION_TABLE, barometerSetting);
    } catch (Exception e) {
      return Float.NaN;
    }
  }
}
