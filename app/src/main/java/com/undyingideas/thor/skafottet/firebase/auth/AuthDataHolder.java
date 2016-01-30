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
        final AuthListenerData authListenerData = weakReference.get();
        if (authListenerData != null) {
            authListenerData.firebaseAuthDataReceived(isLoggedIn);
        }
    }

    public void setLoggedIn(final boolean newValue) { isLoggedIn = newValue; }

    public boolean isLoggedIn() { return isLoggedIn; }
}
