package org.schmivits.airball.display;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;

public class DisplayConfiguration {

    public final int mTextColor = Color.WHITE;
    public final int mLineColor = Color.WHITE;
    public final int mPointerColor = Color.parseColor("#ffcc33");

    public final Paint mLinePaint;
    public final Paint mPointerPaint;
    public final float mCautionStripeThickness;
    public final float mThinLineThickness;
    public final float mThickLineThickness;
    public final float mVeryThickLineThickness;
    public final float mPointerShapeSize;
    public final float mPointerToScaleOffset;
    public final String mTextTypeface = "fonts/LiberationSans-Regular.ttf";
    public final Resources mResources;
    public final AssetManager mAssets;

    public DisplayConfiguration(float w, float h, Resources resources, AssetManager assets) {
        mResources = resources;
        mAssets = assets;
        mCautionStripeThickness = (float) Math.floor(w / 100);
        mThinLineThickness = (float) Math.floor(w / 300);
        mThickLineThickness = (float) Math.floor(1.75f * mThinLineThickness);
        mVeryThickLineThickness = (float) Math.floor(2.5f * mThinLineThickness);
        mPointerShapeSize = (float) Math.floor(w / 30);
        mPointerToScaleOffset = (float) Math.floor(mPointerShapeSize / 3);

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);

        mPointerPaint = new Paint();
        mPointerPaint.setColor(mPointerColor);
        mPointerPaint.setAntiAlias(true);
    }
}
