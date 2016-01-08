package com.undyingideas.thor.skafottet.firebase.DTO;

/**
 * Created by theis on 05-01-2016.
 */
public class WordStatus {
    String wordID;
    int score;

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
