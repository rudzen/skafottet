
/*
 * Copyright 2016 Rudy Alex Kohn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.activities;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
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
 *         - Battery Check (Disabled until further notice)<br>
 *         - Menu Sounds
 * </p>
 * Uses WeakReferences to avoid having any loose ends where the GC can't collect the object.
 * @author rudz
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class MenuActivityAbstract extends SoundAbstract {
//    BatteryLevelRecieverData.BatteryLevelRecieveDataInterface

    protected Handler menuHandler;

    /* star field stuff */
    protected StarField sf;

    /* battery stuff - currently disabled !!! */

//    IntentFilter batteryLevelFilter;
//    BatteryLevelReciever batteryLevelReciever;
//    BatteryLevelRecieverData batteryLevelRecieverData;

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

        /* begin configuration for starfield and sensor */
        sf = (StarField) findViewById(R.id.sf);

        sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);

        registerSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null && mSensorManager != null) mSensorManager.registerListener(sensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (menuHandler != null) menuHandler.removeCallbacksAndMessages(null);
        else menuHandler = new Handler();
        if (sf == null) {
            sf = (StarField) findViewById(R.id.sf);
            sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);
        } else {
            sf.setRun(true);
        }
//        if (batteryLevelRecieverData == null) {
//            batteryLevelRecieverData = new BatteryLevelRecieverData(this);
//        }
//        registerBatteryReciever();
    }

    @Override
    protected void onPause() {
//        unregisterBatteryReciever();
        if (sf != null) sf.setRun(false);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        menuHandler.removeCallbacksAndMessages(null);
        if (mSensor != null && mSensorManager != null) mSensorManager.unregisterListener(sensorListener, mSensor);
    }

    @Override
    protected void onDestroy() {
        //noinspection AssignmentToNull
        sf = null;
        mSensor = null;
        mSensorManager = null;
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            sf = (StarField) findViewById(R.id.sf);
            sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);
        }
        if (sf != null) {
            // this SHOULD fix idiotic apps like facebook messenger messing with my starfield!
            // it also stops the animation when a dialog is showing (which is quite okay!)
            sf.setRun(hasFocus);
        }
    }

//    private void registerBatteryReciever() {
//        if (batteryLevelRecieverData != null && BatteryLevelReciever.containsObserver(batteryLevelRecieverData)) {
//            BatteryLevelReciever.removeObserver(batteryLevelRecieverData);
//        }
//        batteryLevelRecieverData = new BatteryLevelRecieverData(this, false);
//        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        BatteryLevelReciever.addObserver(batteryLevelRecieverData);
//        batteryLevelReciever = new BatteryLevelReciever();
//        registerReceiver(batteryLevelReciever, batteryLevelFilter);
//    }

//    private void unregisterBatteryReciever() {
//        BatteryLevelReciever.removeObserver(batteryLevelRecieverData);
//        unregisterReceiver(batteryLevelReciever);
//        batteryLevelRecieverData = null;
//        batteryLevelFilter = null;
//        batteryLevelReciever = null;
//    }

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

//    @Override
//    public void onBatteryStatusChanged(final BatteryDTO batteryInformation) {
//        Log.d(TAG, String.valueOf(batteryInformation.getLevel()));
//        menuHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BatteryLevelReciever.addObserver(batteryLevelRecieverData);
//            }
//        }, 20000);
//
//        if (sf != null) {
//            // turn off the starfield if lower than 25% battery
//            if (sf.isRun() && batteryInformation.getLevel() < 25) {
//                sf.setRun(false);
//            } else if (!sf.isRun() && batteryInformation.getLevel() > 25) {
//                sf.setRun(true);
//            }
//        }
//    }


}