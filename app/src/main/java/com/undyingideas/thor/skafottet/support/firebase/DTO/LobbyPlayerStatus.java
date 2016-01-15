package com.undyingideas.thor.skafottet.support.firebase.DTO;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyPlayerStatus {
    private String name;
    private int score;

    public LobbyPlayerStatus(){}

    public LobbyPlayerStatus(final String name, final int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
