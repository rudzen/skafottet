/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Score implements Comparable<Score>, Serializable, Parcelable {
    private static final long serialVersionUID = -7482559749006887621L;
    private final String name;
    private final String word;
    private int score;
    private Date date;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss", Locale.US);

    public Score(final String word, final String name, final int score, final Date date) {
        this.word = word;
        this.score = score;
        this.name = name;
        this.date = date;
    }

    public Score(final Score score) {
        this(score.getWord(), score.getName(), score.getScore(), score.getDate());
    }

    public String getWord() { return word; }

    public int getScore() { return score; }

    public String getName() { return name; }

    public void setScore(final int score) { this.score = score; }

    public Date getDate() { return date; }

    public void setDate(final Date date) { this.date = date; }

    public String getDateString() { return formatter.format(date); }

    @Override
    public String toString() {
        return "Score {" + "name='" + name + '\'' + ", word='" + word + '\'' + ", score=" + score + ", " + getDateString() + '}';
    }

    @Override
    public int compareTo(@NonNull final Score anotherScore) {
        return anotherScore.getScore() - score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(word);
        dest.writeInt(score);
        dest.writeLong(date != null ? date.getTime() : -1);
    }

    protected Score(final Parcel in) {
        name = in.readString();
        word = in.readString();
        score = in.readInt();
        final long tmpDate = in.readLong();
        date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<Score> CREATOR = new ScoreCreator();

    private static class ScoreCreator implements Creator<Score> {
        @Override
        public Score createFromParcel(final Parcel source) {
            return new Score(source);
        }

        @Override
        public Score[] newArray(final int size) {
            return new Score[size];
        }
    }
}
