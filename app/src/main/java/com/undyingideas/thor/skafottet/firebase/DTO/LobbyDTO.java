package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyDTO {

    private ArrayList<LobbyPlayerStatus> playerList = new ArrayList<>();

    public LobbyDTO(){

    }

    public void add(final LobbyPlayerStatus lps) {
        playerList.add(lps);
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
