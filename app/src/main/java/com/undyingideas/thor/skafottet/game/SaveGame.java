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

package com.undyingideas.thor.skafottet.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created on 09-01-2016, 13:13.
 * Project : skafottet
 * SaveGame DTO, Parceable, used with GSON to save in prefs through TinyDB.
 * @author rudz
 */
public class SaveGame implements Parcelable {

    private HangedMan logic;
    private boolean multiPlayer;
    private String[] names;

    public SaveGame() { }

    public SaveGame(final HangedMan logic, final boolean multiPlayer, final String ... names) {
        this.logic = logic;
        this.multiPlayer = multiPlayer;
        this.names = names;
    }

    public HangedMan getLogic() { return logic; }

    public void setLogic(final HangedMan logic) { this.logic = logic; }

    public boolean isMultiPlayer() { return multiPlayer; }

    public void setMultiPlayer(final boolean multiPlayer) { this.multiPlayer = multiPlayer; }

    public String[] getNames() { return names; }

    public void setNames(final String ... names) { this.names = names; }

    @Override
    public String toString() {
        return "SaveGame{" +
                "names=" + Arrays.toString(names) +
                ", multiPlayer=" + multiPlayer +
                ", logic=" + logic +
                '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(logic, 0);
        dest.writeByte(multiPlayer ? (byte) 1 : (byte) 0);
        dest.writeStringArray(names);
    }

    protected SaveGame(final Parcel in) {
        logic = in.readParcelable(HangedMan.class.getClassLoader());
        multiPlayer = in.readByte() != 0;
        names = in.createStringArray();
    }

    public static final Creator<SaveGame> CREATOR = new SaveGameCreator();

    private static class SaveGameCreator implements Creator<SaveGame> {
        @Override
        public SaveGame createFromParcel(final Parcel source) {return new SaveGame(source);}

        @Override
        public SaveGame[] newArray(final int size) {return new SaveGame[size];}
    }
}
