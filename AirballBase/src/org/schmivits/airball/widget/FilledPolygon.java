package org.schmivits.airball.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class FilledPolygon extends Widget {

    private Path mPath;
    private final Paint mPaint;
    private final float[][] mPoints;

    public FilledPolygon(float[][] points, int color) {
        mPoints = points;
        makePath();

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onResize() {
        makePath();
    }

    private void makePath() {
        mPath = new Path();
        mPath.moveTo(mPoints[0][0] * getWidth(), mPoints[0][1] * getHeight());
        for (int i = 1; i < mPoints.length; i++) {
            mPath.lineTo(mPoints[i][0] * getWidth(), mPoints[i][1] * getHeight());
        }
        mPath.lineTo(mPoints[0][0] * getWidth(), mPoints[0][1] * getHeight());
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}
