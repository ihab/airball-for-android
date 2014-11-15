package com.google.airball.widget;

import android.graphics.Canvas;
import android.os.Debug;

public abstract class Widget {

    protected float mX;
    protected float mY;
    protected float mW;
    protected float mH;
    private boolean mVisible = true;
    private boolean mClip = true;
    private boolean mSizing = false;
    private boolean mMoving = false;

    protected Widget() {
        this(0, 0, 0, 0);
    }

    protected Widget(float x, float y, float w, float h) {
        mX = x;
        mY = y;
        mW = w;
        mH = h;
    }

    public void moveTo(float x, float y) {
        mX = x; mY = y;
        if (!mMoving) {
            mMoving = true;
            onMove();
            mMoving = false;
        }
    }

    public void sizeTo(float w, float h) {
        mW = w; mH = h;
        if (!mSizing) {
            mSizing = true;
            onResize();
            mSizing = false;
        }
    }

    public final float getX() {
        return mX;
    }

    public final void setX(float x) { moveTo(x, mY); }

    public final float getY() {
        return mY;
    }

    public final void setY(float y) { moveTo(mX, y); }

    public final float getWidth() {
        return mW;
    }

    public final void setWidth(float w) { sizeTo(w, mH); }

    public final float getHeight() {
        return mH;
    }

    public final void setHeight(float h) { sizeTo(mW, h); }

    public boolean isVisible() { return mVisible; }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isClip() {
        return mClip;
    }

    public void setClip(boolean clip) {
        mClip = clip;
    }

    protected void onResize() {}

    protected void onMove() {}

    public final void draw(Canvas canvas) {
        if (!mVisible) {
            return;
        }
        canvas.save();
        canvas.translate(mX, mY);
        if (mClip) { canvas.clipRect(0f, 0f, mW, mH); }
        drawContents(canvas);
        canvas.restore();
    }

    protected abstract void drawContents(Canvas canvas);
}
