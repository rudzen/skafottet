package com.undyingideas.thor.skafottet.firebase.controller;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyController{

    Firebase ref;
    public LobbyController(final Firebase ref) {
        this.ref = ref;

    }

    public

    class FireBaseEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
