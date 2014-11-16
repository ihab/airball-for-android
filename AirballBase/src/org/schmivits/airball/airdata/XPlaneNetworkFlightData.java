package org.schmivits.airball.airdata;

import org.schmivits.airball.airdata.XPlanePacketInterpreter.XPlaneUdpBlock;
import org.schmivits.airball.util.ValueModel;

public class XPlaneNetworkFlightData
        implements FlightData, XPlanePacketInterpreter.BlockReceiver {

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

    private Aircraft mAircraft;
    private float mAlpha;
    private float mBeta;
    private float mAirspeed;
    private float mAltitude;
    private float mClimbRate;
    private final NetworkListener mNetworkListener;

    public XPlaneNetworkFlightData(Aircraft aircraft, int port, boolean isUdp) {
        mAircraft = aircraft;
        XPlanePacketInterpreter packetInterpreter = new XPlanePacketInterpreter(this);
        mNetworkListener = isUdp
                ? new UdpListener(port, packetInterpreter)
                : new TcpListener(port, packetInterpreter);
        mNetworkListener.start();
    }

    private static final int SPEEDS = 3;
    private static final int SPEEDS_IAS = 0;

    private static final int MACHANDVERT = 4;
    private static final int MACHANDVERT_VERTICALSPEED = 2;

    private static final int AOASIDESLIP = 18;
    private static final int AOASIDESLIP_ALPHA = 0;
    private static final int AOASIDESLIP_BETA = 1;

    private static final int POSITION = 20;
    private static final int POSITION_ALTMSL = 2;

    @Override
    public void blockReceived(XPlaneUdpBlock block) {
        switch (block.mIndex) {
            case AOASIDESLIP:
                mAlpha = block.mData[AOASIDESLIP_ALPHA];
                mBeta = - block.mData[AOASIDESLIP_BETA];
                break;
            case MACHANDVERT:
                mClimbRate = block.mData[MACHANDVERT_VERTICALSPEED];
                break;
            case SPEEDS:
                mAirspeed = block.mData[SPEEDS_IAS];
                break;
            case POSITION:
                mAltitude = block.mData[POSITION_ALTMSL];
            default:
                return;
        }
        mUpdateSourceHelper.fire();
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
        mUpdateSourceHelper.addUpdateListener(r);
    }

    @Override
    public void removeUpdateListener(Runnable r) {
        mUpdateSourceHelper.removeUpdateListener(r);
    }

    @Override
    public void destroy() {
        mNetworkListener.stop();
    }
}
