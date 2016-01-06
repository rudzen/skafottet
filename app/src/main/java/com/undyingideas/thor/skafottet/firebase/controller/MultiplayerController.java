package com.undyingideas.thor.skafottet.firebase.controller;

import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.highscore.Player;

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
    HashMap<String, PlayerDTO> playerList = new HashMap<>();
    HashMap<String, LobbyDTO> lobbyList = new HashMap<>();

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



    public void update() {
        PlayerDTO dto;
        for(String dtokey : playerList.keySet()) {
            dto = playerList.get(dtokey);
            Log.d("firebase update", dto.getName() + "  " + dto.getGameList().size());
        }
    }

    public void lobbyUpdate() {
        LobbyDTO dto;
        for(String dtokey : lobbyList.keySet()) {
            dto = lobbyList.get(dtokey);
            Log.d("firebase update", dtokey + "  " + dto.toString() + " " + dto.getPlayerList().size());
        }
    }
}
