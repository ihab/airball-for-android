package com.google.airball.airdata;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.google.airball.util.ValueModel;
import com.google.dynonskyview.ADAHRSDataBlock;
import com.google.dynonskyview.DynonSerialFormat;

public class DynonSerialFlightData implements FlightData {

  private static final long KEEP_ALIVE_DELAY = 500L;  // milliseconds 

  private final UpdateSourceHelper mUpdateSourceHelper = new UpdateSourceHelper();
  private final ValueModel<Aircraft> mAircraftModel;
  private final BetaModel.Config mBetaModelConfig;
  private ADAHRSDataBlock mCurrentDataBlock;
  /** Current beta as a ratio of full scale, [-1.0, 1.0] */
  private float mCurrentBetaRatio;
  /** Current altitude in feet */
  private float mCurrentAltitude;
  private Timer mKeepAlive = null;
  private final Thread mThread;

  public DynonSerialFlightData(
      final Aircraft aircraft,
      BetaModel.Config config,
      BluetoothAdapter adapter,
      UUID bluetoothServiceUUID) {
    mBetaModelConfig = config;

    mAircraftModel = new ValueModel<Aircraft>() {
      @Override public boolean isValid() { return true; }
      @Override public Aircraft getValue() { return aircraft; }
    };

    updateKeepalive();
    addDataBlock(null);

    mThread = new DataScanThread(adapter, bluetoothServiceUUID, new DataScanThread.HaveData() {
      @Override public void line(final String line) {
        DynonSerialFlightData.this.addDataLine(line);
      }
      @Override public void status(String status) {
        // TODO(ihab)
      }
    });
    mThread.start();
  }

  @Override public void addUpdateListener(Runnable r) {
    synchronized (mUpdateSourceHelper) {
      mUpdateSourceHelper.addUpdateListener(r);
    }
  }

  @Override public void removeUpdateListener(Runnable r) {
    synchronized (mUpdateSourceHelper) {    
      mUpdateSourceHelper.removeUpdateListener(r);
    }
  }
  
  private void updateKeepalive() {
    if (mKeepAlive != null) {
      mKeepAlive.cancel();
      mKeepAlive = null;
    }
    mKeepAlive = new Timer();
    mKeepAlive.schedule(
        new TimerTask() {
          @Override public void run() {
            addDataBlock(null);
          }
        },
        KEEP_ALIVE_DELAY);
  }

  private void addDataLine(String line) {
    try {
      addDataBlock(DynonSerialFormat.wordToData(line));
      updateKeepalive();
    } catch (Exception e) {
      Log.v(getClass().getName(), e.toString());
      // Corrupt data; drop on the floor
      // If this goes on too long, our timeout will fire and invalidate UI
    }
  }

  private void addDataBlock(ADAHRSDataBlock block) {
    mCurrentDataBlock = block;

    if (block == null) {
      mCurrentAltitude = Float.NaN; 
    } else {
      if (!Float.isNaN(block.displayedAltitude)) {
        mCurrentAltitude = (float)
            (block.displayedAltitude / Constants.METERS_PER_FOOT);
      }
    }

    if (block == null ||
        Float.isNaN(block.lateralAcceleration) ||
        Float.isNaN(block.airspeed)) {
      // mCurrentBetaRatio = Float.NaN;
    } else {
        mCurrentBetaRatio = BetaModel.computeBetaRatio(
            mBetaModelConfig,
            block.lateralAcceleration,
            (float) (block.airspeed / Constants.METERS_PER_SECOND_PER_KNOT),
            mCurrentAltitude);
    }

    mUpdateSourceHelper.fire();
  }

  private abstract class AirdataValueModel implements ValueModel<Float> {
    @SuppressWarnings("unused") private final String mName;
    public AirdataValueModel(String name) { mName = name; }
    @Override public boolean isValid() {
      return !Float.isNaN(get());
    }
    @Override public Float getValue() { return isValid() ? get() : 0.0f; }
    private float get() { return (mCurrentDataBlock == null) ? Float.NaN : getFromBlock(); }
    protected abstract float getFromBlock();    
  }

  private final ValueModel<Float> mAirspeed = new AirdataValueModel("airspeed") {
    @Override public float getFromBlock() { return mCurrentDataBlock.airspeed; }
  };

  private final ValueModel<Float> mAlpha = new AirdataValueModel("angleOfAttack") {
    @Override public float getFromBlock() { return mCurrentDataBlock.angleOfAttack; }
  };

  private final ValueModel<Float> mBeta = new AirdataValueModel("beta") {
    @Override public float getFromBlock() { return mCurrentBetaRatio; }
  };

  private final ValueModel<Float> mAltitude = new AirdataValueModel("currentAltitude") {
    @Override public float getFromBlock() { return mCurrentAltitude; }
  };

  private final ValueModel<Float> mClimbRate = new AirdataValueModel("verticalSpeed") {
    @Override public float getFromBlock() { return mCurrentDataBlock.verticalSpeed; }
  };

  private final Airdata mAirdata = new Airdata() {
    @Override public ValueModel<Float> getAirspeed() { return mAirspeed; }
    @Override public ValueModel<Float> getAlpha() { return mAlpha; }
    @Override public ValueModel<Float> getBeta() { return mBeta; }
    @Override public ValueModel<Float> getAltitude() { return mAltitude; }
    @Override public ValueModel<Float> getClimbRate() { return mClimbRate; }
  };

  @Override
  public ValueModel<Aircraft> getAircraft() { return mAircraftModel; }

  @Override
  public Airdata getAirdata() { return mAirdata; }

  @Override
  public String getConnectionStatus() {
    return null;
  }

  @Override
  public void destroy() {
    mThread.interrupt();
  }
}
