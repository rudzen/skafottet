/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.support.firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created on 20-01-2016, 09:41.
 * Project : skafottet
 *
 * @author rudz
 */
class FireBaseValueListener implements ValueEventListener {
    @Override
    public void onDataChange(final DataSnapshot snapshot) {
        if (snapshot != null) {
            final boolean connected = snapshot.getValue(Boolean.class);
            if (connected) {
                Log.d("Firebase", "connected");
            } else {
                Log.d("Firebase", "not connected");
            }
        }
    }

    @Override
    public void onCancelled(final FirebaseError error) {
        Log.d("Firebase", "Connection listener cancelled");
    }
}
