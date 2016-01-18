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
