package org.schmivits.airball.airdata;

/**
 * Aircraft V speeds. Standard nomenclature.<br>
 * All values are knots of indicated air speed (dynamic pressure).<br>
 * 0 through 999
 */
public interface Aircraft {

    float getVs0();

    float getVs1();

    float getVfe();

    float getVno();

    float getVne();

    float getAs();

    float getAmin();

    float getAx();

    float getAy();

    float getAref();

    float getBfs();
}
