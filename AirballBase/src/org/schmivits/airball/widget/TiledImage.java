package org.schmivits.airball.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TiledImage extends Widget {

    private final Bitmap mBmp;

    public TiledImage(Bitmap bmp) {
        mBmp = bmp;
    }

    @Override
    protected void drawContents(Canvas canvas) {
        for (float x = 0; x < getWidth(); x += mBmp.getWidth()) {
            for (float y = 0; y < getHeight(); y += mBmp.getHeight()) {
                canvas.drawBitmap(mBmp, x, y, null);
            }
        }
    }
}
