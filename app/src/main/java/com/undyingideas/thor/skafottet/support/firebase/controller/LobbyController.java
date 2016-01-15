package com.undyingideas.thor.skafottet.support.firebase.controller;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;

import java.util.HashMap;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyController {

    private final Firebase ref;
    private final Firebase lobbyRef;
    private final MultiplayerController mpcRef;
    public HashMap<String, LobbyDTO> lobbyList = new HashMap<>();

    public LobbyController(final MultiplayerController mp, final Firebase ref){
        this.ref = ref;
        lobbyRef = ref.child("Lobby");
        mpcRef = mp;
    }

    public void createLobby( final LobbyDTO lobbyDTO){

        //This genetates uniqe ID in firebase
        final Firebase newPostRef = lobbyRef.push();

        newPostRef.child("word").setValue(lobbyDTO.getWord());
        for(LobbyPlayerStatus status: lobbyDTO.getPlayerList())
                newPostRef.child(status.getName()).setValue(status.getScore());

        // Add lobby id to players lobby list
        final String postId = newPostRef.getKey();
        for(final LobbyPlayerStatus s : lobbyDTO.getPlayerList()) {
            ref.child("Players").child(s.getName()).child("gameList").push().setValue(postId);
        }
    }

    public void addLobbyListener(final String key) {
        ref.child("Lobby").child(key).addChildEventListener(new LobbyListener(mpcRef));
    }

    public void updateLobby(String s, String s1, int numWrongLetters) {
        ref.child("Lobby").child(s).child(s1).setValue(numWrongLetters);
    }
}
