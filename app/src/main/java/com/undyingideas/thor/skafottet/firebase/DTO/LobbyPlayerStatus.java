package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyPlayerStatus {
    ArrayList<WordStatus> wordList = new ArrayList<>();

    public LobbyPlayerStatus(){}

    public LobbyPlayerStatus(ArrayList<WordStatus> wordList) {
        this.wordList = wordList;
    }

    public ArrayList<WordStatus> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<WordStatus> wordList) {
        this.wordList = wordList;
    }
}
