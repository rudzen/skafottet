package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyPlayerStatus {
    ArrayList<WordStatus> wordList = new ArrayList<>();
    String name;

    public LobbyPlayerStatus(){}

    public LobbyPlayerStatus(String name, ArrayList<WordStatus> wordList) {
        this.name = name;
        this.wordList = wordList;
    }

    public ArrayList<WordStatus> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<WordStatus> wordList) {
        this.wordList = wordList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
