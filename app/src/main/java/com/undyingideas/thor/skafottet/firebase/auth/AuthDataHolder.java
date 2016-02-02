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

package com.undyingideas.thor.skafottet.firebase.auth;

import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.auth<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 15:15
 * </p>
 * Helper class for wrapping Runnable to facilitate easy use of {@link AuthListener}.<br>
 * Handles the integration in a very GC friendly manner.
 *
 */
public class AuthDataHolder extends WeakReferenceHolder<AuthDataHolder.AuthListenerData> implements Runnable {

    private boolean isLoggedIn;

    public AuthDataHolder(final AuthListenerData authListenerData) {
        super(authListenerData);
    }

    public interface AuthListenerData {
        void firebaseAuthDataReceived(final boolean loggedIn);
    }

    @Override
    public void run() {
        final AuthListenerData authListenerData = mWeakReference.get();
        if (authListenerData != null) {
            authListenerData.firebaseAuthDataReceived(isLoggedIn);
        }
    }

    public void setLoggedIn(final boolean newValue) { isLoggedIn = newValue; }

    public boolean isLoggedIn() { return isLoggedIn; }
}
