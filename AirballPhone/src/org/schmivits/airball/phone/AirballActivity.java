package org.schmivits.airball.phone;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.schmivits.airball.airdata.AccelerometerFlightData;
import org.schmivits.airball.airdata.Aircraft;
import org.schmivits.airball.airdata.ConstantFlightData;
import org.schmivits.airball.airdata.DynonSerialFlightData;
import org.schmivits.airball.airdata.FlightData;
import org.schmivits.airball.airdata.N42PEBluetoothFlightData;
import org.schmivits.airball.airdata.N42PEUARTFlightData;
import org.schmivits.airball.airdata.XPlaneNetworkFlightData;

public abstract class AirballActivity extends Activity {

    private FlightData mFlightData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.airball);
        final AirballView view = (AirballView) findViewById(R.id.airball_view);

        View.OnTouchListener onTouch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setOnTouchListener(null);
                startActivity(new Intent(AirballActivity.this, PreferencesActivity.class));
                AirballActivity.this.finish();
                return true;
            }
        };
        view.setOnTouchListener(onTouch);

        mFlightData = getFlightData();
        view.setModel(mFlightData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlightData.destroy();
    }

    private final Aircraft mAircraft = new Aircraft() {
        private float p(int id) { return Prefs.getPrefFloat(AirballActivity.this, id); }
        @Override public float getVs0() { return p(R.string.pref_v_s0_key);  }
        @Override public float getVs1() { return p(R.string.pref_v_s1_key); }
        @Override public float getVfe() { return p(R.string.pref_v_fe_key); }
        @Override public float getVno() { return p(R.string.pref_v_no_key); }
        @Override public float getVne() { return p(R.string.pref_v_ne_key); }
        @Override public float getAs() { return p(R.string.pref_alpha_s_key); }
        @Override public float getAmin() { return p(R.string.pref_alpha_min_key); }
        @Override public float getAx() { return p(R.string.pref_alpha_x_key); }
        @Override public float getAy() { return p(R.string.pref_alpha_y_key); }
        @Override public float getAref() { return p(R.string.pref_alpha_ref_key); }
        @Override public float getBfs() { return p(R.string.pref_beta_full_scale_key); }
    };

    private FlightData getFlightData() {
        int prefKeyId = R.string.pref_data_source_key;
        if (Prefs.prefEq(this, prefKeyId, R.string.pref_data_source_value_constant_data)) {
            return new ConstantFlightData(mAircraft);
        } else if (Prefs.prefEq(this, prefKeyId, R.string.pref_data_source_value_dummy_data)) {
            return new AccelerometerFlightData(
                    mAircraft,
                    (SensorManager) getSystemService(SENSOR_SERVICE),
                    isLandscape());
        } else if (Prefs.prefEq(this, prefKeyId, R.string.pref_data_source_value_skyview_bluetooth)) {
          // TODO: Dynon Skyview data source is hard-coded for N42PE
            return new N42PEBluetoothFlightData(findBluetoothAdapter());
        } else if (Prefs.prefEq(this, prefKeyId, R.string.pref_data_source_value_skyview_usb_serial)) {
          // TODO: Dynon Skyview data source is hard-coded for N42PE
          return new N42PEUARTFlightData(this);
        } else if (Prefs.prefEq(this, prefKeyId, R.string.pref_data_source_value_xplane_udp)) {
            boolean isUdp;
            if (Prefs.prefEq(this,
                    R.string.pref_xplane_ip_protocol_key,
                    R.string.pref_xplane_ip_protocol_value_udp)) {
                isUdp = true;
            } else if (Prefs.prefEq(this,
                    R.string.pref_xplane_ip_protocol_key,
                    R.string.pref_xplane_ip_protocol_value_tcp)) {
                isUdp = false;
            } else {
                throw new RuntimeException("Unrecognized X-Plane data protocol preference value");
            }
            return new XPlaneNetworkFlightData(
                    mAircraft,
                    Prefs.getPrefInt(this, R.string.pref_xplane_ip_port_key),
                    isUdp);
        } else {
            throw new RuntimeException("Unrecognized data source preference value");
        }
    }

    protected abstract boolean isLandscape();

    private BluetoothAdapter findBluetoothAdapter() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            adapter = (BluetoothAdapter) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return adapter;
    }
}