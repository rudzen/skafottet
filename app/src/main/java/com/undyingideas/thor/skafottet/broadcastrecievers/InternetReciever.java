package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Stakes a claim for information from the system about the current internet connection state.
 * Observers can ask to be notified about the current state in the correct order by stacking them up.
 * Uses {@link InternetRecieverData} to handle the information about the type.
 * @author rudz
 */
public class InternetReciever extends BroadcastReceiver {

    private final Handler handler = new Handler();
    private static final ArrayList<InternetRecieverData> observers = new ArrayList<>();

    private final static String TAG = "InternetReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            final NetworkInfo ni = (NetworkInfo) bundle.get(ConnectivityManager.EXTRA_NETWORK_INFO);
            final int connectionStatus;
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                connectionStatus = ni.getType();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                connectionStatus = -1;
                Log.d(TAG, "There is no network connectivity");
            } else {
                // dirty guard hack
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
