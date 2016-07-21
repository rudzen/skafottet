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
import java.util.Calendar;
import java.util.Locale;

public class Score implements Comparable<Score>, Serializable, Parcelable {
    private static final long serialVersionUID = -7482559749006887621L;
    private final String mName;
    private final String mWord;
    private int mScore;
    private long mDate;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss", Locale.US);

    public Score(final String word, final String name, final int score) {
        mWord = word;
        mScore = score;
        mName = name;
        mDate = Calendar.getInstance().getTime().getTime();
    }

    public Score(final String word, final String name, final int score, final long date) {
        mWord = word;
        mScore = score;
        mName = name;
        mDate = date;
    }

    public Score(final Score score) {
        this(score.getWord(), score.getName(), score.getScore(), score.getDate());
    }

    public String getWord() { return mWord; }

    public int getScore() { return mScore; }

    public String getName() { return mName; }

    public void setScore(final int score) { mScore = score; }

    public long getDate() { return mDate; }

    public void setDate(final long date) { mDate = date; }

    public String getDateString() { return formatter.format(mDate); }

    @Override
    public String toString() {
        return "Score {" + "mName='" + mName + '\'' + ", mWord='" + mWord + '\'' + ", mScore=" + mScore + ", " + getDateString() + '}';
    }

    @Override
    public int compareTo(@NonNull final Score anotherScore) {
        return anotherScore.getScore() - mScore;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Score score1 = (Score) o;

        return mDate == score1.getDate() && mScore == score1.getScore() && mName.equals(score1.getName()) && mWord.equals(score1.getWord());

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mWord.hashCode();
        result = 31 * result + mScore;
        result = 31 * result + (int) (mDate ^ mDate >>> 32);
        return result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mName);
        dest.writeString(mWord);
        dest.writeInt(mScore);
        dest.writeLong(mDate);
    }

    protected Score(final Parcel in) {
        mName = in.readString();
        mWord = in.readString();
        mScore = in.readInt();
        mDate = in.readLong();
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
