package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyKeyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class PlayerController {

    final Firebase ref;

    public PlayerController(final Firebase ref){
        this.ref = ref;
    }

    public void createPlayer(final String name) {
        Firebase playersRef = ref.child("MultiPlayer").child("Players").child(name);
        fireBaseCreate h = new fireBaseCreate(name);
        playersRef.runTransaction(h);
    }

    public void getLobbyKey(final String name) {
        Firebase keyLobbyRef = ref.child("MultiPlayer").child("Players").child(name).child("gameList");
        keyLobbyRef.addValueEventListener(new FireBaseEventListener());
    }

    class FireBaseEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<LobbyKeyDTO> l = new ArrayList<>();
            for(DataSnapshot s : dataSnapshot.getChildren()) {
                l.add(s.getValue(LobbyKeyDTO.class));
            }
            LobbyController lc = new LobbyController(ref);
            lc.getGameWord(l.get(0).getKey(), "Rudy");
        }

        @Override
        public void onCancelled (FirebaseError er) {

        }
    }
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
        Log.d("firebase", "succes = " +b);
    }
}

