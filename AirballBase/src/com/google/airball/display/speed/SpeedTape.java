package com.google.airball.display.speed;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.google.airball.airdata.Aircraft;
import com.google.airball.base.R;
import com.google.airball.display.DisplayConfiguration;
import com.google.airball.util.ValueModel;
import com.google.airball.widget.Container;
import com.google.airball.widget.FilledPolygon;
import com.google.airball.widget.Rectangle;
import com.google.airball.widget.TiledImage;
import com.google.airball.widget.Widget;

public class SpeedTape extends Container {

    public interface Model {
        ValueModel<Float> getSpeed();
        ValueModel<Aircraft> getAircraft();
    }

    private final Model mModel;
    private final Widget mTape;
    private final Widget mInvalidImage;

    /**
     * Create a SpeedTape.
     */
    public SpeedTape(
            DisplayConfiguration config, Resources res, AssetManager assets,
            float x, float y, float w, float h,
            final Model model) {
        moveTo(x, y);
        sizeTo(w, h);

        mModel = model;

        float invalidWidth = (float) Math.floor(w / 8);

        mTape = new SpeedTapeCore(
                config,
                assets,
                0, 0,
                w - config.mPointerToScaleOffset, h,
                new SpeedTapeCore.Model() {
                    @Override
                    public float getSpeed() {
                        return model.getSpeed().isValid() ? model.getSpeed().getValue() : 0f;
                    }
                    @Override
                    public float getVs0() {
                        return model.getAircraft().isValid() ? model.getAircraft().getValue()
                                .getVs0() : 0f;
                    }
                    @Override
                    public float getVfe() {
                        return model.getAircraft().isValid() ? model.getAircraft().getValue()
                                .getVfe() : 0f;
                    }
                    @Override
                    public float getVs1() {
                        return model.getAircraft().isValid() ? model.getAircraft().getValue()
                                .getVs1() : 0f;
                    }
                    @Override
                    public float getVno() {
                        return model.getAircraft().isValid() ? model.getAircraft().getValue()
                                .getVno() : 0f;
                    }
                    @Override
                    public float getVne() {
                        return model.getAircraft().isValid() ? model.getAircraft().getValue()
                                .getVne() : 0f;
                    }
                });

        Widget pointerSymbol = new FilledPolygon(
                new float[][]{
                        {1.0f, 0.0f},
                        {0.0f, 0.5f},
                        {1.0f, 1.0f},
                },
                config.mPointerColor);
        pointerSymbol.moveTo(0f, 0f);
        pointerSymbol.sizeTo(config.mPointerShapeSize, config.mPointerShapeSize);

        Widget pointerLine = new Rectangle(config.mPointerColor);
        pointerLine.moveTo(0f, 0f);
        pointerLine.sizeTo(w, config.mThickLineThickness);

        pointerSymbol.moveTo(
                getWidth() - pointerSymbol.getWidth(),
                (getHeight() - pointerSymbol.getHeight()) / 2f);
        pointerLine.moveTo(
                getWidth() - pointerLine.getWidth(), (getHeight() - pointerLine.getHeight()) / 2f);

        mInvalidImage = new TiledImage(BitmapFactory.decodeResource(res, R.drawable.error_texture));
        mInvalidImage.moveTo(getWidth() - invalidWidth - config.mPointerToScaleOffset, 0f);
        mInvalidImage.sizeTo(invalidWidth, h);

        mInvalidImage.setVisible(false);
        mChildren.add(mTape);
        mChildren.add(mInvalidImage);
        mChildren.add(pointerSymbol);
        mChildren.add(pointerLine);
    }

    @Override
    protected void drawContents(Canvas canvas) {
        if (!mModel.getSpeed().isValid() || !mModel.getAircraft().isValid()) {
            mInvalidImage.setVisible(true);
            mTape.setVisible(false);
        } else {
            mInvalidImage.setVisible(false);
            mTape.setVisible(true);
        }
        super.drawContents(canvas);
    }
}
