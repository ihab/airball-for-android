package org.schmivits.airball.display.airball;

import org.schmivits.airball.display.DisplayConfiguration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class AlphaX extends TotemPoleDecoration {

    private final Paint mPaint = new Paint();
    private final Path[] mPaths = { null, null };

    public AlphaX(DisplayConfiguration config) {
        super(config);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mConfig.mPointerColor);
        mPaint.setStrokeWidth(mConfig.mThinLineThickness);
    }

    @Override
    protected void computeSize() {
        sizeTo(mUnitSize * 12f, mUnitSize);
        float center = getWidth() / 2;

        mPaths[0] = new Path();
        mPaths[0].moveTo(center - 6f * mUnitSize, mUnitSize);
        mPaths[0].lineTo(center - 5f * mUnitSize, mUnitSize);
        mPaths[0].lineTo(center - 6f * mUnitSize, 0f);

        mPaths[1] = new Path();
        mPaths[1].moveTo(center + 6f * mUnitSize, mUnitSize);
        mPaths[1].lineTo(center + 5f * mUnitSize, mUnitSize);
        mPaths[1].lineTo(center + 6f * mUnitSize, 0f);
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawPath(mPaths[0], mPaint);
        canvas.drawPath(mPaths[1], mPaint);
    }
}
