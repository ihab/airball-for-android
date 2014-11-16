package org.schmivits.airball.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Prefs {

    private Prefs() {}

    public static float getPrefFloat(Context ctx, int id) {
        try {
            return Float.parseFloat(getSharedPreferences(ctx).getString(ctx.getString(id), "0.0"));
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public static int getPrefInt(Context ctx, int id) {
        try {
            return Integer.parseInt(getSharedPreferences(ctx).getString(ctx.getString(id), "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String getPrefString(Context ctx, int id) {
        return getSharedPreferences(ctx).getString(ctx.getString(id), "");
    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static boolean prefEq(Context ctx, int prefKeyId, int valueId) {
        return ctx.getString(valueId).equals(getPrefString(ctx, prefKeyId));
    }
}
