package com.undyingideas.thor.skafottet.support.firebase.DTO;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyPlayerStatus {
    private ArrayList<WordStatus> wordList = new ArrayList<>();
    private String name;

    public LobbyPlayerStatus(){}

    public LobbyPlayerStatus(final String name, final ArrayList<WordStatus> wordList) {
        this.name = name;
        this.wordList = wordList;
    }

    public ArrayList<WordStatus> getWordList() {
        return wordList;
    }

    public void setWordList(final ArrayList<WordStatus> wordList) {
        this.wordList = wordList;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
