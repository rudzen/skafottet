package com.undyingideas.thor.skafottet;

/**
 * Created by Gump on 04-01-2016.
 */
public class PlayerScore {
    String name;
    int score;

    public PlayerScore(){

    }
    public PlayerScore(String name, int score){
        this.name = name;
        this.score = score;
    }


    public String getName() {        return name;    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString(){
        return name + " " + score;
    }
}
