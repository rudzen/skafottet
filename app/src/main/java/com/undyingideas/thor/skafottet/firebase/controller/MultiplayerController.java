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

    public ArrayList<LobbyDTO> getActiveGames() {
        return null;
    }


    public void update() {
//        PlayerDTO dto;
//        for(Map.Entry<String, PlayerDTO> stringPlayerDTOEntry : playerList.entrySet()) {
//            dto = stringPlayerDTOEntry.getValue();
//            Log.d("firebase update", dto.getName() + "  " + dto.getGameList().size());
//        }
        updateHandler.post(playerUpdater);
    }

    public void lobbyUpdate() {
        LobbyDTO dto;
        for(String dtokey : lobbyList.keySet()) {
            dto = lobbyList.get(dtokey);
            Log.d("firebase update", dtokey + "  " + dto.toString() + " " + dto.getPlayerList().size());
        }
    }
}
