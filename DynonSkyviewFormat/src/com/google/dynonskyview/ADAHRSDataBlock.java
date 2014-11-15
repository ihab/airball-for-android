package com.google.dynonskyview;

/**
 * Basic block of Dynon SkyView ADAHRS serial data, with minimal format conversions
 * for usability in programming but no further calculations applied.
 */
public class ADAHRSDataBlock {
	
  /**
   * Current Zulu time<br>
   * milliseconds<br>
   * [0, infinity)
   */
  public long time;

  /**
   * Pitch angle<br>
   * degrees<br>
   * [-90, 90]
   */
  public float pitch;

  /**
   * Roll angle<br>
   * degrees<br>
   * [-180, 180]
   */
  public float roll;

  /**
   * Yaw angle<br>
   * degrees<br>
   * [0, 359]
   */
  public float yaw;

  /**
   * Airspeed<br>
   * meters/second<br>
   * [0, 9999]
   */
  public float airspeed;

  /**
   * Displayed altitude<br>
   * meters<br>
   * [-99999, 99999]
   */
  public float displayedAltitude;

  /**
   * Pressure altitude<br>
   * meters<br>
   * [-99999, 99999]
   */
  public float pressureAltitude;

  /**
   * Turn rate (positive means right turn)<br>
   * degrees per second<br>
   * [-99.9, 99.9]
   */
  public float turnRate;

  /**
   * Vertical speed, positive upward<br>
   * feet per second<br>
   * [-99.0, 99.9]
   */
  public float verticalSpeed;

  /**
   * Lateral acceleration, positive leftward)<br>
   * g<br>
   * [-0.99, 0.99]
   */
  public float lateralAcceleration;

  /**
   * Vertical acceleration, positive upward<br>
   * g<br>
   * [-9.9, 9.9]
   */
  public float verticalAcceleration;

  /**
   * Angle of attack, proportion of critical value<br>
   * nondimensional<br>
   * [0, 0.99]
   */
  public float angleOfAttack;
}
