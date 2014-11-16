package org.schmivits.airball.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public final class SingleImage extends Widget {

    private final Bitmap mBmp;

    public SingleImage(Bitmap bmp) {
        super.sizeTo(bmp.getWidth(), bmp.getHeight());
        mBmp = bmp;
    }

    public void sizeTo(float x, float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawBitmap(mBmp, 0f, 0f, null);
    }
}
