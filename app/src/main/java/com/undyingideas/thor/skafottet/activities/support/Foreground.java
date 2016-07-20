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

package com.undyingideas.thor.skafottet.activities.support;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
@SuppressWarnings({"StaticVariableOfConcreteClass", "StaticMethodNamingConvention", "OverloadedMethodsWithSameNumberOfParameters", "HardcodedFileSeparator", "StandardVariableNames"})
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

    private final AtomicBoolean mForeground = new AtomicBoolean();
    private final AtomicBoolean mPaused = new AtomicBoolean(true);

    private final Handler mHandler = new Handler();
    private final CopyOnWriteArrayList<Listener> mListeners = new CopyOnWriteArrayList<>();
    private Runnable mCheck;

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
            } else {
                throw new IllegalStateException("Foreground is not initialised and cannot obtain the Application object");
            }
        }
        return instance;
    }

    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException("Foreground is not initialised - invoke at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() { return mForeground.get(); }

    public boolean isBackground() { return !mForeground.get(); }

    public void addListener(final Listener listener) { mListeners.add(listener); }

    public void removeListener(final Listener listener) { mListeners.remove(listener); }

    @Override
    public void onActivityResumed(final Activity activity) {
        mPaused.set(false);
        final boolean wasBackground = !mForeground.get();
        mForeground.set(true);

        if (mCheck != null) {
            mHandler.removeCallbacks(mCheck);
        }

        if (wasBackground) {
            Log.i(TAG, "went foreground");
            for (final Listener l : mListeners) {
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
        mPaused.set(true);

        if (mCheck != null) {
            mHandler.removeCallbacks(mCheck);
        }
        mCheck = new ListenerRunnable();
        mHandler.postDelayed(mCheck, CHECK_DELAY);
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

        @SuppressWarnings("StandardVariableNames")
        @Override
        public void run() {
            if (mForeground.get() && mPaused.get()) {
                mForeground.set(false);
                Log.i(TAG, "went background");
                for (final Listener l : mListeners) {
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