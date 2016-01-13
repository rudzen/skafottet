package com.undyingideas.thor.skafottet.support.firebase.DTO;

import android.util.ArrayMap;
import android.util.ArraySet;

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

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<LobbyPlayerStatus> getPlayerList() {
        ArrayList<LobbyPlayerStatus> a = new ArrayList<>(playerList.values());
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
        for(LobbyPlayerStatus status: playerList.values()) s+= status.getName() + " ";
        return s;
    }

    public void put(String key, LobbyPlayerStatus lobbyPlayerStatus) {
        playerList.put(key, lobbyPlayerStatus);
    }
}
