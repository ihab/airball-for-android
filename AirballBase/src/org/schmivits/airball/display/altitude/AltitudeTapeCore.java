package org.schmivits.airball.display.altitude;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.widget.Widget;

public class AltitudeTapeCore extends Widget {

  public interface Model {
    float getAltitude();
  }

  private final DisplayConfiguration mConfig;
  private final Model mModel;
  private final Paint mTextThousandsPaint = new Paint();
  private final Paint mTextHundredsPaint = new Paint();

  private final float mTapePixelsPerFoot;
  private final float mTextThousandsSize;
  private final float mTextHundredsSize;
  private final float mTickMarkTwentiesLength;
  private final float mTickMarkHundredsLength;
  private final float mTickMarkThousandsLength;
  private final float mThousandsEmphasisLineDistanceFromText;
  private final float mThousandsEmphasisLineDistanceFromLeft;
  private final float mTextThousandsRightBoundary;
  private final float mTextHundredsLeftBoundary;

  public AltitudeTapeCore(
      DisplayConfiguration config, AssetManager assets,
      float x, float y, float w, float h,
      Model model) {
    super(x, y, w, h);
    mConfig = config;
    mModel = model;

    mTapePixelsPerFoot = (float) Math.floor(w / 100);

    mTextThousandsSize = (float) Math.floor(w / 3.25);
    mTextHundredsSize = (float) Math.floor(w / 4.5);

    mTickMarkTwentiesLength = (float) Math.floor(w / 10);
    mTickMarkHundredsLength = (float) Math.floor(mTickMarkTwentiesLength * 1.5);
    mTickMarkThousandsLength = (float) Math.floor(mTickMarkTwentiesLength * 2);

    mThousandsEmphasisLineDistanceFromText = (float) Math.floor(mTextThousandsSize / 10);
    mThousandsEmphasisLineDistanceFromLeft = (float) Math.floor(mTickMarkTwentiesLength * 2.5);

    mTextThousandsRightBoundary = (float) Math.floor(
        mThousandsEmphasisLineDistanceFromLeft + mTextThousandsSize * 1.125);
    mTextHundredsLeftBoundary = mTextThousandsRightBoundary + (float) Math.floor(mTextThousandsSize / 10);

    Typeface tf = Typeface.createFromAsset(assets, mConfig.mTextTypeface);

    mTextThousandsPaint.setColor(mConfig.mTextColor);
    mTextThousandsPaint.setTypeface(tf);
    mTextThousandsPaint.setTextSize(mTextThousandsSize);
    mTextThousandsPaint.setTextAlign(Align.RIGHT);
    mTextThousandsPaint.setAntiAlias(true);

    mTextHundredsPaint.setColor(mConfig.mTextColor);
    mTextHundredsPaint.setTypeface(tf);
    mTextHundredsPaint.setTextSize(mTextHundredsSize);
    mTextHundredsPaint.setTextAlign(Align.LEFT);
    mTextHundredsPaint.setAntiAlias(true);
  }

  float altitudeToCanvasPosition(float alt) {
    return (getHeight() / 2f) - (alt - mModel.getAltitude()) * mTapePixelsPerFoot;
  }

  private void drawAltitude(Canvas canvas, int alt20) {
    if (alt20 < 0) { return; }

    float altitude = alt20 * 20f;
    float y = altitudeToCanvasPosition(altitude);
    boolean isHundreds = ((altitude % 100f) == 0);
    boolean isThousands = ((altitude % 1000f) == 0);

    if (isThousands || isHundreds) {
      int thousands = (int) (altitude / 1000);
      int hundreds = (int) Math.floor((altitude - (thousands * 1000)) / 100) * 100;
      String thousandsString = thousands == 0 ? "" : "" + thousands;
      String hundredsString = hundreds == 0 ? "000" : "" + hundreds;
      canvas.drawText(thousandsString, mTextThousandsRightBoundary, y + 0.35f * mTextThousandsSize, mTextThousandsPaint);
      canvas.drawText(hundredsString, mTextHundredsLeftBoundary, y + 0.35f * mTextHundredsSize, mTextHundredsPaint);

      if (isThousands) {
        canvas.drawRect(
            0, y - mConfig.mVeryThickLineThickness / 2,
            mTickMarkThousandsLength, y + mConfig.mVeryThickLineThickness / 2,
            mConfig.mLinePaint);
        canvas.drawRect(
            mThousandsEmphasisLineDistanceFromLeft,
            y - mTextThousandsSize / 2 - mThousandsEmphasisLineDistanceFromText - mConfig.mThinLineThickness,
            getWidth(),
            y - mTextThousandsSize / 2 - mThousandsEmphasisLineDistanceFromText,
            mConfig.mLinePaint);
        canvas.drawRect(
            mThousandsEmphasisLineDistanceFromLeft,
            y + mTextThousandsSize / 2 + mThousandsEmphasisLineDistanceFromText,
            getWidth(),
            y + mTextThousandsSize / 2 + mThousandsEmphasisLineDistanceFromText + mConfig.mThinLineThickness,
            mConfig.mLinePaint);
      } else if (isHundreds) {
        canvas.drawRect(
            0, y - mConfig.mThickLineThickness / 2,
            mTickMarkHundredsLength, y + mConfig.mThickLineThickness / 2,
            mConfig.mLinePaint);
      }
    } else {
      canvas.drawRect(
          0, y - mConfig.mThinLineThickness / 2,
          mTickMarkTwentiesLength, y + mConfig.mThinLineThickness / 2,
          mConfig.mLinePaint);
    }
  }

  private void drawScaleLine(Canvas canvas) {
    float yMax = Math.min(getHeight(), altitudeToCanvasPosition(0f));
    canvas.drawRect(0, 0, mConfig.mThinLineThickness, yMax + mConfig.mVeryThickLineThickness / 2, mConfig.mLinePaint);
  }

  @Override
  protected void drawContents(Canvas canvas) {
    drawScaleLine(canvas);
    int alt20AtTop = (int) Math.ceil((mModel.getAltitude() + (getHeight() / 2f) * mTapePixelsPerFoot) / 20) + 1;
    int alt20AtBottom = (int) Math.floor((mModel.getAltitude() - (getHeight() / 2f) * mTapePixelsPerFoot) / 20) - 1;
    for (int i = alt20AtBottom; i <= alt20AtTop; i++) {
      drawAltitude(canvas, i);
    }
  }
}
