package org.schmivits.airball.widget;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

public class Container extends Widget {

    protected final List<Widget> mChildren = new ArrayList<Widget>();

    public Container() {
    }

    @Override
    protected void drawContents(Canvas canvas) {
        for (Widget w : mChildren) {
            w.draw(canvas);
        }
    }

    protected void center(Widget w) {
        w.moveTo(
                (getWidth() - w.getWidth()) / 2f,
                (getHeight() - w.getHeight()) / 2f);
    }

    protected void centerX(Widget w) {
        w.moveTo(
                (getWidth() - w.getWidth()) / 2f,
                w.getY());
    }

    protected void centerY(Widget w) {
        w.moveTo(
                w.getX(),
                (getHeight() - w.getHeight()) / 2f);
    }
}
