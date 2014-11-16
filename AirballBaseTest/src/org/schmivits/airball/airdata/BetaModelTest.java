package org.schmivits.airball.airdata;

import junit.framework.TestCase;
import android.util.Log;

public class BetaModelTest extends TestCase {

  private static final BetaModel.Config config = new BetaModel.Config();
  static {
    config.fullScaleBeta = 10f;  // degrees
    config.lateralArea = 30f;    // square feet
    config.weight = 1600f;       // pounds
    // NACA 0009 at http://galileo.phys.virginia.edu/classes/311/notes/aero/node4.html
    config.lateralClPerDegree = 0.11f;
  }
 
  public void testCommonValues() {
    float betaRatio;
    // At 0.2g and 110 kIAS, we expect small angles, about 2.4 degrees of slip
    betaRatio = BetaModel.computeBetaRatio(
        config,
        0.2f,   // g
        110f,   // kIAS
        1500f); // feet
    Log.v("", "betaRatio = " + betaRatio);    
    assertTrue(betaRatio > 0.23f);
    assertTrue(betaRatio < 0.25f);
    // At 0.4g, we expect double the angle
    betaRatio = BetaModel.computeBetaRatio(
        config,
        0.4f,   // g
        110f,   // kIAS
        1500f); // feet
    Log.v("", "betaRatio = " + betaRatio);
    assertTrue(betaRatio > 0.46f);
    assertTrue(betaRatio < 0.50f);
    // At 0.2g but only 35 knots, we expect a huge angle
    betaRatio = BetaModel.computeBetaRatio(
        config,
        0.2f,   // g
        35f ,   // kIAS
        1500f); // feet
    Log.v("", "betaRatio = " + betaRatio);
    assertTrue(betaRatio > 2.3f);
    assertTrue(betaRatio < 2.5f);
  }  
}
