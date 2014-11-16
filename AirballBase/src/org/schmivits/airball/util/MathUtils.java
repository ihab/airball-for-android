package org.schmivits.airball.util;

public class MathUtils {

  public static double interpolate(
      double x0, double y0,
      double x1, double y1,
      double x) {
    return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
  }
    
  public static double interpolate(double[][] xyValues, double x) {
    if (x < xyValues[0][0] || x > xyValues[xyValues.length - 1][0]) {
      throw new RuntimeException("Value " + x + " out of range of known data");
    }
    for (int i = 0; i < xyValues.length - 1; i++) {
      if (x >= xyValues[i][0] && x <= xyValues[i + 1][0]) {
        return interpolate(
            xyValues[i][0], xyValues[i][1],
            xyValues[i + 1][0], xyValues[i + 1][1],
            x);
      }
    }
    throw new RuntimeException("Table of known data values malformed");
  }
}
