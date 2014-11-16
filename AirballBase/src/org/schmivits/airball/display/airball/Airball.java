package org.schmivits.airball.display.airball;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import org.schmivits.airball.airdata.Aircraft;
import org.schmivits.airball.base.R;
import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.util.ValueModel;
import org.schmivits.airball.widget.Container;
import org.schmivits.airball.widget.TiledImage;
import org.schmivits.airball.widget.Widget;

public class Airball extends Container {

    public interface Model {
        ValueModel<Float> getAlpha();
        ValueModel<Float> getBeta();
        ValueModel<Float> getAirspeed();
        ValueModel<Aircraft> getAircraft();
    }

    private final DisplayConfiguration mConfig;
    private final Model mModel;
    private final Widget mAirball;
    private final Widget mErrorAirball;
    private final Widget mSpeedTooSmallAirball;
    private final Widget mTotemPole;
    private final Widget mErrorLineHorizontal;
    private final Widget mErrorLineVertical;
    private float mSpeedTooSmallAirballWidth;

    public Airball(DisplayConfiguration config, Model model) {
        mConfig = config;
        mModel = model;

        Bitmap errorTexture = BitmapFactory.decodeResource(
                mConfig.mResources, R.drawable.error_texture);

        boolean isValentines =
                Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 14;

        mChildren.add(mAirball = isValentines
                ? new AirballHeartCore(mConfig) : new AirballCore(mConfig));
        mChildren.add(mErrorAirball = new TiledImage(errorTexture));
        mChildren.add(mSpeedTooSmallAirball = new SpeedTooSmallAirball(config));
        mChildren.add(mTotemPole = new TotemPole(config, model.getAircraft().getValue()));
        mChildren.add(mErrorLineHorizontal = new TiledImage(errorTexture));
        mChildren.add(mErrorLineVertical = new TiledImage(errorTexture));
    }

    @Override
    public void onResize() {
        mSpeedTooSmallAirballWidth = getWidth() / 10f;
        mErrorAirball.sizeTo(mSpeedTooSmallAirballWidth / 2f, mSpeedTooSmallAirballWidth / 2f);
        mSpeedTooSmallAirball.sizeTo(mSpeedTooSmallAirballWidth, mSpeedTooSmallAirballWidth);
        mTotemPole.sizeTo(getWidth(), getHeight());
        mErrorLineHorizontal.sizeTo(getWidth(), mConfig.mCautionStripeThickness);
        mErrorLineVertical.setY(0);
        centerX(mErrorLineVertical);
        mErrorLineVertical.sizeTo(mConfig.mCautionStripeThickness, getHeight());
        center(mErrorLineVertical);
    }

    @Override
    public void drawContents(Canvas canvas) {
        Widget airballDisplayWidget;

        if (mModel.getAirspeed().isValid() && mModel.getAircraft().isValid()) {
            mErrorAirball.setVisible(false);
            float diameter =
                    (mModel.getAirspeed().getValue() / mModel.getAircraft().getValue().getVne()) *
                    getWidth();
            if (diameter < mSpeedTooSmallAirballWidth) {
                mAirball.setVisible(false);
                mSpeedTooSmallAirball.setVisible(true);
                airballDisplayWidget = mSpeedTooSmallAirball;
            } else {
                mAirball.sizeTo(diameter, diameter);
                mAirball.setVisible(true);
                mSpeedTooSmallAirball.setVisible(false);
                airballDisplayWidget = mAirball;
            }
        } else {
            mAirball.setVisible(false);
            mErrorAirball.setVisible(true);
            mSpeedTooSmallAirball.setVisible(false);
            airballDisplayWidget = mErrorAirball;
        }

        if (mModel.getAlpha().isValid() && mModel.getAircraft().isValid()) {
            mErrorLineVertical.setVisible(false);
            float y = Scaling.computeY(
                    mModel.getAlpha().getValue(),
                    mModel.getAircraft().getValue(),
                    getHeight());
            airballDisplayWidget.moveTo(
                    airballDisplayWidget.getX(),
                    y - (airballDisplayWidget.getHeight() / 2f));
        } else {
            mErrorLineVertical.setVisible(true);
            airballDisplayWidget.moveTo(
                    airballDisplayWidget.getX(),
                    (getHeight() - airballDisplayWidget.getHeight()) / 2f);
        }

        if (mModel.getBeta().isValid() && mModel.getAircraft().isValid()) {
            mErrorLineHorizontal.setVisible(false);
            float x = Scaling.computeX(
                    mModel.getBeta().getValue(),
                    mModel.getAircraft().getValue(),
                    getWidth());
            airballDisplayWidget.moveTo(
                    x - (airballDisplayWidget.getWidth() / 2f),
                    airballDisplayWidget.getY());
        } else {
            mErrorLineHorizontal.setVisible(true);
            airballDisplayWidget.moveTo(
                    (getWidth() - airballDisplayWidget.getWidth()) / 2f,
                    airballDisplayWidget.getY());
        }

        super.drawContents(canvas);
    }
}
