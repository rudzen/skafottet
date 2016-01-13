package com.undyingideas.thor.skafottet.support.firebase.DTO;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyDTO {

    private String word;
    private ArrayList<LobbyPlayerStatus> playerList = new ArrayList<>();

    public LobbyDTO(){

    }

    public void add(final LobbyPlayerStatus lps) {
        playerList.add(lps);
    }

    public LobbyDTO(final String word, final ArrayList<LobbyPlayerStatus> playerList) {
        this.word = word;
        this.playerList = playerList;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<LobbyPlayerStatus> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(final ArrayList<LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }
}
