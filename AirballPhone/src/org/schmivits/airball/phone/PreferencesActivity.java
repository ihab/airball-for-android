package org.schmivits.airball.phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

public class PreferencesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        findViewById(R.id.return_to_display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreferencesActivity.this, StartViewActivity.class));
                PreferencesActivity.this.finish();
            }
        });
    }

    public static class MainScreen
            extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final int[] mListPreferences = {
                R.string.pref_xplane_ip_protocol_key,
                R.string.pref_view_type_key,
                R.string.pref_data_source_key,
        };

        private static final int[] mEditTextPreferences = {
                R.string.pref_xplane_ip_port_key,
                R.string.pref_alpha_min_key,
                R.string.pref_alpha_x_key,
                R.string.pref_alpha_y_key,
                R.string.pref_alpha_ref_key,
                R.string.pref_alpha_s_key,
                R.string.pref_beta_full_scale_key,
                R.string.pref_v_s0_key,
                R.string.pref_v_s1_key,
                R.string.pref_v_fe_key,
                R.string.pref_v_no_key,
                R.string.pref_v_ne_key,
        };

        public  MainScreen() { super(); }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            update();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            update();
        }

        private Preference findPreference(int id) {
            return findPreference(getString(id));
        }

        private void updateListPreference(int id) {
            ListPreference lp = (ListPreference) findPreference(id);
            lp.setSummary(lp.getEntry());
        }

        private void updateEditTextPreference(int id) {
            EditTextPreference ep = (EditTextPreference) findPreference(id);
            ep.setSummary(ep.getText());
        }

        private void updateIpOptionsEnabled() {
            ListPreference dataSource = (ListPreference)
                    findPreference(R.string.pref_data_source_key);
            boolean enabled = getString(R.string.pref_data_source_value_xplane_udp).equals(
                    dataSource.getValue());
            findPreference(R.string.pref_xplane_ip_port_key).setEnabled(enabled);
            findPreference(R.string.pref_xplane_ip_protocol_key).setEnabled(enabled);
        }

        private void update() {
            for (int k : mListPreferences) { updateListPreference(k); }
            for (int k : mEditTextPreferences) { updateEditTextPreference(k); }
            updateIpOptionsEnabled();
        }
    }
}
