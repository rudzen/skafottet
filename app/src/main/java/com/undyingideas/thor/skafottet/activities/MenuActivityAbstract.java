/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.broadcastrecievers.BatteryDTO;
import com.undyingideas.thor.skafottet.broadcastrecievers.BatteryLevelReciever;
import com.undyingideas.thor.skafottet.broadcastrecievers.BatteryLevelRecieverData;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.StarField;

import java.lang.ref.WeakReference;

/**
 * Created on 28-12-2015, 13:07.
 * Project : R-TicTacToe
 * <p>
 *     Contains :<br>
 *         - Starfield View<br>
 *         - Sensor<br>
 *         - Battery Check
 * </p>
 * Uses WeakReferences to avoid having any loose ends where the GC can't collect the object.
 * @author rudz
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class MenuActivityAbstract extends AppCompatActivity implements BatteryLevelRecieverData.BatteryLevelRecieveDataInterface {

    /* star field stuff */
    @Nullable
    StarField sf;
    private Handler starhandler;
    private UpdateStarfield updateStarfield;

    /* battery stuff */
    IntentFilter batteryLevelFilter;
    BatteryLevelReciever batteryLevelReciever;
    BatteryLevelRecieverData batteryLevelRecieverData;

    /* sensor stuff */
    @Nullable
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mSensor;
    private MenuSensorEventListener sensorListener;

    private static final String TAG = "MenuActivityAbstract";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* whoop */
        setContentView(R.layout.activity_menu);

        // TODO : Insert battery level check and disable background animation and sensor if <= low

        /* begin configuration for starfield and sensor */
        sf = (StarField) findViewById(R.id.sf);

        sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);

        updateStarfield = new UpdateStarfield(this);

        starhandler = new Handler();
        starhandler.post(updateStarfield);


        registerSensor();
        registerBatteryReciever();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null && mSensorManager != null) mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (starhandler != null) starhandler.removeCallbacksAndMessages(null);
        else starhandler = new Handler();
        if (sf == null) {
            sf = (StarField) findViewById(R.id.sf);
            sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);
        } else {
            sf.setRun(true);
        }
        starhandler.post(updateStarfield);
        if (batteryLevelRecieverData == null) {
            batteryLevelRecieverData = new BatteryLevelRecieverData(this);
        }
        registerBatteryReciever();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sf != null) sf.setRun(false);
        starhandler.removeCallbacksAndMessages(null);
        if (mSensor != null && mSensorManager != null) mSensorManager.unregisterListener(sensorListener, mSensor);
        if (batteryLevelReciever != null) {
            unregisterBatteryReciever();
        }
    }

    @Override
    protected void onDestroy() {
        sf = null;
        mSensor = null;
        mSensorManager = null;
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (sf != null) {
            // this SHOULD fix idiotic apps like facebook messenger messing with my starfield!
            // it also stops the animation when a dialog is showing (which is quite okay!)
            sf.setRun(hasFocus);
        }
    }
    private void registerBatteryReciever() {
        if (batteryLevelRecieverData != null && BatteryLevelReciever.containsObserver(batteryLevelRecieverData)) {
            BatteryLevelReciever.removeObserver(batteryLevelRecieverData);
        }
        batteryLevelRecieverData = new BatteryLevelRecieverData(this, false);
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BatteryLevelReciever.addObserver(batteryLevelRecieverData);
        batteryLevelReciever = new BatteryLevelReciever();
        registerReceiver(batteryLevelReciever, batteryLevelFilter);
    }

    private void unregisterBatteryReciever() {
        BatteryLevelReciever.removeObserver(batteryLevelRecieverData);
        batteryLevelRecieverData = null;
        unregisterReceiver(batteryLevelReciever);
        batteryLevelFilter = null;
        batteryLevelReciever = null;
    }

    private void registerSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (mSensor != null) {
            sensorListener = new MenuSensorEventListener(this);
            mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
            Log.i(TAG, "TYPE_GRAVITY sensor registered");
        } else {
            Log.e(TAG, "TYPE_GRAVITY sensor NOT registered");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor != null) {
                sensorListener = new MenuSensorEventListener(this);
                mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
                Log.i(TAG, "TYPE_ACCELEROMETER sensor registered");
            } else {
                mSensorManager = null;
                Log.e(TAG, "TYPE_ACCELEROMETER sensor NOT registered");
            }
        }
    }

    private static class UpdateStarfield implements Runnable {
        private final WeakReference<MenuActivityAbstract> menuActivityAbstractWeakReference;
        private static final int FPS = 1000 / 30;

        public UpdateStarfield(final MenuActivityAbstract menuActivityAbstract) {
            menuActivityAbstractWeakReference = new WeakReference<>(menuActivityAbstract);
        }

        @Override
        public void run() {
            final MenuActivityAbstract menuActivityAbstract = menuActivityAbstractWeakReference.get();
            if (menuActivityAbstract != null && menuActivityAbstract.sf != null) {
                menuActivityAbstract.sf.invalidate();
                menuActivityAbstract.starhandler.postDelayed(menuActivityAbstract.updateStarfield, FPS);
            }
        }
    }

    private static class MenuSensorEventListener implements android.hardware.SensorEventListener {

        final WeakReference<MenuActivityAbstract> menuActivityAbstractWeakReference;

        public MenuSensorEventListener(final MenuActivityAbstract menuActivityAbstract) {
            menuActivityAbstractWeakReference = new WeakReference<>(menuActivityAbstract);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            final MenuActivityAbstract menuActivityAbstract = menuActivityAbstractWeakReference.get();
            if (menuActivityAbstract != null && menuActivityAbstract.sf != null) {
                menuActivityAbstract.sf.setGravity(event.values[0], event.values[1]);
//                Log.d("GRAV", "Starfield gravity updated to x: " + event.values[0] + ", y: " + event.values[1]);
            }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final  int accuracy) { }
    }

    @Override
    public void onBatteryStatusChanged(final BatteryDTO batteryInformation) {
        Log.d(TAG, String.valueOf(batteryInformation.getLevel()));
        BatteryLevelReciever.addObserver(batteryLevelRecieverData);

        if (sf != null) {
            // turn off the starfield if lower than 25% battery
            if (sf.isRun() && batteryInformation.getLevel() < 25) {
                sf.setRun(false);
            } else if (!sf.isRun() && batteryInformation.getLevel() > 25) {
                sf.setRun(true);
            }
        }
    }
}
