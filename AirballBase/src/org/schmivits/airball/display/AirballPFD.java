package org.schmivits.airball.display;

import android.content.res.AssetManager;
import android.content.res.Resources;

import org.schmivits.airball.airdata.Aircraft;
import org.schmivits.airball.airdata.FlightData;
import org.schmivits.airball.display.airball.Airball;
import org.schmivits.airball.display.altitude.AltitudeTape;
import org.schmivits.airball.display.climbrate.ClimbRateTape;
import org.schmivits.airball.display.speed.SpeedTape;
import org.schmivits.airball.util.ValueModel;
import org.schmivits.airball.widget.Container;
import org.schmivits.airball.widget.Widget;

public class AirballPFD extends Container {

    /**
     * Create an Airball PFD widget.
     */
    public AirballPFD(Resources resources, AssetManager assets, final FlightData model,
            final float width, final float height) {
        sizeTo(width, height);

        SpeedTape.Model speedTapeModel = new SpeedTape.Model() {
            @Override
            public ValueModel<Float> getSpeed() {
                return model.getAirdata().getAirspeed();
            }

            @Override
            public ValueModel<Aircraft> getAircraft() {
                return model.getAircraft();
            }
        };

        AltitudeTape.Model altitudeTapeModel = new AltitudeTape.Model() {
            @Override
            public ValueModel<Float> getAltitude() {
                return model.getAirdata().getAltitude();
            }
        };

        Airball.Model airballModel = new Airball.Model() {
            @Override
            public ValueModel<Float> getAlpha() {
                return model.getAirdata().getAlpha();
            }

            @Override
            public ValueModel<Float> getBeta() {
                return model.getAirdata().getBeta();
            }

            @Override
            public ValueModel<Float> getAirspeed() {
                return model.getAirdata().getAirspeed();
            }

            @Override
            public ValueModel<Aircraft> getAircraft() {
                return model.getAircraft();
            }
        };

        ClimbRateTape.Model climbRateTapeModel = new ClimbRateTape.Model() {
            @Override
            public ValueModel<Float> getClimbRate() {
                return model.getAirdata().getClimbRate();
            }
        };

        float instrumentGap = (float) Math.floor(width / 75);
        float altitudeTapeWidth = 0.175f * width;
        float speedTapeWidth = 0.125f * width;
        float climbRateTapeWidth = 0.05f * width;
        float airballWidth =
                getWidth() - speedTapeWidth - altitudeTapeWidth - climbRateTapeWidth
                        - 3 * instrumentGap;

        DisplayConfiguration config = new DisplayConfiguration(width, height, resources, assets);

        Widget airball;

        float x = 0f;
        mChildren.add(new SpeedTape(
                config, resources, assets,
                x, 0f,
                speedTapeWidth, getHeight(),
                speedTapeModel));
        x += speedTapeWidth + instrumentGap;
        mChildren.add(airball = new Airball(config, airballModel));
        airball.moveTo(x, 0f);
        airball.sizeTo(airballWidth, getHeight());
        x += airballWidth + instrumentGap;
        mChildren.add(new AltitudeTape(
                config, resources, assets,
                x, 0f,
                altitudeTapeWidth, getHeight(),
                altitudeTapeModel));
        x += altitudeTapeWidth + instrumentGap;
        mChildren.add(new ClimbRateTape(
                config, resources, assets,
                x, 0f,
                climbRateTapeWidth, getHeight(),
                climbRateTapeModel));
    }
}
