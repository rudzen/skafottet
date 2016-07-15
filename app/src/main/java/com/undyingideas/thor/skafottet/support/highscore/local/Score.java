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
    private final String mName;
    private final String mWord;
    private int mScore;
    private Date mDate;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss", Locale.US);

    public Score(final String word, final String name, final int score, final Date date) {
        mWord = word;
        mScore = score;
        mName = name;
        mDate = date;
    }

    public Score(final Score score) {
        this(score.getmWord(), score.getmName(), score.getmScore(), score.getmDate());
    }

    public String getmWord() { return mWord; }

    public int getmScore() { return mScore; }

    public String getmName() { return mName; }

    public void setmScore(final int mScore) { this.mScore = mScore; }

    public Date getmDate() { return mDate; }

    public void setmDate(final Date mDate) { this.mDate = mDate; }

    public String getDateString() { return formatter.format(mDate); }

    @Override
    public String toString() {
        return "Score {" + "mName='" + mName + '\'' + ", mWord='" + mWord + '\'' + ", mScore=" + mScore + ", " + getDateString() + '}';
    }

    @Override
    public int compareTo(@NonNull final Score anotherScore) {
        return anotherScore.getmScore() - mScore;
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

        return mScore == score1.getmScore() && mName.equals(score1.getmName()) && mWord.equals(score1.getmWord()) && mDate.equals(score1.getmDate());

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mWord.hashCode();
        result = 31 * result + mScore;
        result = 31 * result + mDate.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mName);
        dest.writeString(mWord);
        dest.writeInt(mScore);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
    }

    protected Score(final Parcel in) {
        mName = in.readString();
        mWord = in.readString();
        mScore = in.readInt();
        final long tmpDate = in.readLong();
        mDate = tmpDate == -1 ? null : new Date(tmpDate);
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
