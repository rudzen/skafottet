package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class PlayerDTO {

    private String name;
    private int score;
    private ArrayList<String> gameList = new ArrayList<>();

    public PlayerDTO(){
    }

    public PlayerDTO(final String name) {
        this(name, 0, new ArrayList<String>());
    }

    public PlayerDTO(final String name, final int score, final ArrayList<String> gameList) {
        this.name = name;
        this.score = score;
        this.gameList = gameList;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public ArrayList<String> getGameList() {
        return gameList;
    }

    public void setGameList(final ArrayList<String> gameList) {
        this.gameList = gameList;
    }
}