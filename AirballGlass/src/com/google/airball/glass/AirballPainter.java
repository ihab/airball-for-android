
package com.google.airball.glass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import com.google.airball.airdata.FlightData;
import com.google.airball.display.AirballPFD;
import com.google.airball.widget.Widget;

public class AirballPainter implements SurfaceHolder.Callback {
  private SurfaceHolder mHolder;
  private final FlightData mFlightData;
  private final Widget mWidget;
  private final Runnable mUpdateListener = new Runnable() {
    @Override public void run() { draw(); }
  };
  
  public AirballPainter(Context context, FlightData flightData) {
    mFlightData = flightData;
    mWidget = new AirballPFD(context.getResources(), context.getAssets(), mFlightData, 640f, 480f);
  }

  private void draw() {
    Canvas canvas;
    try {
      canvas = mHolder.lockCanvas();
    } catch (Exception e) {
      return;
    }
    if (canvas != null) {
      canvas.drawColor(Color.BLACK);
      mWidget.draw(canvas);
      mHolder.unlockCanvasAndPost(canvas);
    }
  } 
  
  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    mHolder = holder;    
    mFlightData.addUpdateListener(mUpdateListener);
    draw();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    mFlightData.removeUpdateListener(mUpdateListener);
    mHolder = null;
    draw();    
  }
}
