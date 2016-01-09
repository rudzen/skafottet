package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.utility.Constant;

import java.util.HashMap;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class PlayerController {

    private final Firebase ref;
    private final MultiplayerController mpcRef;
    public HashMap<String, PlayerDTO> playerList = new HashMap<>();

    public PlayerController(final MultiplayerController mp, final Firebase ref){
        this.ref = ref;
        mpcRef = mp;
        this.ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Players").addChildEventListener(new NameGetter(mpcRef));
    }

    public void createPlayer(final String name) {
        final Firebase playersRef = ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Players").child(name);
        final fireBaseCreate h = new fireBaseCreate(name);
        playersRef.runTransaction(h);
    }

    public void addListener(final String name) {
        ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Players").child(name).child("gameList").addChildEventListener(new GameListListener(mpcRef));
    }
}

class fireBaseCreate implements Transaction.Handler{
    private boolean succes;
    private final String name;
    fireBaseCreate(final String name){
        this.name = name;
}

    @Override
    public Transaction.Result doTransaction(final MutableData mutableData) {
        if (mutableData.getValue() == null) {
            mutableData.setValue(new PlayerDTO(name));
            return Transaction.success(mutableData);
        } else {
            return Transaction.abort();
        }
    }

    @Override
    public void onComplete(final FirebaseError firebaseError, final boolean b, final DataSnapshot dataSnapshot) {
        succes = b;
    }
}

class NameGetter implements ChildEventListener {
    private final MultiplayerController mpcref;

    public NameGetter(final MultiplayerController mpcref) {
        this.mpcref = mpcref;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        mpcref.pc.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
        mpcref.update();
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        mpcref.pc.playerList.remove(dataSnapshot.getKey());
        mpcref.pc.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
        mpcref.update();
    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        mpcref.pc.playerList.remove(dataSnapshot.getKey());
        mpcref.update();
    }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        // void
    }

    @Override
    public void onCancelled(final FirebaseError firebaseError) {
        // void
    }

    private static PlayerDTO getDTO(final DataSnapshot dataSnapshot) {
        final PlayerDTO dto = new PlayerDTO(dataSnapshot.getKey());
        dto.setScore(Integer.valueOf(dataSnapshot.child("score").getValue().toString()));
        if (dataSnapshot.hasChild("gameList"))
            for(final DataSnapshot ds : dataSnapshot.child("gameList").getChildren())
                dto.getGameList().add(ds.getValue().toString());
        return dto;
    }
}

class GameListListener implements ChildEventListener {
    private final MultiplayerController mpc;

    GameListListener(final MultiplayerController mpc) {
        this.mpc = mpc;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        mpc.lc.addLobbyListener(dataSnapshot.getValue().toString());
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        // shouldn't happen
        Log.d("firebaseError", "childchangede"+dataSnapshot.toString());
    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        Log.d("firebase childremoved", dataSnapshot.toString());
    }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        // shouldn't happen
        Log.d("firebaseError", "childMoved"+dataSnapshot.toString());
    }

    @Override
    public void onCancelled(final FirebaseError firebaseError) {
        Log.d("firebaseError", "cancelled"+firebaseError.toString());
    }
}