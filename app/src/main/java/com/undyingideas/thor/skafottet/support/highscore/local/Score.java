/*
 * Copyright 2016 Rudy Alex Kohn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.undyingideas.thor.skafottet.support.highscore.local;

import java.io.Serializable;
import java.util.Comparator;

class Score implements Serializable, Comparator<Score> {
    private static final long serialVersionUID = -7482559749006887621L;
    private final String name;
    private final String word;
    private final int score, day, month, year;

    public Score(final String word, final String name, final int score, final int day, final int month, final int year) {
        this.word = word;
        this.score = score;
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Score(final Score score) {
        this(score.getWord(), score.getName(), score.getScore(), score.getDay(), score.getMonth(), score.getYear());
    }

    private String getWord() {
        return word;
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

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public int compare(final Score o1, final Score o2) {
        if (o1.score > o2.score) return 1;
        if (o1.score < o2.score) return -1;

        if (o1.word.length() > o2.word.length()) return 1;
        if (o1.word.length() < o2.word.length()) return -1;

        if (o1.year > o2.year) return 1;
        if (o1.year < o2.year) return -1;

        if (o1.month > o2.month) return 1;
        if (o1.month < o2.month) return -1;

        if (o1.day > o2.day) return 1;
        if (o1.day < o2.day) return -1;

        return 0;
    }
}
