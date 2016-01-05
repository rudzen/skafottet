package com.undyingideas.thor.skafottet;

/**
 * Created on 04-01-2016, 06:34.
 * Project : skafottet
 * @author Gump
 */
public class PlayerScore {
    String name;
    int score;

    public PlayerScore(){

    }
    public PlayerScore(final String name, final int score){
        this.name = name;
        this.score = score;
    }


    public String getName() {        return name;    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    @Override
    public String toString(){
        return name + " " + score;
    }
}
