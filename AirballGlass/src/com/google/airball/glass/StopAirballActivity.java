package com.google.airball.glass;

import android.content.Intent;
import android.os.Bundle;

public class StopAirballActivity extends AirballActivity {
  
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setEnabled(StartAirballActivityActualData.class, true);
    setEnabled(StartAirballActivityDummyData.class, true);
    setEnabled(StopAirballActivity.class, false);
    stopService(new Intent(this, AirballServiceActualData.class));
    stopService(new Intent(this, AirballServiceDummyData.class));    
    finish();
  }
}
