package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyKeyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class PlayerController {

    final Firebase ref;
    final MultiplayerController mpcRef;

    public PlayerController(final MultiplayerController mp, final Firebase ref){
        this.ref = ref;
        this.mpcRef = mp;
        ref.child("MultiPlayer").child("Players").addChildEventListener(new NameGetter(mpcRef));
    }

    public void createPlayer(final String name) {
        Firebase playersRef = ref.child("MultiPlayer").child("Players").child(name);
        fireBaseCreate h = new fireBaseCreate(name);
        playersRef.runTransaction(h);
    }

    public void getLobbyDTOByLobbyKey(final String name) {
        Firebase keyLobbyRef = ref.child("MultiPlayer").child("Players").child(name).child("gameList");
        keyLobbyRef.addChildEventListener(new LobbyEventListenter(ref,name));
    }


  //  public void getGameWord(String lobbyId, String playerName) {
  //      ref.child("Lobby").child(lobbyId).child("Lobby").child("playerList").addValueEventListener(new LobbyEventListener());
 //   }

}

class fireBaseCreate implements Transaction.Handler{
    boolean succes;
    final String name;
    fireBaseCreate(String name){
        this.name = name;
}

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        if (mutableData.getValue() == null) {
            mutableData.setValue(new PlayerDTO(name));
            return Transaction.success(mutableData);
        } else {
            return Transaction.abort();
        }
    }

    @Override
    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
        succes = b;
        Log.d("firebase", "succes = " + b);
    }
}

class NameGetter implements ChildEventListener {
    final MultiplayerController mpcref;

    public NameGetter(MultiplayerController mpcref) {
        this.mpcref = mpcref;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        mpcref.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
        mpcref.update();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mpcref.playerList.remove(dataSnapshot.getKey());
        mpcref.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
        mpcref.update();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        mpcref.playerList.remove(dataSnapshot.getKey());
        mpcref.update();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // void
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        // void
    }

    protected PlayerDTO getDTO(DataSnapshot dataSnapshot) {
        PlayerDTO dto = new PlayerDTO(dataSnapshot.getKey());
        dto.setScore(Integer.valueOf(dataSnapshot.child("score").getValue().toString()));
        if (dataSnapshot.hasChild("gameList"))
            for(DataSnapshot ds : dataSnapshot.child("gameList").getChildren())
                dto.getGameList().add(ds.getValue().toString());
        return dto;
    }
}