package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
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

    private final Handler handler = new Handler();
    private static final ArrayList<BatteryLevelRecieverData> observers = new ArrayList<>();

    private final static String TAG = "BatteryLevelReciever";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "BatteryLevel change");
        if (intent.getExtras() != null) {

            // notify the observers who cares about the current battery state!
            for (final BatteryLevelRecieverData internetRecieverData : observers) {
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

    public static void addObserver(final BatteryLevelRecieverData newObserver) {
        if (!observers.contains(newObserver)) {
            observers.add(newObserver);
            Log.d(TAG, "Observer added");
            Log.d(TAG, "Observers current in stack :" + observers.size());
        }
    }

    public static boolean removeObserver(final BatteryLevelRecieverData observerToRemove) {
        return observers.remove(observerToRemove);
    }


}
