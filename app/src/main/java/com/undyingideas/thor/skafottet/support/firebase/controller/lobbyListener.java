package com.undyingideas.thor.skafottet.support.firebase.controller;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;

/**
 * Created on 07-01-2016, 12:13.
 * Project : skafottet
 * @author Theis'
 */
class LobbyListener implements ChildEventListener {
    private final MultiplayerController mpc;

    public LobbyListener(final MultiplayerController mpc) {
        this.mpc = mpc;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        mpc.lc.lobbyList.put(dataSnapshot.getRef().getParent().getKey(), getDTO(dataSnapshot));
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        mpc.lc.lobbyList.remove(dataSnapshot.getRef().getParent().getKey());
        mpc.lc.lobbyList.put(dataSnapshot.getRef().getParent().getKey(), getDTO(dataSnapshot));
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        mpc.lc.lobbyList.remove(dataSnapshot.getRef().getParent().getKey());
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        // void
    }

    @Override
    public void onCancelled(final FirebaseError firebaseError) {
        // void
    }

    private static LobbyDTO getDTO(final DataSnapshot ds) {
        final LobbyDTO dto = new LobbyDTO();
        for(final DataSnapshot s : ds.getChildren()) dto.add(s.getValue(LobbyPlayerStatus.class));
        return dto;
    }
}