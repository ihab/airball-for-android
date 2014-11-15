package com.google.airball.glass;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.SurfaceHolder;

import com.google.airball.airdata.FlightData;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

public abstract class AirballService extends Service {

  private static final String LIVE_CARD_ID = "com.google.airball.glass";

  private SurfaceHolder.Callback mCallback = null;
  private TimelineManager mTimelineManager = null;
  private LiveCard mLiveCard = null;
  private FlightData mFlightData = null;

  @Override
  public void onCreate() {
    super.onCreate();
    mTimelineManager = TimelineManager.from(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (mLiveCard == null) {
      mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);
      mFlightData = getFlightData();
      mCallback = new AirballPainter(this, mFlightData);
      mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mCallback);
      mLiveCard.setAction(
          PendingIntent.getActivity(
              this, 
              0, 
              new Intent(this, StartAirballActivityActualData.class),  // KLUDGE!!
              0));
      mLiveCard.publish();
    }
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    if (mLiveCard != null && mLiveCard.isPublished()) {
      if (mCallback != null) {
        mLiveCard.getSurfaceHolder().removeCallback(mCallback);
      }
      mLiveCard.unpublish();
      mLiveCard = null;
    }
    if (mFlightData != null) {
      mFlightData.destroy();
      mFlightData = null;
    }
    super.onDestroy();
  }

  protected abstract FlightData getFlightData();
}
