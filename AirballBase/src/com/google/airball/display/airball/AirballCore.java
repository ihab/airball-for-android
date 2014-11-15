package com.google.airball.display.airball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.airball.display.DisplayConfiguration;
import com.google.airball.widget.Widget;

public class AirballCore extends Widget {

  private static final int AIRBALL_COLOR = Color.BLUE;

  private final DisplayConfiguration mConfig;
  private final Paint mAirballPaint = new Paint();
  
  public AirballCore(DisplayConfiguration config) {
    mConfig = config;
    mAirballPaint.setColor(AIRBALL_COLOR);
    mAirballPaint.setAntiAlias(true);
  }

  @Override protected void drawContents(Canvas canvas) {
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mAirballPaint);
    canvas.drawRect(
        0f, getHeight() / 2 - mConfig.mThinLineThickness / 2,
        getWidth(), getHeight() / 2 + mConfig.mThinLineThickness / 2,
        mConfig.mLinePaint);
    canvas.drawRect(
        getWidth() / 2 - mConfig.mThinLineThickness / 2, 0f,
        getWidth() / 2 + mConfig.mThinLineThickness / 2, getHeight(),
        mConfig.mLinePaint);
  }
}
