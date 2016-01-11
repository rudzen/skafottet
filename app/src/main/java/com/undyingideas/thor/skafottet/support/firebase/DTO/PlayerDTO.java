package com.undyingideas.thor.skafottet.support.firebase.DTO;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class PlayerDTO {

    private String name;
    private int score;
    private String password;
    private ArrayList<String> gameList = new ArrayList<>();

    public PlayerDTO(){
    }

    public PlayerDTO(final String name) {
        this(name, 0, new ArrayList<String>(),null);
    }


    public PlayerDTO(final String name, final String password) {
        this(name, 0, new ArrayList<String>(),password);
    }

    public PlayerDTO(final String name, final int score, final ArrayList<String> gameList, final String password) {
        this.name = name;
        this.score = score;
        this.gameList = gameList;
        this.password = password;
    }

    public PlayerDTO(final String name, final int score, final ArrayList<String> gameList){
        this.name = name;
        this.score = score;
        this.gameList = gameList;
        password = "1";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}