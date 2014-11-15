package com.google.dynonskyview;


public class DummyFlightData {

  private static interface Signal {
    public float computeValue(long currentTime);
  }

  private static Signal makeSignal(
      final long startTime,
      final long period,
      final float phase,
      final float min,
      final float max) {
    return new Signal() {
      @Override public float computeValue(long currentTime) {
        double x = (((double) currentTime - startTime) / period) + phase;
        float value = (float) ((1 + Math.sin(x)) * (max - min) / 2 + min);
        return value;
      }
    };
  }

  private static interface BlockUpdater {
    public void update(long currentTime, ADAHRSDataBlock block);
  }

  private final BlockUpdater mUpdater;

  public DummyFlightData(final long period, final long startTime) {
    final Signal airspeed = makeSignal(startTime, period, 0.0f, 18f, 86f);  // meters/second
    final Signal displayedAltitude = makeSignal(startTime, period, 0.2f, 1000f, 2000f);  // meters
    final Signal lateralAcceleration = makeSignal(startTime, period, 0.3f, -0.1f, 0.1f);  // g's
    final Signal angleOfAttack = makeSignal(startTime, period, 0.4f, 0f, 0.95f);  // ratio of critical
    mUpdater = new BlockUpdater() {
      @Override public void update(long currentTime, ADAHRSDataBlock block) {
        block.time = currentTime - startTime;
        block.pitch = Float.NaN;
        block.roll = Float.NaN;
        block.yaw = Float.NaN;
        block.airspeed = airspeed.computeValue(currentTime);
        block.displayedAltitude = displayedAltitude.computeValue(currentTime);
        block.pressureAltitude = Float.NaN;
        block.turnRate = 0f;
        block.verticalSpeed = Float.NaN;
        block.lateralAcceleration = lateralAcceleration.computeValue(currentTime);
        block.verticalAcceleration = Float.NaN;
        block.angleOfAttack = angleOfAttack.computeValue(currentTime);
      }
    };
  }

  public ADAHRSDataBlock getBlock(long currentTime) {
    ADAHRSDataBlock block = new ADAHRSDataBlock();
    mUpdater.update(currentTime, block);
    return block;
  }
}