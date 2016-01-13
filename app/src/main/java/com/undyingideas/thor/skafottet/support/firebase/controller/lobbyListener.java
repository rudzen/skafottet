package com.undyingideas.thor.skafottet.support.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

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
        if (mpc.lc != null) {
            LobbyDTO dto;
            String key = dataSnapshot.getRef().getParent().getKey();
            if (! mpc.lc.lobbyList.containsKey(key)){
                dto = new LobbyDTO();
                mpc.lc.lobbyList.put(key, dto);
            } else {
                dto = mpc.lc.lobbyList.get(key);
            }
        }
        mpc.lobbyUpdate();
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        if (mpc.lc != null) {
            mpc.lc.lobbyList.remove(dataSnapshot.getRef().getParent().getKey());
            mpc.lc.lobbyList.put(dataSnapshot.getRef().getParent().getKey(), getDTO(dataSnapshot));
        }
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
        if (GameUtility.mpc.lc.lobbyList.containsKey(ds.getRef().getParent().getKey())
        final LobbyDTO dto = new LobbyDTO();
        dto.setWord(ds.child("word").getValue(String.class));
        for(final DataSnapshot s : ds.getChildren()) dto.add(new LobbyPlayerStatus(s.getKey(), Integer.valueOf(s.getValue(String.class))));

        Log.d("firebase", );
        //Log.d("firebase", dto.getWord() + dto.getPlayerList().get(0).getName() + dto.getPlayerList().get(1).getName());
        return dto;
    }
}