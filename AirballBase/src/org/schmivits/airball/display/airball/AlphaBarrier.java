package org.schmivits.airball.display.airball;

import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.widget.Widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AlphaBarrier extends Widget {

    private static final int COLOR = Color.RED;
    private final Paint mPaint = new Paint();

    public AlphaBarrier(DisplayConfiguration config) {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(COLOR);
        mPaint.setStrokeWidth(config.mThickLineThickness);
    }

    @Override
    protected void onResize() {
        setWidth(6f * getHeight());
    }

    @Override
    protected void drawContents(Canvas canvas) {
        float center = getWidth() / 2;
        for (int i = 0; i < 3; i++) {
            canvas.drawLine(
                    center - getHeight() * i, 0,
                    center - getHeight() * (i + 1), getHeight(),
                    mPaint);
            canvas.drawLine(
                    center + getHeight() * i, 0,
                    center + getHeight() * (i + 1), getHeight(),
                    mPaint);
        }
        canvas.drawLine(
                getHeight(), 0,
                getWidth() - getHeight(), 0,
                mPaint);
    }
}
