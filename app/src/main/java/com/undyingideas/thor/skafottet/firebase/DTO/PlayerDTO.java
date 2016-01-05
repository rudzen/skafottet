package com.undyingideas.thor.skafottet.firebase.DTO;

import java.util.ArrayList;

/**
 * Created by theis on 05-01-2016.
 */
public class PlayerDTO {

    String name;
    int score;
    ArrayList<String> gameList = new ArrayList<>();

    public PlayerDTO(){
    }

    public PlayerDTO(String name, int score, ArrayList<String> gameList) {
        this.name = name;
        this.score = score;
        this.gameList = gameList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<String> getGameList() {
        return gameList;
    }

    public void setGameList(ArrayList<String> gameList) {
        this.gameList = gameList;
    }
}