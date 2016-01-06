package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyKeyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyEventListenter implements ChildEventListener {



    Firebase ref;
    Firebase keyLobbyRef;


    public LobbyEventListenter(Firebase ref,String name){
        this.ref= ref;
        keyLobbyRef = ref.child("MultiPlayer").child("Players").child(name).child("gameList");
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("firebase",""+dataSnapshot.getKey() + "   " + dataSnapshot.getValue().toString());
        String lobbykey = dataSnapshot.getValue().toString();

        ref.child("Lobby").child(lobbykey)
                .child("Lobby").child("playerList").addValueEventListener(new MyValueEventListener());
        Log.d("firebase", "Valueeventlistener created");


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private static class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            LobbyDTO dto = new LobbyDTO();
            for (DataSnapshot s : dataSnapshot.getChildren()){
                dto.add(s.getValue(LobbyPlayerStatus.class));

            }
            for(LobbyPlayerStatus s : dto.getPlayerList())
                    Log.d("firebase dto", s.getName());
            //LobbyDTO dto = dataSnapshot.getRef().getValue(LobbyDTO.class);
            Log.d("firebase",""+ dataSnapshot.getRef());

           // Log.d("firebase", dto.getPlayerList().get(0).getName());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
