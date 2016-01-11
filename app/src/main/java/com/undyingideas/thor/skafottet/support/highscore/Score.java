/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.highscore;

import java.io.Serializable;
import java.util.Comparator;

class Score implements Serializable, Comparator<Score> {
    private static final long serialVersionUID = -7482559749006887621L;
    private final String name;
    private final int score, day, month, year;

    public Score(final String name, final int score, final int day, final int month, final int year) {
        this.score = score;
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Score(final Score s) {
        this(s.getName(), s.getScore(), s.getDay(), s.getMonth(), s.getYear());
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    private int getDay() {
        return day;
    }

    private int getMonth() {
        return month;
    }

    private int getYear() {
        return year;
    }

    @Override
    public int compare(final Score o1, final Score o2) {
        if (o1.getScore() < o2.getScore()) return 1;
        if (o1.getScore() > o2.getScore()) return -1;

        if (o1.getYear() > o2.getYear()) return 1;
        if (o1.getYear() < o2.getYear()) return -1;

        if (o1.getMonth() > o2.getMonth()) return 1;
        if (o1.getMonth() < o2.getMonth()) return -1;

        if (o1.getDay() > o2.getDay()) return 1;
        if (o1.getDay() < o2.getDay()) return -1;

        return 0;
    }
}
