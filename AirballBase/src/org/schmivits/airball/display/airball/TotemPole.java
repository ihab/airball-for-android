package org.schmivits.airball.display.airball;

import org.schmivits.airball.airdata.Aircraft;
import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.widget.Container;
import org.schmivits.airball.widget.Rectangle;
import org.schmivits.airball.widget.Widget;

import android.graphics.Paint;

public class TotemPole extends Container {

    private static final float UNIT_SIZE_PROPORTION = 0.025f;

    private final DisplayConfiguration mConfig;
    private final Aircraft mAircraft;
    private final Paint mPaint = new Paint();
    private final Widget mTopLine;
    private final Widget mBottomLine;
    private final Widget mAlphaBarrier;
    private final TotemPoleDecoration mLandingReference;
    private final TotemPoleDecoration mAlphaX;
    private final TotemPoleDecoration mAlphaY;

    public TotemPole(DisplayConfiguration config, Aircraft aircraft) {
        mConfig = config;
        mAircraft = aircraft;

        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(config.mPointerColor);
        mPaint.setStrokeWidth(mConfig.mThickLineThickness);

        mChildren.add(mTopLine = new Rectangle(mConfig.mPointerColor));
        mChildren.add(mBottomLine = new Rectangle(mConfig.mPointerColor));
        mChildren.add(mAlphaX = new AlphaX(config));
        mChildren.add(mAlphaY = new AlphaY(config));
        mChildren.add(mLandingReference = new LandingReference(config));
        mChildren.add(mAlphaBarrier = new AlphaBarrier(config));
    }

    @Override
    protected void onResize() {
        float unit = getHeight() * UNIT_SIZE_PROPORTION;

        mAlphaX.setUnitSize(unit);
        mAlphaX.setY(
                Scaling.computeY(mAircraft.getAx(), mAircraft, getHeight())
                        - mAlphaX.getHeight());
        centerX(mAlphaX);

        mAlphaY.setUnitSize(unit);
        mAlphaY.setY(
                Scaling.computeY(mAircraft.getAy(), mAircraft, getHeight())
                        - mAlphaY.getHeight());
        centerX(mAlphaY);

        mLandingReference.setUnitSize(unit);
        mLandingReference.setY(
                Scaling.computeY(mAircraft.getAref(), mAircraft, getHeight())
                        - mLandingReference.getHeight() / 2f);
        centerX(mLandingReference);

        float yAlphaCrit = Scaling.computeY(mAircraft.getAs(), mAircraft, getHeight());
        mAlphaBarrier.setHeight(getHeight() - yAlphaCrit);
        mAlphaBarrier.setY(yAlphaCrit);
        centerX(mAlphaBarrier);

        mTopLine.setY(0f);
        mTopLine.setHeight(mLandingReference.getY());
        mTopLine.setWidth(mConfig.mThickLineThickness);
        centerX(mTopLine);

        mBottomLine.setY(mLandingReference.getY() + mLandingReference.getHeight());
        mBottomLine.setHeight(mAlphaBarrier.getY() - mBottomLine.getY());
        mBottomLine.setWidth(mConfig.mThickLineThickness);
        centerX(mBottomLine);
    }
}
