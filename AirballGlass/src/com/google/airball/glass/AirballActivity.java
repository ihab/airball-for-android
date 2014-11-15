package com.google.airball.glass;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

public class AirballActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }
  
  protected void setEnabled(Class<? extends Activity> activity, boolean state) {
    getPackageManager().setComponentEnabledSetting(
        new ComponentName(getApplicationContext(), activity),
        state ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP);
  }
}
