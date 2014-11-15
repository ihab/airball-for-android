package com.google.airball.display.airball;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.google.airball.display.DisplayConfiguration;
import com.google.airball.widget.Widget;

public class AirballHeartCore extends Widget {

    private static final String HEART_IMAGE_FILE = "img/heart.png";

    private final Bitmap mBitmap;

    public AirballHeartCore(DisplayConfiguration config) {
        try {
            mBitmap = BitmapFactory.decodeStream(config.mAssets.open(HEART_IMAGE_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawBitmap(mBitmap, null, new RectF(0f, 0f, getWidth(), getHeight()), null);
    }
}
