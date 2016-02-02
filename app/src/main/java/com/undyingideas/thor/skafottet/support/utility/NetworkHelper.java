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
        if (conn == TYPE_WIFI) status = "Wifi";
        else if (conn == TYPE_MOBILE) status = "Mobil";
        else status = conn == TYPE_NOT_CONNECTED ? "Ingen" : null;
        return status;
    }

    public static String getConnectivityStatusStringFromStatus(final int connectionStatus) {
        final String status;
        if (connectionStatus == TYPE_WIFI) status = "Wifi";
        else status = connectionStatus == TYPE_MOBILE ? "Mobil" : "Ingen";
        return status;
    }

}
