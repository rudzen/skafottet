package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;

/**
 * Created by Theis' on 07-01-2016.
 */
class LobbyListener implements ChildEventListener {
    final MultiplayerController mpc;

    public LobbyListener(MultiplayerController mpc) {
        this.mpc = mpc;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        mpc.lc.lobbyList.put(dataSnapshot.getRef().getParent().getKey(), getDTO(dataSnapshot));
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mpc.lc.lobbyList.remove(dataSnapshot.getRef().getParent().getKey());
        mpc.lc.lobbyList.put(dataSnapshot.getRef().getParent().getKey(), getDTO(dataSnapshot));
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        mpc.lc.lobbyList.remove(dataSnapshot.getRef().getParent().getKey());
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // void
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        // void
    }

    protected LobbyDTO getDTO(DataSnapshot ds) {
        LobbyDTO dto = new LobbyDTO();
        for(DataSnapshot s : ds.getChildren()) dto.add(s.getValue(LobbyPlayerStatus.class));
        return dto;
    }
}