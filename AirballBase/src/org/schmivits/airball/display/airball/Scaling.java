package org.schmivits.airball.display.airball;

import org.schmivits.airball.airdata.Aircraft;

public class Scaling {

    public static float computeY(
            float alpha,
            Aircraft aircraft,
            float widgetHeight) {
        float ratio = (alpha - aircraft.getAmin()) / (aircraft.getAs() - aircraft.getAmin());
        return widgetHeight * (0.1f + 0.8f * ratio);
    }

    public static float computeX(
            float beta,
            Aircraft aircraft,
            float widgetWidth) {
        return widgetWidth * (1f + beta / aircraft.getBfs()) / 2f;
    }
}
