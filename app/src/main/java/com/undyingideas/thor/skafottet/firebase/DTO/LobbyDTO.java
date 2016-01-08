package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyDTO {

    ArrayList<LobbyPlayerStatus> playerList = new ArrayList<>();

    public LobbyDTO(){

    }

    public void add(final LobbyPlayerStatus lps) {
        this.playerList.add(lps);
    }

    public LobbyDTO(final ArrayList<LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<LobbyPlayerStatus> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(final ArrayList<LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }
}
