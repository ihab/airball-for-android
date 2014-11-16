package org.schmivits.airball.display.climbrate;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.widget.Container;
import org.schmivits.airball.widget.Rectangle;
import org.schmivits.airball.widget.Text;
import org.schmivits.airball.widget.Widget;

public class ClimbRateTapeCore extends Container {

    public interface Model {
        float getClimbRate();
    }

    private static final int INDICATOR_COLOR = Color.argb(255, 204, 102, 255);

    private final Model mModel;
    private final DisplayConfiguration mConfig;
    private final float mIndicatorThickness;
    private final Paint mIndicatorPaint;
    private final Typeface mTypeface;
    private final float mTapePixelsPerFpm;

    public ClimbRateTapeCore(DisplayConfiguration config, Resources res, AssetManager assets,
            float x, float y, float w, float h, Model model) {
        moveTo(x, y);
        sizeTo(w, h);

        mModel = model;
        mConfig = config;
        mIndicatorThickness = (float) Math.floor(w * 0.45);
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(INDICATOR_COLOR);
        mTypeface = Typeface.createFromAsset(assets, config.mTextTypeface);

        mTapePixelsPerFpm = (float) 0.9 * (getHeight() / 2) / 2000;

        addConstantItems(res);
    }

    private float getYCoordinate(float value) {
        return getHeight() / 2f - value * mTapePixelsPerFpm;
    }

    private void addTickMark(float thickness, float length, float textSize, float value,
            String text) {
        float y = getYCoordinate(value);
        float mTextLeftBoundaryFromLeft = (float) Math.floor(getWidth() * 0.7);
        Widget tick = new Rectangle(mConfig.mLineColor);
        tick.moveTo(0, y - thickness / 2f);
        tick.sizeTo(length, thickness);
        mChildren.add(tick);
        if (text != null) {
            Widget textWidget = new Text(text, textSize, mConfig.mTextColor, mTypeface);
            textWidget.moveTo(mTextLeftBoundaryFromLeft, y - textWidget.getHeight() / 2f);
            mChildren.add(textWidget);
        }
    }

    private void addConstantItems(Resources res) {
        float mTickMarkLength1000 = (float) Math.floor(getWidth() * 0.55);
        float mTickMarkLength500 = (float) Math.floor(getWidth() * 0.45);

        float mTextSize1000 = (float) Math.floor(getWidth() * 0.6);

        Widget line = new Rectangle(mConfig.mLineColor);
        line.moveTo(0f, 0f);
        line.sizeTo(mConfig.mThinLineThickness, getHeight());
        mChildren.add(line);

        for (int i = -2; i <= 2; i++) {
            addTickMark(
                    mConfig.mThickLineThickness,
                    mTickMarkLength1000,
                    mTextSize1000,
                    i * 1000f,
                    "" + Math.abs(i));
        }
        for (int i = -3; i <= 3; i += 2) {
            addTickMark(
                    mConfig.mThinLineThickness,
                    mTickMarkLength500,
                    0f,
                    i * 500f,
                    null);
        }
    }

    @Override
    protected void drawContents(Canvas canvas) {
        float[] yCoordinates = new float[]{
                getHeight() / 2f,
                getYCoordinate(mModel.getClimbRate()),
        };
        canvas.drawRect(
                mConfig.mThinLineThickness * 2f, Math.min(yCoordinates[0], yCoordinates[1]),
                mConfig.mThinLineThickness + mIndicatorThickness,
                Math.max(yCoordinates[0], yCoordinates[1]),
                mIndicatorPaint);
        super.drawContents(canvas);
    }
}
