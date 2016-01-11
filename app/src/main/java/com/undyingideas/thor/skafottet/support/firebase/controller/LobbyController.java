package com.undyingideas.thor.skafottet.support.firebase.controller;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;

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
        lobbyRef = ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Lobby");
        mpcRef = mp;
    }

    public void createLobby( final LobbyDTO lobbyDTO){

        //This genetates uniqe ID in firebase
        final Firebase newPostRef = lobbyRef.push();

        //Putting the player object into a hashmap with "player" key.

        //final Map<String, LobbyDTO> post1 = new HashMap<>();
        //post1.put("Lobby", lobbyDTO);
        newPostRef.setValue(lobbyDTO);

        // Get the unique ID generated by push()
        final String postId = newPostRef.getKey();
        for(final LobbyPlayerStatus s : lobbyDTO.getPlayerList()) {
            ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Players").child(s.getName()).child("gameList").push().setValue(postId);
        }
    }

    public void addLobbyListener(final String key) {
        ref.child(Constant.FIREBASE_MULTI_PLAYER).child("Lobby").child(key).addChildEventListener(new LobbyListener(mpcRef));
    }
}