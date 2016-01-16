/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Determines network connection state.
 * Created by rudz on 10-11-2015.
 * - Updated 16-01-2016 to mirror broadcast internet connection intent type values.
 * @author rudz
 */
public final class NetworkHelper {

    private static final int TYPE_NOT_CONNECTED = -1;
    private static final int TYPE_MOBILE = 0;
    private static final int TYPE_WIFI = 1;

    public static int getConnectivityStatus(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    @SuppressWarnings("AssignmentToNull")
    public static String getConnectivityStatusString(final Context context) {
        final int conn = getConnectivityStatus(context);
        final String status;
        if (conn == TYPE_WIFI) status = "Wifi tilgængelig";
        else if (conn == TYPE_MOBILE) status = "Mobil data tilgængelig";
        else status = conn == TYPE_NOT_CONNECTED ? "Ingen forbindelse til internettet." : null;
        return status;
    }
}
