package org.schmivits.dynonskyview;

import java.lang.reflect.Field;

import junit.framework.TestCase;

public class FormatTest extends TestCase {

  private void assertFloatEquals(String msg, float expected, float actual) {
    float error = (actual - expected) / expected;
    if (error > 0.0001f) {
      fail(msg + " expected <" + expected + "> but was <" + actual + ">");
    }
  }

  private void assertBlockEquals(ADAHRSDataBlock expected, ADAHRSDataBlock actual) {
    for (Field f : ADAHRSDataBlock.class.getFields()) {
      try {
        assertFloatEquals(
            "Field " + f.getName(),
            f.getFloat(expected), f.getFloat(actual));
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void assertEquivalent(ADAHRSDataBlock block, String word) {
    assertBlockEquals(block, DynonSerialFormat.wordToData(word));
    assertEquals(word, DynonSerialFormat.dataToWord(block));
  }

  public void testSimpleCorrectValuesA() {
    ADAHRSDataBlock block = new ADAHRSDataBlock();
    String word = "";

    block.time =
        ((39L * 3600L + 29L * 60L + 19L) * 1000L)
        + (long) (8.0 / 64.0 * 1000.0);
    word += "39291908";

    block.pitch = -78.9f;
    word += "-789";

    block.roll = -128.9f;
    word += "-1289";

    block.yaw = 259f;
    word += "259";

    block.airspeed = 179.5f;
    word += "1795";

    block.displayedAltitude = -8765f;
    word += "-8765";
    block.pressureAltitude = Float.NaN;

    block.turnRate = -87.9f;
    word += "-879";
    block.verticalSpeed = Float.NaN;

    block.lateralAcceleration = -0.79f;
    word += "-79";

    block.verticalAcceleration = -7.9f;
    word += "-79";

    block.angleOfAttack = 0.59f;
    word += "59";

    // Status
    word += "000000";

    // Internal use
    word += "00";

    word += DynonSerialFormat.makeChecksum(word);

    assertEquivalent(block, word);
  }

  public void testSimpleCorrectValuesB() {
    ADAHRSDataBlock block = new ADAHRSDataBlock();
    String word = "";

    block.time =
        ((39L * 3600L + 29L * 60L + 19L) * 1000L)
        + (long) (8.0 / 64.0 * 1000.0);
    word += "39291908";

    block.pitch = -78.9f;
    word += "-789";

    block.roll = -128.9f;
    word += "-1289";

    block.yaw = 259f;
    word += "259";

    block.airspeed = 179.5f;
    word += "1795";

    block.displayedAltitude = Float.NaN;
    block.pressureAltitude = -8765f;;
    word += "-8765";

    block.turnRate = Float.NaN;
    block.verticalSpeed = -87.9f;
    word += "-879";

    block.lateralAcceleration = -0.79f;
    word += "-79";

    block.verticalAcceleration = -7.9f;
    word += "-79";

    block.angleOfAttack = 0.59f;
    word += "59";

    // Status
    word += "000001";

    // Internal use
    word += "00";

    word += DynonSerialFormat.makeChecksum(word);

    assertEquivalent(block, word);
  }
}
