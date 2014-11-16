package org.schmivits.airball.display;

import org.schmivits.airball.airdata.Aircraft;
import org.schmivits.airball.airdata.FlightData;
import org.schmivits.airball.display.airball.Airball;
import org.schmivits.airball.util.ValueModel;
import org.schmivits.airball.widget.Container;
import org.schmivits.airball.widget.Widget;

import android.content.res.AssetManager;
import android.content.res.Resources;

public class AirballAnalog extends Container {

    /**
     * Create an Airball analog widget.
     */
    public AirballAnalog(Resources resources, AssetManager assets, final FlightData model,
            final float width, final float height) {
        sizeTo(width, height);

        Airball.Model airballModel = new Airball.Model() {
            @Override public ValueModel<Float> getAlpha() {
                return model.getAirdata().getAlpha();
            }
            @Override public ValueModel<Float> getBeta() {
                return model.getAirdata().getBeta();
            }
            @Override public ValueModel<Float> getAirspeed() {
                return model.getAirdata().getAirspeed();
            }
            @Override public ValueModel<Aircraft> getAircraft() {
                return model.getAircraft();
            }
        };

        DisplayConfiguration config = new DisplayConfiguration(width, height, resources, assets);
        Widget airball;
        mChildren.add(airball = new Airball(config, airballModel));
        airball.moveTo(0f, 0f);
        airball.sizeTo(width, height);
    }
}
