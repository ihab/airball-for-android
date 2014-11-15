package com.google.airball.glass;

import android.content.Intent;
import android.os.Bundle;

public class StartAirballActivityActualData extends AirballActivity {
  
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setEnabled(StartAirballActivityActualData.class, false);
    setEnabled(StartAirballActivityDummyData.class, false);
    setEnabled(StopAirballActivity.class, true);
    startService(new Intent(this, AirballServiceActualData.class));
    finish();
  }
}
