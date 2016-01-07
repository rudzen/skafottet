package com.undyingideas.thor.skafottet.firebase.controller;

import android.os.Handler;
import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by theis on 06-01-2016.
 */
public class MultiplayerController {
    public ArrayList<LobbyDTO> games;
    public LobbyController lc;
    public PlayerController pc;
    final Firebase ref;
    String name;
    public HashMap<String, PlayerDTO> playerList = new HashMap<>();
    HashMap<String, LobbyDTO> lobbyList = new HashMap<>();
    private Handler updateHandler;
    private Runnable playerUpdater;

    public MultiplayerController(final Firebase ref, final Runnable playerUpdater){
        this.ref = ref;
        games = new ArrayList<>();
        lc = new LobbyController(this, ref);
        pc = new PlayerController(this, ref);
        updateHandler = new Handler();
        this.playerUpdater = playerUpdater;
    }

    public void createLobby(LobbyDTO dto) {
        lc.createLobby(dto);
    }

    public boolean login(String name) {
        if (playerList.containsKey(name)){
            logout();
            this.name = name;
            pc.addListener(name);
            return true;
        } else return false;
    }

    public void logout() {
        if (name == null) return;
        lc = new LobbyController(this, ref);
        games.clear();
    }

    public void update() {
        updateHandler.post(playerUpdater);
    }

    public void lobbyUpdate() {
        updateHandler.post(playerUpdater);
    }
}
