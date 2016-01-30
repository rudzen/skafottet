package com.undyingideas.thor.skafottet.firebase.challenge;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.challenge<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:09
 * </p>
 *
 * When someone issues a multiplayer challenge, it will be added to the targets gameList.<br>
 * This class will on login retrieve the current gameList and monitor changes.
 * It acts as an regular observer class except that it utilizes the {@link ChallengeListenerSlave} class to deliver
 * its payload through the ChallengeListenerSlaveAction interface.
 */
public class ChallengeListener implements Firebase.ResultHandler, ChildEventListener {




    public ChallengeListener() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(final FirebaseError firebaseError) {

    }


    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {

    }

    @Override
    public void onCancelled(final FirebaseError firebaseError) {

    }
}
