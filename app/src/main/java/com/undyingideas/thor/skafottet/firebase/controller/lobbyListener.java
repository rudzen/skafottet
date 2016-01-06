package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;

/**
 * Created by theis on 06-01-2016.
 */
public class lobbyListener implements ChildEventListener {
    final MultiplayerController mpcRef;
    public lobbyListener(MultiplayerController mpcRef) {
        this.mpcRef = mpcRef;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        mpcRef.lobbyList.put(dataSnapshot.getKey(), dataSnapshot.child("Lobby").getValue(LobbyDTO.class));
        mpcRef.lobbyUpdate();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mpcRef.lobbyList.remove(dataSnapshot.getKey());
        mpcRef.lobbyList.put(dataSnapshot.getKey(), dataSnapshot.child("Lobby").getValue(LobbyDTO.class));
        mpcRef.lobbyUpdate();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        mpcRef.lobbyList.remove(dataSnapshot.getKey());
        mpcRef.lobbyUpdate();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // void
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        // void
    }
}
