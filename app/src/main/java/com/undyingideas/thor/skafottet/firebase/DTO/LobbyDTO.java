package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyDTO {

    ArrayList<LobbyPlayerStatus> playerList = new ArrayList<>();

    public LobbyDTO(){

    }

    public LobbyDTO(ArrayList<LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<LobbyPlayerStatus> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }
}
