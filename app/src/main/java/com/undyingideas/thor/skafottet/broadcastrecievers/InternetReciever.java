package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayDeque;

/**
 * Stakes a claim for information from the system about the current internet connection state.
 * Observers can ask to be notified about the current state.
 */
public class InternetReciever extends BroadcastReceiver {

    private Handler handler = new Handler();
    private static ArrayDeque<Runnable> observers = new ArrayDeque<>();

    private final static String TAG = "InternetReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");
        final Bundle bundle = intent.getExtras();
        CharSequence msg;
        if (bundle != null) {
            final NetworkInfo ni = (NetworkInfo) bundle.get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                msg = "Internetforbindelsen oprettet via " + ni.getTypeName();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.d(TAG, "There's no network connectivity");
                msg = "Ingen internet forbindelse.";
            }
        }
        // notify the observers who cares about the current internet state!
        while (!observers.isEmpty()) {
            handler.post(observers.pollFirst());
        }
    }

    public static void addObserver(final Runnable newObserver) {
        observers.push(newObserver);
    }

    public static void removeObserver(final Runnable observerToRemove) {
        observers.remove(observerToRemove);
    }
}
