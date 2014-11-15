package com.google.airball.display.speed;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;

import com.google.airball.display.DisplayConfiguration;
import com.google.airball.widget.Widget;

public class SpeedTapeCore extends Widget {

  public interface Model {
    float getSpeed();
    float getVs0();  // Bottom of white arc
    float getVfe();  // Top of white arc
    float getVs1();  // Bottom of green arc
    float getVno();  // Top of green / bottom of yellow arc
    float getVne();  // Top of yellow / bottom of red arc
  }

  // The below constants, while they may seem silly ;), are useful for
  // tweaking exact RGB values if necessary
  private static final int WHITE_ARC_COLOR = Color.WHITE;
  private static final int GREEN_ARC_COLOR = Color.GREEN;
  private static final int YELLOW_ARC_COLOR = Color.YELLOW;
  private static final int RED_ARC_COLOR = Color.RED;

  private static final float TEXT_BASELINE_TO_CENTER_FACTOR = 0.375f;

  private final DisplayConfiguration mConfig;
  private final Model mModel;

  private final float mVArcRightBoundaryFromRight;
  private final float mTapePixelsPerKnot;
  private final float mTickMarkFivesLength;
  private final float mTickMarkTensLength;
  private final float mTextSize;
  private final float mTextRightBoundaryFromRight;
  private final float mVArcThickness;
  private final Paint mTextPaint = new Paint();
  private final Paint mWhiteArcPaint = new Paint();
  private final Paint mGreenArcPaint = new Paint();
  private final Paint mYellowArcPaint = new Paint();
  private final Paint mRedArcPaint = new Paint();

  public SpeedTapeCore(
      DisplayConfiguration config,
      AssetManager assets, 
      float x, float y, float w, float h,
      Model model) {
    super(x, y, w, h);

    mConfig = config;
    mModel = model;

    mVArcThickness = (float) Math.floor(w / 12);
    mVArcRightBoundaryFromRight = 2f * mConfig.mThinLineThickness;
    mTapePixelsPerKnot = (float) Math.floor(w / 10);
    mTickMarkFivesLength = (float) Math.floor(3 * mVArcThickness);
    mTickMarkTensLength = (float) Math.floor(3.25 * mVArcThickness);
    mTextSize = (float) Math.floor(w / 2.5);
    mTextRightBoundaryFromRight = (float) Math.floor(3.75 * mVArcThickness);

    Typeface tf = Typeface.createFromAsset(assets, mConfig.mTextTypeface);

    mTextPaint.setColor(mConfig.mTextColor);
    mTextPaint.setTypeface(tf);
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setTextAlign(Align.RIGHT);
    mTextPaint.setAntiAlias(true);

    mWhiteArcPaint.setColor(WHITE_ARC_COLOR);
    mGreenArcPaint.setColor(GREEN_ARC_COLOR);
    mYellowArcPaint.setColor(YELLOW_ARC_COLOR);
    mRedArcPaint.setColor(RED_ARC_COLOR);
  }

  float speedToCanvasPosition(float speed) {
    return getHeight() / 2 + (speed - mModel.getSpeed()) * mTapePixelsPerKnot;
  }

  private void drawSpeed(Canvas canvas, int speed5) {
    if (speed5 < 0) { return; }

    int speed = speed5 * 5;
    float y = speedToCanvasPosition(speed);
    boolean isTens = ((speed % 10f) == 0);

    if (isTens) {
      canvas.drawText(
          "" + speed,
          getWidth() - mTextRightBoundaryFromRight, y + mTextSize * TEXT_BASELINE_TO_CENTER_FACTOR,
          mTextPaint);
      canvas.drawRect(
          getWidth() - mTickMarkTensLength, y - mConfig.mThickLineThickness / 2,
          getWidth(), y + mConfig.mThickLineThickness / 2,
          mConfig.mLinePaint);
    } else {
      canvas.drawRect(
          getWidth() - mTickMarkFivesLength, y - mConfig.mThinLineThickness / 2,
          getWidth(), y + mConfig.mThinLineThickness / 2,
          mConfig.mLinePaint);
    }
  }

  private void drawScaleLine(Canvas canvas) {
    float yMin = Math.max(0, speedToCanvasPosition(0f));
    canvas.drawRect(
        getWidth() - mConfig.mThinLineThickness, yMin - mConfig.mThinLineThickness / 2,
        getWidth(), getHeight(),
        mConfig.mLinePaint);
  }

  private void drawVArcs(Canvas canvas) {
    canvas.drawRect(
        getWidth() - mVArcRightBoundaryFromRight - 2 * mVArcThickness, speedToCanvasPosition(mModel.getVs0()),
        getWidth() - mVArcRightBoundaryFromRight - mVArcThickness, speedToCanvasPosition(mModel.getVfe()),
        mWhiteArcPaint);
    canvas.drawRect(
        getWidth() - mVArcRightBoundaryFromRight - mVArcThickness, speedToCanvasPosition(mModel.getVs1()),
        getWidth() - mVArcRightBoundaryFromRight, speedToCanvasPosition(mModel.getVno()),
        mGreenArcPaint);
    canvas.drawRect(
        getWidth() - mVArcRightBoundaryFromRight - mVArcThickness, speedToCanvasPosition(mModel.getVno()),
        getWidth() - mVArcRightBoundaryFromRight, speedToCanvasPosition(mModel.getVne()),
        mYellowArcPaint);
    canvas.drawRect(
        getWidth() - mVArcRightBoundaryFromRight - mVArcThickness, speedToCanvasPosition(mModel.getVne()),
        getWidth() - mVArcRightBoundaryFromRight, getHeight(),
        mRedArcPaint);
  }

  @Override
  protected void drawContents(Canvas canvas) {
    drawScaleLine(canvas);
    drawVArcs(canvas);
    int speed5AtTop = (int)
            Math.floor((mModel.getSpeed() - getHeight() / 2 / mTapePixelsPerKnot) / 5)
            - 1;
    int speed5AtBottom = (int)
            Math.ceil((mModel.getSpeed() + getHeight() / 2 / mTapePixelsPerKnot) / 5)
            + 1;
    for (int i = speed5AtTop; i <= speed5AtBottom; i++) {
      drawSpeed(canvas, i);
    }
  }
}
