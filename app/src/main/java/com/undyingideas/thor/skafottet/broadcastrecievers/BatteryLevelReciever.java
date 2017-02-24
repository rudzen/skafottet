/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
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

package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Stakes a claim for information from the system about the current battery information.
 * Observers can ask to be notified about the current state in the correct order by stacking them up.
 * Uses {@link BatteryLevelRecieverData} to handle the information about the type.
 *
 * @author rudz
 */
public class BatteryLevelReciever extends BroadcastReceiver {

    private final Handler mHandler = new Handler();
    private static final ArrayList<BatteryLevelRecieverData> mObservers = new ArrayList<>();

    private static final String TAG = "BatteryLevelReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Debug.isDebuggerConnected()) {
            Log.d(TAG, "BatteryLevel change");
        }
        if (intent.getExtras() != null) {

            // notify the observers who cares about the current battery state!
            for (final BatteryLevelRecieverData internetRecieverData : mObservers) {
                internetRecieverData.setData(BatteryDTO.newBuilder()
                        .level(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0))
                        .health(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0))
                        .iconSmall(intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0))
                        .plugged(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0))
                        .present(intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT))
                        .scale(intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0))
                        .status(intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0))
                        .tech(intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY))
                        .temperature(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0))
                        .voltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0))
                        .build()
                );
                mHandler.post(internetRecieverData);
                if (!internetRecieverData.isKeepInReciever()) {
                    if (Debug.isDebuggerConnected() && removeObserver(internetRecieverData)) {
                        Log.d(TAG, "Observer removed");
                    } else {
                        Log.d(TAG, "Observer kept");
                    }
                }
            }
            mObservers.trimToSize();
        }

    }

    public static void addObserver(final BatteryLevelRecieverData newObserver) {
        if (!mObservers.contains(newObserver)) {
            mObservers.add(newObserver);
            if (Debug.isDebuggerConnected()) {
                Log.d(TAG, "Observer added");
                Log.d(TAG, "Observers current in stack :" + mObservers.size());
            }
        }
    }

    private static boolean removeObserver(final BatteryLevelRecieverData observerToRemove) {
        return mObservers.remove(observerToRemove);
    }

    public static boolean containsObserver(final BatteryLevelRecieverData batteryLevelRecieverData) {
        return mObservers.contains(batteryLevelRecieverData);
    }

}
