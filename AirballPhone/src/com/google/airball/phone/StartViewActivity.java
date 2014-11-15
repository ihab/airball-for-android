package com.google.airball.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class StartViewActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Class<?> activityClass;
        if (Prefs.prefEq(
                this,
                R.string.pref_view_type_key,
                R.string.pref_view_type_value_pfd)) {
            activityClass = AirballPFDActivity.class;
        } else if (Prefs.prefEq(
                this,
                R.string.pref_view_type_key,
                R.string.pref_view_type_value_analog)) {
            activityClass = AirballAnalogActivity.class;
        } else {
            throw new RuntimeException("Unrecognized preference value for view type");
        }
        startActivity(new Intent(this, activityClass));
        finish();
    }
}
