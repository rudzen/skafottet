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

package com.undyingideas.thor.skafottet.firebase.highscore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 23-01-2016, 19:39.
 * Project : skafottet
 * Highscore class for firebase
 * @author rudz
 */
public class HighscoreDTO implements Parcelable {

    private String name;
    private int score;

    public HighscoreDTO(final String name, final int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }


    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeInt(score);
    }

    private HighscoreDTO(final Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    public static final Parcelable.Creator<HighscoreDTO> CREATOR = new FireBaseHighScoresCreator();

    private static class FireBaseHighScoresCreator implements Creator<HighscoreDTO> {
        @Override
        public HighscoreDTO createFromParcel(final Parcel source) {return new HighscoreDTO(source);}

        @Override
        public HighscoreDTO[] newArray(final int size) {return new HighscoreDTO[size];}
    }
}
