package com.undyingideas.thor.skafottet.activities.support;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Usage:
 * <p/>
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 * <p/>
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 * <p/>
 * or
 * <p/>
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 * <p/>
 * Foreground.Listener myListener = new Foreground.Listener(){
 * public void onBecameForeground(){
 * // ... whatever you want to do
 * }
 * public void onBecameBackground(){
 * // ... whatever you want to do
 * }
 * }
 * <p/>
 * public void onCreate(){
 * super.onCreate();
 * Foreground.get(this).addListener(listener);
 * }
 * <p/>
 * public void onDestroy(){
 * super.onCreate();
 * Foreground.get(this).removeListener(listener);
 * }
 *
 * 18.01.2016, rudz<br>
 * - Modified to specific coding standard for the project.
 *
 * @author steveliles
 */
@SuppressWarnings({"StaticVariableOfConcreteClass", "StaticMethodNamingConvention", "OverloadedMethodsWithSameNumberOfParameters", "StaticVariableNamingConvention", "StandardVariableNames", "unused"})
public class Foreground implements Application.ActivityLifecycleCallbacks {


    public static final long CHECK_DELAY = 500;
    public static final String TAG = Foreground.class.getName();

    private static final String STILL_FOREGROUND = "still foreground";
    private static final String LISTENER_THREW_EXCEPTION = "Listener threw exception!";


    public interface Listener {
        void onBecameForeground();
        void onBecameBackground();
    }

    private static Foreground instance;

    private boolean foreground, paused = true;
    private final Handler handler = new Handler();
    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
    private Runnable check;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application The application itself
     * @return an initialised Foreground instance
     */
    public static Foreground init(final Application application) {
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(final Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(final Context ctx) {
        if (instance == null) {
            final Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException("Foreground is not initialised and " + "cannot obtain the Application object");
        }
        return instance;
    }

    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException("Foreground is not initialised - invoke " + "at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() { return foreground; }

    public boolean isBackground() { return !foreground; }

    public void addListener(final Listener listener) { listeners.add(listener); }

    public void removeListener(final Listener listener) { listeners.remove(listener); }

    @Override
    public void onActivityResumed(final Activity activity) {
        paused = false;
        final boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            Log.i(TAG, "went foreground");
            for (final Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (final Exception e) {
                    Log.e(TAG, LISTENER_THREW_EXCEPTION, e);
                }
            }
        } else {
            Log.i(TAG, STILL_FOREGROUND);
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);
        check = new ListenerRunnable();
        handler.postDelayed(check, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(final Activity activity) { }

    @Override
    public void onActivityStopped(final Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(final  Activity activity, final Bundle outState) {}

    @Override
    public void onActivityDestroyed(final Activity activity) { }

    private class ListenerRunnable implements Runnable {

        @Override
        public void run() {
            if (foreground && paused) {
                foreground = false;
                Log.i(TAG, "went background");
                for (final Listener l : listeners) {
                    try {
                        l.onBecameBackground();
                    } catch (final Exception e) {
                        Log.e(TAG, LISTENER_THREW_EXCEPTION, e);
                    }
                }
            } else {
                Log.i(TAG, STILL_FOREGROUND);
            }
        }
    }
}