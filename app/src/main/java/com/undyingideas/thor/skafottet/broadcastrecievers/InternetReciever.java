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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Stakes a claim for information from the system about the current internet connection state.
 * Observers can ask to be notified about the current state in the correct order by stacking them up.
 * Uses {@link InternetRecieverData} to handle the information about the type.
 *
 * @author rudz
 */
public class InternetReciever extends BroadcastReceiver {

    private final Handler handler = new Handler();
    private static final ArrayList<InternetRecieverData> observers = new ArrayList<>();

    private final static String TAG = "InternetReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int connectionStatus = intent.getExtras().getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(connectionStatus);
        if (!networkInfo.isConnected()) {
            connectionStatus = -1;
        }
        // notify the observers who cares about the current internet state!
        for (final InternetRecieverData internetRecieverData : observers) {
            internetRecieverData.setData(connectionStatus);
            handler.post(internetRecieverData);
            if (!internetRecieverData.isKeepInReciever()) {
                if (removeObserver(internetRecieverData)) {
                    Log.d(TAG, "Observer removed");
                } else {
                    Log.d(TAG, "Observer kept");
                }
            }
        }
        observers.trimToSize();
    }

    public static void addObserver(final InternetRecieverData newObserver) {
        if (!observers.contains(newObserver)) {
            observers.add(newObserver);
            Log.d(TAG, "Observer added");
            Log.d(TAG, "Observers current in stack :" + observers.size());
        }
    }

    public static boolean removeObserver(final InternetRecieverData observerToRemove) {
        return observers.remove(observerToRemove);
    }
}
