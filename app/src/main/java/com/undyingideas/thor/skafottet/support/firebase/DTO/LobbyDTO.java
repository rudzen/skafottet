package com.undyingideas.thor.skafottet.support.firebase.DTO;

import android.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyDTO {

    private String word;
    private ArrayMap<String, LobbyPlayerStatus> playerList = new ArrayMap<>();

    public LobbyDTO(){

    }

    public void add(final LobbyPlayerStatus lps) {
        playerList.put(lps.getName(), lps);
    }

    public LobbyDTO(final String word, final ArrayMap<String, LobbyPlayerStatus> playerList) {
        this.word = word;
        this.playerList = playerList;
    }

    public String getWord() {
        return word;
    }

    public void setWord(final String word) {
        this.word = word;
    }

    public ArrayList<LobbyPlayerStatus> getPlayerList() {
        final ArrayList<LobbyPlayerStatus> a = new ArrayList<>(playerList.values());
        return a;
    }

    public void setPlayerList(final ArrayMap<String, LobbyPlayerStatus> playerList) {
        this.playerList = playerList;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Word = "+word;
        s += " , names = ";
        for(final LobbyPlayerStatus status: playerList.values()) s+= status.getName() + " ";
        return s;
    }

    public void put(final String key, final LobbyPlayerStatus lobbyPlayerStatus) {
        playerList.put(key, lobbyPlayerStatus);
    }
}
