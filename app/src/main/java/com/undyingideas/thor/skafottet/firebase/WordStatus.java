package com.undyingideas.thor.skafottet.firebase;

/**
 * Created by theis on 05-01-2016.
 */
public class WordStatus {
    String wordID;
    int score;

    public WordStatus(){}

    public WordStatus(String wordID, int score) {
        this.wordID = wordID;
        this.score = score;
    }

    public String getWordID() {
        return wordID;
    }

    public void setWordID(String wordID) {
        this.wordID = wordID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
