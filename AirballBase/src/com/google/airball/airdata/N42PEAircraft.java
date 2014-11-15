package com.google.airball.airdata;

public class N42PEAircraft implements Aircraft {

    @Override
    public float getVs0() {
        return 35f;
    }

    @Override
    public float getVs1() {
        return 45f;
    }

    @Override
    public float getVfe() {
        return 84f;
    }

    @Override
    public float getVno() {
        return 126f;
    }

    @Override
    public float getVne() {
        return 158f;
    }

    @Override
    public float getAs() {
        return 1.0f;
    }

    @Override
    public float getAmin() {
        return 0.0f;
    }

    @Override
    public float getAx() {
        return 0.7f;
    }

    @Override
    public float getAy() {
        return 0.75f;
    }

    @Override
    public float getAref() {
        return 0.9f;
    }

    @Override
    public float getBfs() {
        return 3.0f;
    }
}
