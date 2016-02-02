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

package com.undyingideas.thor.skafottet.support.firebase.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * 16.01.2016, rudz
 * - As parcel
 *
 * @author theis
 */
public class LobbyPlayerStatus implements Parcelable {
    private String name;
    private int score;

    public LobbyPlayerStatus() {}

    public LobbyPlayerStatus(final String name, final int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() { return score; }

    public void setScore(final int score) { this.score = score; }

    public String getName() { return name; }

    public void setName(final String name) { this.name = name; }

    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeInt(score);
    }

    LobbyPlayerStatus(final Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    public static final Parcelable.Creator<LobbyPlayerStatus> CREATOR = new LobbyPlayerStatusCreator();

    private static class LobbyPlayerStatusCreator implements Creator<LobbyPlayerStatus> {
        @Override
        public LobbyPlayerStatus createFromParcel(final Parcel source) {return new LobbyPlayerStatus(source);}

        @Override
        public LobbyPlayerStatus[] newArray(final int size) {return new LobbyPlayerStatus[size];}
    }
}
