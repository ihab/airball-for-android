package com.google.airball.display.airball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.google.airball.display.DisplayConfiguration;
import com.google.airball.widget.Widget;

public class SpeedTooSmallAirball extends Widget {

    private final Paint mPaint = new Paint();
    private final DisplayConfiguration mConfig;

    public SpeedTooSmallAirball(DisplayConfiguration config) {
        mConfig = config;
        mPaint.setColor(config.mLineColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(config.mThickLineThickness);
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawCircle(
                getWidth() / 2f, getHeight() / 2f,
                getWidth() / 2f - mConfig.mThickLineThickness / 2f,
                mPaint);
    }
}
