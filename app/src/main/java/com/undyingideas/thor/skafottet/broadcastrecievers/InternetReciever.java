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

    private final Handler mHandler = new Handler();
    private static final ArrayList<InternetRecieverData> OBSERVERS = new ArrayList<>();

    private final static String TAG = "InternetReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final int connectionStatus = activeNetwork != null ? activeNetwork.getType() : -1;

        // notify the observers who cares about the current internet state!
        for (final InternetRecieverData internetRecieverData : OBSERVERS) {
            internetRecieverData.setData(connectionStatus);
            mHandler.post(internetRecieverData);
            if (!internetRecieverData.isKeepInReciever()) {
                if (removeObserver(internetRecieverData)) {
                    Log.d(TAG, "Observer removed");
                } else {
                    Log.d(TAG, "Observer kept");
                }
            }
        }
        OBSERVERS.trimToSize();
    }

    public static void addObserver(final InternetRecieverData newObserver) {
        if (!OBSERVERS.contains(newObserver)) {
            OBSERVERS.add(newObserver);
            Log.d(TAG, "Observer added");
            Log.d(TAG, "Observers current in stack :" + OBSERVERS.size());
        }
    }

    public static boolean removeObserver(final InternetRecieverData observerToRemove) {
        return OBSERVERS.remove(observerToRemove);
    }
}
