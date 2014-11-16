package org.schmivits.airball.airdata;

import org.schmivits.airball.util.ValueModel;

public class ConstantFlightData implements FlightData {

    private abstract class SimpleValueModel implements ValueModel<Float> {
        @Override
        public boolean isValid() {
            return true;
        }
    }

    private final ValueModel<Float> mAirspeedValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return 53.1f;
        }
    };

    private final ValueModel<Float> mAlphaValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return 0.35f;
        }
    };

    private final ValueModel<Float> mBetaValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return 0.15f;
        }
    };

    private final ValueModel<Float> mAltitudeValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return 13956f;
        }
    };

    private final ValueModel<Float> mClimbRateValueModel = new SimpleValueModel() {
        @Override
        public Float getValue() {
            return 456f;
        }
    };

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

    private final Aircraft mAircraft;

    public ConstantFlightData(Aircraft aircraft) {
        this.mAircraft = aircraft;
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

    @Override
    public void addUpdateListener(Runnable r) {
        r.run();
    }

    @Override
    public void removeUpdateListener(Runnable r) {
        r.run();
    }

    @Override
    public void destroy() {
    }
}
