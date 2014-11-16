package org.schmivits.airball.widget;

import android.graphics.Canvas;

public class Rectangle extends Widget {

    private final int mColor;

    public Rectangle(int color) {
        mColor = color;
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawColor(mColor);
    }
}
