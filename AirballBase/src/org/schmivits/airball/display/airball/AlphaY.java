package org.schmivits.airball.display.airball;

import org.schmivits.airball.display.DisplayConfiguration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class AlphaY extends TotemPoleDecoration {

    private final Paint mPaint = new Paint();
    private final Path[] mPaths = { null, null };

    public AlphaY(DisplayConfiguration config) {
        super(config);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mConfig.mPointerColor);
        mPaint.setStrokeWidth(mConfig.mThinLineThickness);
    }

    @Override
    protected void computeSize() {
        sizeTo(mUnitSize * 20f, mUnitSize);
        float center = getWidth() / 2f;

        mPaths[0] = new Path();
        mPaths[0].moveTo(center -  8f * mUnitSize, mUnitSize);
        mPaths[0].lineTo(center -  9f * mUnitSize, mUnitSize);
        mPaths[0].lineTo(center - 10f * mUnitSize, 0f);

        mPaths[1] = new Path();
        mPaths[1].moveTo(center +  8f * mUnitSize, mUnitSize);
        mPaths[1].lineTo(center +  9f * mUnitSize, mUnitSize);
        mPaths[1].lineTo(center + 10f * mUnitSize, 0f);
    }


    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawPath(mPaths[0], mPaint);
        canvas.drawPath(mPaths[1], mPaint);
    }
}
