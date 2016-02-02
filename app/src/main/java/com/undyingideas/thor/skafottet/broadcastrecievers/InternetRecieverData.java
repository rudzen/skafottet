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

import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * Created on 16-01-2016, 07:28.
 * Project : skafottet<br>
 * Generic connection state data class.<br>
 * Combines observer and DTO for internet connection status.
 * For use with {@link InternetReciever}.<br>
 * - Updated for skafottet's data structure.
 * @author rudz
 */
public class InternetRecieverData implements Runnable {

    /* store the destination interface as a WEAK reference, this allows the system to GC the subject
       if needed. Since it's guarded nothing will happend if the target was destroyed. */
    private final WeakReference<InternetRecieverInterface> internetRecieverInterfaceWeakReference;

    /* the TAG so we know who we are */
    private final static String TAG = "InternetRecieverData";

    /* the internet connection state, this is set through the broadcast reciever itself */
    private int data; // is -1 if no connection!

    /* to let the class add itself back to the InternetReciever stack */
    private boolean keepInReciever;

    /* simple (and memory efficient) structure to hold the string values of the connection state */
    private static final SparseArray<String> CONNECTION_INFO = new SparseArray<>(7);

    /* populate the structure with the needed strings */
    static {
        CONNECTION_INFO.put(ConnectivityManager.TYPE_MOBILE, "Mobil");
        CONNECTION_INFO.put(ConnectivityManager.TYPE_WIFI, "Wifi");
        CONNECTION_INFO.put(ConnectivityManager.TYPE_ETHERNET, "Ethernet");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CONNECTION_INFO.put(ConnectivityManager.TYPE_VPN, "VPN");
        }
        CONNECTION_INFO.put(ConnectivityManager.TYPE_BLUETOOTH, "Bluetooth");
        CONNECTION_INFO.put(ConnectivityManager.TYPE_DUMMY, "Dummy");
        CONNECTION_INFO.put(ConnectivityManager.TYPE_MOBILE_DUN, "Mobil DUN");
    }

    /**
     * This interface has to be implemented by the target, this ensures that we can send data to it.<br>
     * This is a combination of controlling the runnable interface + the needs for the {@link InternetReciever}.<br>
     * In essence, it "replaces" the Runnable interface with a more controlled one.<br>
     * @author rudz
     */
    public interface InternetRecieverInterface {
        void onInternetStatusChanged(int connectionState);
        void onInternetStatusChanged(final String connectionState);
    }

    /**
     * Required the interface implementation.
     * @param internetRecieverInterface The object which implements the interface.
     */
    public InternetRecieverData(final InternetRecieverInterface internetRecieverInterface) {
        this(internetRecieverInterface, true);
    }

    /**
     * Allows defining manually if this class should be kept by the Internet Reciever.
     * @param internetRecieverInterface The object which implements the interface.
     * @param keepInReciever If true, this observer class will not be removed when executed.
     */
    private InternetRecieverData(final InternetRecieverInterface internetRecieverInterface, final boolean keepInReciever) {
        internetRecieverInterfaceWeakReference = new WeakReference<>(internetRecieverInterface);
        this.keepInReciever = keepInReciever;
    }

    /**
     * The runnable interface is started here, this will make sure there still is a reference to the master class.
     * If the references is obtained, it will send the data back to it through its own interface.
     */
    @Override
    public void run() {
        Log.d(TAG, "Observer runnable started.");
        final InternetRecieverInterface internetRecieverInterface = internetRecieverInterfaceWeakReference.get();
        if (internetRecieverInterface != null) {
            internetRecieverInterface.onInternetStatusChanged(data);
            if (data > -1) {
                internetRecieverInterface.onInternetStatusChanged(CONNECTION_INFO.get(data));
            } else {
                internetRecieverInterface.onInternetStatusChanged("Ingen");
            }
        }
    }

    /**
     * Setter for connection state, this is invokes through the reciever.
     * @param newData The connection state of the system
     */
    public void setData(final int newData) {
        data = newData;
    }

    public int  getData() {
        return data;
    }


    /**
     * Sets the keepInReciever feature
     * @param newValue If true, it will not be removed from the internet reciever.
     */
    public void setKeepInReciever(final boolean newValue) {
        keepInReciever = newValue;
    }

    public boolean isKeepInReciever() {
        return keepInReciever;
    }
}
