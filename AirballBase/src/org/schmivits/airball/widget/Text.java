package org.schmivits.airball.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Text extends Widget {

    // TODO: This is a bit of a hack because text gets clipped; need to improve
    private static final float RIGHT_PADDING = 3f;

    private final String mText;

    private final Paint mPaint;

    public Text(String text, float size, int color, Typeface typeface) {
        mText = text;

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setTypeface(typeface);
        mPaint.setTextSize(size);
        mPaint.setTextAlign(Align.LEFT);
        mPaint.setAntiAlias(true);

        Rect r = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), r);
        sizeTo(r.width() + RIGHT_PADDING, r.height());
    }

    @Override
    protected void drawContents(Canvas canvas) {
        canvas.drawText(mText, 0, getHeight(), mPaint);
    }
}
