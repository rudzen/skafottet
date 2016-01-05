package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class PlayerController {

    Firebase ref;

    public PlayerController(final Firebase ref){
        this.ref = ref;
    }

    public void createPlayer(final String name) {
        Firebase playersRef = ref.child("MultiPlayer").child("Players").child(name);
        fireBaseCreate h = new fireBaseCreate(name);
        playersRef.runTransaction(h);
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