package com.undyingideas.thor.skafottet.firebase.DTO;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class WordStatus {
    private String wordID;
    private int score;

    public WordStatus(){}

    public WordStatus(final String wordID, final int score) {
        this.wordID = wordID;
        this.score = score;
    }

    public String getWordID() {
        return wordID;
    }

    public void setWordID(final String wordID) {
        this.wordID = wordID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }
}
