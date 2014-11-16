package org.schmivits.dynonskyview;


public class DynonSerialFormat {

  private static void assertEquals(Object expected, Object actual) {
    if (!expected.equals(actual)) {
      throw new RuntimeException(
          "Expected <" + expected + "> not equal to actual <" + actual + ">");
    }
  }
  
  private static void assertTrue(boolean condition, String msg) {
    if (!condition) {
      throw new RuntimeException(msg);
    }
  }
  
  private static class Buffer {
    private String s;
    public Buffer(String s) { this.s = s; }
    public String fetch(int n) {
      String result = s.substring(0, n);
      s = s.substring(n);
      return result;
    }
    public int len() { return s.length(); }
  }

  private abstract static class Conversion<T> {
    protected final int mFieldLen;
    public Conversion(int fieldLen) {
      mFieldLen = fieldLen;
    }
    public void toWord(T value, StringBuilder sb) {
      int l0 = sb.length();
      doToWord(value, sb);
      assertEquals(mFieldLen, sb.length() - l0);
    }
    public T toValue(Buffer b) {
      int l0 = b.len();
      T value = doToValue(b);
      assertEquals(mFieldLen, l0 - b.len());
      return value;
    }
    protected abstract void doToWord(T value, StringBuilder sb);
    protected abstract T doToValue(Buffer b);
  }

  private static class FloatConversion extends Conversion<Float> {
    private final float mMultiplier;
    private final float mMin;
    private final float mMax;
    private final String mFormat;
    private final String mInvalidWord;
    private String x(int n) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < n; i++) { sb.append("X"); }
      return sb.toString();
    }
    public FloatConversion(int fieldLen, boolean signed, float multiplier, float min, float max) {
      super(fieldLen);
      mMultiplier = multiplier;
      mMin = min; mMax = max;
      mFormat = signed
          ? "%1$+0" + fieldLen + "d"
          : "%1$0" + fieldLen + "d";
      mInvalidWord = signed ? ("+" + x(fieldLen - 1)) : x(fieldLen);
    }
    private void check(float value) {
      assertTrue(value >= mMin, "Value " + value + " less than minimum " + mMin);
      assertTrue(value <= mMax, "Value " + value + " greater than maximum " + mMax);    
    }
    @Override protected void doToWord(Float value, StringBuilder sb) {
      if (Float.isNaN(value)) {
        sb.append(mInvalidWord);
      } else {
        check(value);        
        sb.append(String.format(mFormat, (int) Math.round(value / mMultiplier)));
      }
    }
    @Override protected Float doToValue(Buffer b) {
      String word = b.fetch(mFieldLen);
      if (mInvalidWord.equals(word)) {
        return Float.NaN;
      } else {
        float value = Float.parseFloat(word) * mMultiplier;
        check(value);
        return value;
      }
    }
  }

  private static class TimeConversion extends Conversion<Long> {
    public TimeConversion() { super(8); }
    @Override protected void doToWord(Long value, StringBuilder sb) {
      long hours = value / (3600 * 1000);
      value -= hours * (3600 * 1000);
      long minutes = value / (60 * 1000);
      value -= minutes * (60 * 1000);
      long seconds = value / 1000;
      value -= seconds * 1000;
      long sixtyfourths = (long) Math.round(value / 1000.0 * 64.0); 
      sb.append(String.format("%02d", hours));
      sb.append(String.format("%02d", minutes));
      sb.append(String.format("%02d", seconds));
      sb.append(String.format("%02d", sixtyfourths));
    }
    @Override protected Long doToValue(Buffer b) {
      double fractionalSeconds = 
          Integer.parseInt("" + b.fetch(2)) * 3600.0 +
          Integer.parseInt("" + b.fetch(2)) * 60.0 +
          Integer.parseInt("" + b.fetch(2)) +
          Integer.parseInt("" + b.fetch(2)) / 64.0; 
      return (long) (fractionalSeconds * 1000.0);
    }
  };
  
  private static class StatusConversion extends Conversion<Integer> {
    public StatusConversion() { super(6); }
    @Override protected void doToWord(Integer value, StringBuilder sb) {
      switch (value) {
      case 0:
        sb.append("000000");
        break;
      case 1:
        sb.append("000001");
        break;
      default:
        throw new RuntimeException("Illegal status bitmask value: " + value);
      }
    }
    @Override protected Integer doToValue(Buffer b) {
      return Integer.parseInt("" + b.fetch(6).charAt(5), 16) & 0x01;
    }
  }

  private static final TimeConversion mTime = new TimeConversion();
  private static final FloatConversion mPitch = new FloatConversion(4, true, 0.1f, -90f, 90f);
  private static final FloatConversion mRoll = new FloatConversion(5, true, 0.1f, -180f, 180f);  
  private static final FloatConversion mYaw = new FloatConversion(3, false, 1f, 0f, 359f);  
  private static final FloatConversion mAirspeed = new FloatConversion(4, false, 0.1f, 0f, 999.9f);
  private static final FloatConversion mDisplayedOrPressureAltitude = new FloatConversion(5, true, 1f, -99999f, 99999f);  
  private static final FloatConversion mTurnRateOrVerticalSpeed = new FloatConversion(4, true, 0.1f, -999f, 999f);
  private static final FloatConversion mLateralAcceleration = new FloatConversion(3, true, 0.01f, -0.99f, 0.99f);
  private static final FloatConversion mVerticalAcceleration = new FloatConversion(3, true, 0.1f, -9.9f, 9.9f);  
  private static final FloatConversion mAngleOfAttack = new FloatConversion(2, false, 0.01f, 0f, 0.99f);    
  private static final StatusConversion mStatus = new StatusConversion();
  
  private static class ChosenFloat {
	public int choice;
    public float value;
  }
  
  private static final ChosenFloat chooseOne(float a, float b, String fa, String fb) {
    ChosenFloat cf = new ChosenFloat();
    cf.choice = whichOne(a, b, fa, fb);
    cf.value = (cf.choice == 0) ? a : b;
    return cf;
  }
  
  private static final int whichOne(float a, float b, String fa, String fb) {
    boolean isA = Float.isNaN(a), isB = Float.isNaN(b);
    if (isA ^ isB) {
      return isA ? 1 : 0;
    } else {
      throw new RuntimeException("Must supply just one: " + fa + ", " + fb);
    }
  }

  private static final Conversion<ADAHRSDataBlock> mAdahrsConversion = new Conversion<ADAHRSDataBlock>(49) {
    @Override protected void doToWord(ADAHRSDataBlock block, StringBuilder sb) {
      mTime.toWord(block.time, sb);
      mPitch.toWord(block.pitch, sb);
      mRoll.toWord(block.roll, sb);
      mYaw.toWord(block.yaw, sb);
      mAirspeed.toWord(block.airspeed, sb);
      ChosenFloat displayedOrPressureAltitude = chooseOne(
          block.displayedAltitude, block.pressureAltitude,
          "displayedAltitude", "pressureAltitude");
      ChosenFloat turnRateOrVerticalSpeed = chooseOne(
          block.turnRate, block.verticalSpeed,
          "turnRate", "verticalSpeed");
      if (displayedOrPressureAltitude.choice != turnRateOrVerticalSpeed.choice) {
        throw new RuntimeException("Data block inconsistent; cannot encode");
      }
      mDisplayedOrPressureAltitude.toWord(displayedOrPressureAltitude.value, sb);
      mTurnRateOrVerticalSpeed.toWord(turnRateOrVerticalSpeed.value, sb);
      mLateralAcceleration.toWord(block.lateralAcceleration, sb);
      mVerticalAcceleration.toWord(block.verticalAcceleration, sb);
      mAngleOfAttack.toWord(block.angleOfAttack, sb);
      mStatus.toWord(displayedOrPressureAltitude.choice, sb);
      // Internal use
      sb.append("00");
    }
    @Override protected ADAHRSDataBlock doToValue(Buffer b) {
      ADAHRSDataBlock block = new ADAHRSDataBlock();
      block.time = mTime.toValue(b);
      block.pitch = mPitch.toValue(b);
      block.roll = mRoll.toValue(b);
      block.yaw = mYaw.toValue(b);
      block.airspeed = mAirspeed.toValue(b);
      float displayedOrPressureAltitude = mDisplayedOrPressureAltitude.toValue(b);
      float turnRateOrVerticalSpeed = mTurnRateOrVerticalSpeed.toValue(b);
      block.lateralAcceleration = mLateralAcceleration.toValue(b);
      block.verticalAcceleration = mVerticalAcceleration.toValue(b);
      block.angleOfAttack = mAngleOfAttack.toValue(b);
      int status = mStatus.toValue(b);
      block.displayedAltitude = (status == 0)
          ? displayedOrPressureAltitude : Float.NaN;
      block.pressureAltitude = (status == 0)
          ? Float.NaN : displayedOrPressureAltitude;
      block.turnRate = (status == 0)
          ? turnRateOrVerticalSpeed : Float.NaN;
      block.verticalSpeed = (status == 0)
          ? Float.NaN : turnRateOrVerticalSpeed;
      // Internal use
      b.fetch(2);
      return block;
    }
  };

  // package private for testing
  static String makeChecksum(String s) {
    byte b = 0;
    for (int i = 0; i < s.length(); i++) {
      b += s.charAt(i);
    }
    return String.format("%02X", b);
  }
  
  private static void checkChecksum(String word) {
    assertEquals(makeChecksum(word.substring(0, 49)), word.substring(49, 51));
  }
  
  public static String dataToWord(ADAHRSDataBlock data) {
    StringBuilder sb = new StringBuilder();
    mAdahrsConversion.toWord(data, sb);        
    return sb.toString() + makeChecksum(sb.toString());
  }

  public static ADAHRSDataBlock wordToData(String word) {
    assertEquals(51, word.length());
    checkChecksum(word);
    return mAdahrsConversion.toValue(new Buffer(word.substring(0, 49)));
  }
}
