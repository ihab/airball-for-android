package com.google.airball.display.airball;

import com.google.airball.display.DisplayConfiguration;
import com.google.airball.widget.Widget;

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