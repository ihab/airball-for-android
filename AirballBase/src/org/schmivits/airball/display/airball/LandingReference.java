package org.schmivits.airball.display.airball;

import org.schmivits.airball.display.DisplayConfiguration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class LandingReference extends TotemPoleDecoration {

    private static final float ANGLE_OFFSET = 20f;

    private final Paint mPaint = new Paint();

    public LandingReference(DisplayConfiguration config) {
        super(config);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mConfig.mPointerColor);
        mPaint.setStrokeWidth(mConfig.mThinLineThickness);
    }

    @Override
    protected void computeSize() {
        sizeTo(mUnitSize * 2f, mUnitSize * 2f);
    }

    @Override
    protected void drawContents(Canvas canvas) {
        float sweep = 180f - 2 * ANGLE_OFFSET;
        RectF bounds = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(bounds, ANGLE_OFFSET, sweep, false, mPaint);
        canvas.drawArc(bounds, ANGLE_OFFSET - 180f, sweep, false, mPaint);
    }
}
