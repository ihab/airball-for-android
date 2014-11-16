package org.schmivits.airball.display.airball;

import org.schmivits.airball.display.DisplayConfiguration;
import org.schmivits.airball.widget.Widget;

public abstract class TotemPoleDecoration extends Widget {

    protected final DisplayConfiguration mConfig;
    protected float mUnitSize;

    protected TotemPoleDecoration(DisplayConfiguration config) {
        mConfig = config;
        setClip(false);
    }

    public void setUnitSize(float unitSize) {
        this.mUnitSize = unitSize;
        computeSize();
    }

    protected abstract void computeSize();
}