/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet;

import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import com.undyingideas.thor.skafottet.views.StarField;

import java.lang.ref.WeakReference;

/**
 * Created on 28-12-2015, 13:07.
 * Project : R-TicTacToe
 * <p>
 *     Contains Starfield View & Sensor related code ONLY!.
 * </p>
 * @author rudz
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class MenuActivityAbstract extends AppCompatActivity {

    StarField sf; // needs to be protected, as menu act should be able to pause the damn thing.
    private Handler starhandler;
    private UpdateStarfield updateStarfield;

    /* sensor stuff */
    @Nullable
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener sensorListener;

    private static final String TAG = "MenuActivityAbstract";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* whoop */
        setContentView(R.layout.activity_menu);

        // TODO : Insert battery level check and disable background animation and sensor if <= low

        /* begin configuration for starfield and sensor */
        sf = (StarField) findViewById(R.id.sf);

        final Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        sf.init(size.x, size.y, Color.RED);

        updateStarfield = new UpdateStarfield(this);

        starhandler = new Handler();
        starhandler.post(updateStarfield);

        registerSensor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null && mSensorManager != null) mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (starhandler != null) starhandler.removeCallbacksAndMessages(null);
        else starhandler = new Handler();
        sf.setRun(true);
        starhandler.post(updateStarfield);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sf.setRun(false);
        starhandler.removeCallbacksAndMessages(null);
        if (mSensor != null && mSensorManager != null) mSensorManager.unregisterListener(sensorListener, mSensor);
    }

    private void registerSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (mSensor != null) {
            sensorListener = new SensorEventListener(this);
            mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
            Log.i(TAG, "TYPE_GRAVITY sensor registered");
        } else {
            Log.e(TAG, "TYPE_GRAVITY sensor NOT registered");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor != null) {
                sensorListener = new SensorEventListener(this);
                mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
                Log.i(TAG, "TYPE_ACCELEROMETER sensor registered");
            } else {
                mSensorManager = null;
                Log.e(TAG, "TYPE_GRAVITY sensor NOT registered");
            }
        }
    }


    private static class UpdateStarfield implements Runnable {
        private final WeakReference<MenuActivityAbstract> menuActivityAbstractWeakReference;

        public UpdateStarfield(final MenuActivityAbstract menuActivityAbstract) {
            menuActivityAbstractWeakReference = new WeakReference<>(menuActivityAbstract);
        }

        @Override
        public void run() {
            final MenuActivityAbstract menuActivityAbstract = menuActivityAbstractWeakReference.get();
            if (menuActivityAbstract != null) {
                menuActivityAbstract.sf.invalidate();
                menuActivityAbstract.starhandler.postDelayed(menuActivityAbstract.updateStarfield, 1000 / 30);
            }
        }
    }

    private static class SensorEventListener implements android.hardware.SensorEventListener {

        final WeakReference<MenuActivityAbstract> menuActivityAbstractWeakReference;

        public SensorEventListener(final MenuActivityAbstract menuActivityAbstract) {
            menuActivityAbstractWeakReference = new WeakReference<>(menuActivityAbstract);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            final MenuActivityAbstract menuActivityAbstract = menuActivityAbstractWeakReference.get();
            if (menuActivityAbstract != null) {
                menuActivityAbstract.sf.setGravity(event.values[0], event.values[1]);
//                Log.d("GRAV", "Starfield gravity updated to x: " + event.values[0] + ", y: " + event.values[1]);
            }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final  int accuracy) { }
    }
}
