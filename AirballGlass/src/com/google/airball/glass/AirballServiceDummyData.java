package com.google.airball.glass;

import android.content.Context;
import android.hardware.SensorManager;

import com.google.airball.airdata.AccelerometerFlightData;
import com.google.airball.airdata.FlightData;

public class AirballServiceDummyData extends AirballService {
  
  @Override protected FlightData getFlightData() {
    return new AccelerometerFlightData((SensorManager) getSystemService(Context.SENSOR_SERVICE));
  }
}
