package com.undyingideas.thor.skafottet.firebase.auth;

import android.os.Handler;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.auth<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 15:12
 * </p>
 * Generic auth listener while in the game.<br>
 * This auth listener IS NOT used when the game launches, as that situation required some special handling depending on the user settings.
 */
public class AuthListener implements Firebase.AuthStateListener {

    private final ArrayList<AuthDataHolder> slaves = new ArrayList<>();
    private final Handler handler = new Handler();

    @Override
    public void onAuthStateChanged(final AuthData authData) {
        final boolean loggedIn = authData != null;
        for (final AuthDataHolder data : slaves) {
            data.setLoggedIn(loggedIn);
            handler.post(data);
        }
    }

    public void addSlave(final AuthDataHolder toAdd) {
        slaves.add(toAdd);
        slaves.trimToSize();
    }

    public void removeSlave(final AuthDataHolder toRemove) {
        slaves.remove(toRemove);
        slaves.trimToSize();
    }

    public void clearSlaves() {
        slaves.clear();
        slaves.trimToSize();
    }

}
