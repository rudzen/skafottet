package com.undyingideas.thor.skafottet.firebase.controller;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;

import java.util.ArrayList;

/**
 * Created by theis on 06-01-2016.
 */
public class MultiplayerController {
    ArrayList<LobbyDTO> games;
    LobbyController lc;
    PlayerController pc;
    final Firebase ref;
    String name;

    public MultiplayerController(Firebase ref){
        this.ref = ref;
        games = new ArrayList<>();
        lc = new LobbyController(this, ref);
        pc = new PlayerController(this, ref);
    }

    public void createLobby(LobbyDTO dto) {
        lc.createLobby(dto);
    }

    public ArrayList<LobbyDTO> getActiveGames() {
        return null;
    }

}
