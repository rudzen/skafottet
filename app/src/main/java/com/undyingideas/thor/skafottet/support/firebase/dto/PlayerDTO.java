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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 *
 * @author theis
 *
 * 16-01-2016
 * - As parcel
 *
 * 29-01-2016
 * - Added fields to facilitate remote auth.
 * - Removed un-used fields.
 * - Replaced Player object with this.
 * @author rudz
 */
public class PlayerDTO implements Parcelable, Serializable {

    private static final long serialVersionUID = 7717445435298264329L;
    private int score;
    private String name;
    private String email;
    private HashMap<String, Object> timestampJoined;
    private boolean hasLoggedInWithPassword;
    private ArrayList<String> gameList = new ArrayList<>();

    /* define the shifting values for scores on future difficulty settings */
    public static final int DIF_EASY_SHIFT_MULTIPLIER = 2;
    public static final int DIF_MEDIUM_SHIFT_MULTIPLIER = 1;
    public static final int DIF_HARD_SHIFT_MULTIPLIER = 0;

    /* constructors */
    public PlayerDTO(final String name) {
        this.name = name;
    }

    public PlayerDTO(final PlayerDTO playerDTO) {
        name = playerDTO.getName();
        email = playerDTO.getEmail();
        score = playerDTO.getScore();
    }

    public PlayerDTO(final String userName, final String mEncodedEmail, final HashMap<String, Object> timestampJoined) {
        name = userName;
        email = mEncodedEmail;
        this.timestampJoined = timestampJoined;
    }

    /* helper methods */

    public void addPoints(final int pointsToAdd) {
        score += pointsToAdd;
    }

    /* getters and setters */

    public String getName() { return name; }

    public void setName(final String name) { this.name = name; }

    public int getScore() { return score; }

    public void setScore(final int score) { this.score = score; }

    public ArrayList<String> getGameList() { return gameList; }

    public void setGameList(final ArrayList<String> gameList) { this.gameList = gameList; }

    public String getEmail() { return email; }

    public void setEmail(final String email) { this.email = email; }

    public HashMap<String, Object> getTimestampJoined() { return timestampJoined; }

    public void setTimestampJoined(final HashMap<String, Object> timestampJoined) { this.timestampJoined = timestampJoined; }

    public boolean isHasLoggedInWithPassword() { return hasLoggedInWithPassword; }

    public void setHasLoggedInWithPassword(final boolean hasLoggedInWithPassword) { this.hasLoggedInWithPassword = hasLoggedInWithPassword; }

    /* begin parcelazation :) */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(score);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeSerializable(timestampJoined);
        dest.writeByte(hasLoggedInWithPassword ? (byte) 1 : (byte) 0);
        dest.writeStringList(gameList);
    }

    public PlayerDTO() { }

    protected PlayerDTO(final Parcel in) {
        score = in.readInt();
        name = in.readString();
        email = in.readString();
        timestampJoined = (HashMap<String, Object>) in.readSerializable();
        hasLoggedInWithPassword = in.readByte() != 0;
        gameList = in.createStringArrayList();
    }

    public static final Creator<PlayerDTO> CREATOR = new PlayerDTOCreator();

    private static class PlayerDTOCreator implements Creator<PlayerDTO> {
        @Override
        public PlayerDTO createFromParcel(final Parcel source) {
            return new PlayerDTO(source);
        }

        @Override
        public PlayerDTO[] newArray(final int size) {
            return new PlayerDTO[size];
        }
    }
}