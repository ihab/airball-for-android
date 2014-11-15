package com.google.airball.phone;

import com.google.airball.airdata.FlightData;
import com.google.airball.display.AirballAnalog;
import com.google.airball.display.AirballPFD;
import com.google.airball.widget.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class AirballView extends SurfaceView {

    private class FrameRateCounter {
        private static final int REPORTING_INTERVAL = 100;
        private int mCount;
        private long mTime;
        public void addRenderingTime(long time) {
            mCount++;
            mTime += time;
            if (mCount == REPORTING_INTERVAL) {
                double frameRate = (double) mCount / (double) mTime * 1000.0;
                Log.v(AirballView.class.getName(), "Frame rate = " + frameRate);
                mCount = 0;
                mTime = 0L;
            }
        }
    }

    private FlightData mModel;
    private final FrameRateCounter mFrameRateCounter = new FrameRateCounter();

    public AirballView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialize();
                AirballView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setModel(final FlightData model) {
        mModel = model;
    }

    private void initialize() {
        final Widget widget = createWidget();
        final Runnable drawCallback = new Runnable() {
            @Override
            public void run() {
                Canvas canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    long start = System.currentTimeMillis();
                    widget.draw(canvas);
                    mFrameRateCounter.addRenderingTime(System.currentTimeMillis() - start);
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        };

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mModel.addUpdateListener(drawCallback);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mModel.removeUpdateListener(drawCallback);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AirballView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Widget createWidget() {
        if (Prefs.prefEq(
                getContext(),
                R.string.pref_view_type_key,
                R.string.pref_view_type_value_pfd)) {
            return new AirballPFD(
                    getResources(),
                    getContext().getAssets(),
                    mModel,
                    getWidth(), getHeight());
        } else if (Prefs.prefEq(
                getContext(),
                R.string.pref_view_type_key,
                R.string.pref_view_type_value_analog)) {
            return new AirballAnalog(
                    getResources(),
                    getContext().getAssets(),
                    mModel,
                    getWidth(), getHeight());
        } else {
            throw new RuntimeException("Unrecognized preference value for view type");
        }
    }
}
