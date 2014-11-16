package org.schmivits.airball.airdata;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.schmivits.airball.util.ValueModel;

public class AccelerometerFlightData implements FlightData {

    private static final float MAX_ACCELERATION = 10f;

    private final UpdateSourceHelper mUpdateSourceHelper = new UpdateSourceHelper();

    private abstract class SimpleValueModel implements ValueModel<Float> {
        @Override
        public boolean isValid() {
            return true;
        }
    }

    private final ValueModel<Float> mAirspeedValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return mAirspeed;
        }
    };

    private final ValueModel<Float> mAlphaValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return mAlpha;
        }
    };

    private final ValueModel<Float> mBetaValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return mBeta;
        }
    };

    private final ValueModel<Float> mAltitudeValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return mAltitude;
        }
    };

    private final ValueModel<Float> mClimbRateValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return mClimbRate;
        }
    };

    private float getLateral(SensorEvent e) {
        return mIsLandscape ? -e.values[1] : e.values[0];
    }

    private float getLongitudinal(SensorEvent e) { return e.values[2]; }

    private final Airdata mAirdata = new Airdata() {
        @Override
        public ValueModel<Float> getAirspeed() {
            return mAirspeedValueModel;
        }

        @Override
        public ValueModel<Float> getAlpha() {
            return mAlphaValueModel;
        }

        @Override
        public ValueModel<Float> getBeta() {
            return mBetaValueModel;
        }

        @Override
        public ValueModel<Float> getAltitude() {
            return mAltitudeValueModel;
        }

        @Override
        public ValueModel<Float> getClimbRate() {
            return mClimbRateValueModel;
        }
    };

    private final ValueModel<Aircraft> mAircraftValueModel = new ValueModel<Aircraft>() {
        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Aircraft getValue() {
            return mAircraft;
        }
    };

    private final SensorEventListener mSensorAdapter = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mSensorEvents.add(event);
        }
    };

    private final Runnable mUpdateCycle = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    updateValues(mSensorEvents.take());
                } catch (InterruptedException e) {
                    // Ignore and continue
                }
            }
        }
    };

    private final Aircraft mAircraft;
    private final BlockingQueue<SensorEvent> mSensorEvents = new LinkedBlockingDeque<SensorEvent>();
    private final SensorManager mSensorManager;
    private final boolean mIsLandscape;

    private long mTimestamp = -1L;
    private float mAirspeed = 0f;
    private float mAlpha = 0f;
    private float mBeta = 0f;
    private float mClimbRate = 0f;
    private float mAltitude = 5000f;

    public AccelerometerFlightData(
            Aircraft aircraft,
            SensorManager sensorManager,
            boolean isLandscape) {
        mAircraft = aircraft;
        mIsLandscape = isLandscape;
        mSensorManager = sensorManager;
        sensorManager.registerListener(
                mSensorAdapter,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        new Thread(mUpdateCycle).start();
    }

    private void updateValues(SensorEvent e) {
        mAirspeed = scaledRange(getLongitudinal(e), 25f, 130f);
        mAlpha = scaledRange(getLongitudinal(e), 15f, 2f);
        mBeta = scaledSymmetric(-getLateral(e), 4f);
        if (mTimestamp != -1L) {
            mAltitude += mClimbRate
                    * (float) (e.timestamp - mTimestamp) / 1000f / 1000f / 1000f / 60f;
        }
        mClimbRate = scaledRange(getLongitudinal(e), 2000f, -2000f);
        mTimestamp = e.timestamp;
        mUpdateSourceHelper.fire();
    }

    @Override
    public void addUpdateListener(Runnable r) {
        synchronized (mUpdateSourceHelper) {
            mUpdateSourceHelper.addUpdateListener(r);
        }
    }

    @Override
    public void removeUpdateListener(Runnable r) {
        synchronized (mUpdateSourceHelper) {
            mUpdateSourceHelper.removeUpdateListener(r);
        }
    }

    @Override
    public ValueModel<Aircraft> getAircraft() {
        return mAircraftValueModel;
    }

    @Override
    public Airdata getAirdata() {
        return mAirdata;
    }

    @Override
    public String getConnectionStatus() {
        return null;
    }

    public void destroy() {
        mSensorManager.unregisterListener(mSensorAdapter);
    }

    private static float scaledSymmetric(float acceleration, float maxAbs) {
        return acceleration / MAX_ACCELERATION * maxAbs;
    }

    private static float scaledRange(float acceleration, float min, float max) {
        float ratio = ((acceleration / MAX_ACCELERATION + 1) / 2);
        return min + ratio * (max - min);
    }
}
