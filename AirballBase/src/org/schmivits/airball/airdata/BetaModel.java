package org.schmivits.airball.airdata;

import static org.schmivits.airball.airdata.Constants.GRAVITATIONAL_ACCELERATION;
import static org.schmivits.airball.airdata.Constants.METERS_PER_FOOT;
import static org.schmivits.airball.airdata.Constants.METERS_PER_SECOND_PER_KNOT;
import static org.schmivits.airball.airdata.Constants.NEWTONS_PER_POUND;
import static org.schmivits.airball.airdata.Constants.SQUARE_METERS_PER_SQUARE_FOOT;
import static org.schmivits.airball.airdata.Constants.STANDARD_AIR_DENSITY;

import org.schmivits.airball.util.MathUtils;

public class BetaModel {
  
  public static class Config {
    // Lateral area in square feet
    public float lateralArea;
    // Lateral lift coefficient per degree
    public float lateralClPerDegree;
    // Aircraft weight in pounds
    public float weight;
    // Full scale beta, in degrees
    public float fullScaleBeta;
  }

  // http://upload.wikimedia.org/wikipedia/commons/d/dc/StandardAtmosphere.png
  // At 10,000 meters, the density is about 0.35 times its value at sea level
  // The curve between them can be approximated *very* roughly as a straight line
  private static double computeDensity(double densityAltitudeInFeet) {
    double densityAltitude = densityAltitudeInFeet * METERS_PER_FOOT;
    double ratio = MathUtils.interpolate(
        0, 1,         // density ratio = 1 at surface 
        10000, 0.34,  // density ratio = 0.34 at 10,000 meters
        densityAltitude);
    return ratio * STANDARD_AIR_DENSITY;
  }
  
  /**
   * Return the proportion of full scale beta under current conditions.
   * 
   * @param config a BetaModelConfig
   * @param lateralAcceleration lateral acceleration in "g".
   * @param indicatedAirspeed IAS in knots.
   * @param densityAltitude density altitude in feet.
   * @return beta as a proportion of full scale value.
   */
  public static float computeBetaRatio(
      BetaModel.Config config,
      float lateralAcceleration,
      float indicatedAirspeed,
      float densityAltitude) {
    double weight = config.weight * NEWTONS_PER_POUND;
    double acceleration = lateralAcceleration * GRAVITATIONAL_ACCELERATION;
    double area = config.lateralArea * SQUARE_METERS_PER_SQUARE_FOOT;
    double speed = indicatedAirspeed * METERS_PER_SECOND_PER_KNOT;
    double density = computeDensity(densityAltitude);
    double g = GRAVITATIONAL_ACCELERATION;
    double clBeta = config.lateralClPerDegree;
    double beta = 
        (2.0 * weight * acceleration)
        / (area * speed * speed * density * g * clBeta);
    double betaRatio = beta / config.fullScaleBeta;
    return (float) betaRatio;
  }
}
