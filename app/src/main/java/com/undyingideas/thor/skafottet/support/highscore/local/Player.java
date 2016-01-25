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

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author rudz
 */
public final class Player implements Serializable, Parcelable {
    private static final long serialVersionUID = -3214121L;

    private int pts;
    private String name;

    /* define the shifting values for scores on future difficulty settings */
    public static final int DIF_EASY_SHIFT_MULTIPLIER = 2;
    public static final int DIF_MEDIUM_SHIFT_MULTIPLIER = 1;
    public static final int DIF_HARD_SHIFT_MULTIPLIER = 0;

    public Player(final String name, final int pts) {
        this.pts = pts;
        this.name = name;
    }

    public Player(final String name) {
        this(name, 0);
    }

    public Player(final Player player) {
        this(player.getName(), player.getPts());
    }

    public String getPtsString() {
        return Integer.toString(pts);
    }

    public void addPoints(final int points) { pts += points; }

    private int getPts() { return pts; }

    public void setPts(final int pts) { this.pts = pts; }

    public String getName() { return name; }

    public void setName(final String name) { this.name = name; }

    @Override
    public String toString() {
        return "Player{" +
                "pts=" + pts +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(pts);
        dest.writeString(name);
    }

    protected Player(final Parcel in) {
        pts = in.readInt();
        name = in.readString();
    }

    public static final Parcelable.Creator<Player> CREATOR = new PlayerCreator();

    private static class PlayerCreator implements Creator<Player> {
        @Override
        public Player createFromParcel(final Parcel source) {return new Player(source);}

        @Override
        public Player[] newArray(final int size) {return new Player[size];}
    }
}
