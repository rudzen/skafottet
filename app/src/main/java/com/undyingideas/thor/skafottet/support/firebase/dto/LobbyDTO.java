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
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * 16-01-2016, rudz
 * - As parcel
 *
 * @author theis
 */
public class LobbyDTO implements Parcelable {

    private String word;
    private ArrayMap<String, LobbyPlayerStatus> playerList = new ArrayMap<>();

    public LobbyDTO() { }

    public LobbyDTO(final String word, final ArrayMap<String, LobbyPlayerStatus> playerList) {
        this.word = word;
        this.playerList = playerList;
    }

    public LobbyDTO(final LobbyDTO lobbyDTO) {
        word = lobbyDTO.getWord();
        playerList = lobbyDTO.getPlayerList();
    }

    public void add(final LobbyPlayerStatus lps) { playerList.put(lps.getName(), lps); }

    public void put(final String key, final LobbyPlayerStatus lobbyPlayerStatus) { playerList.put(key, lobbyPlayerStatus); }

    public String getWord() { return word; }

    public void setWord(final String word) { this.word = word; }

    public ArrayMap<String, LobbyPlayerStatus> getPlayerList() { return playerList; }

    public void setPlayerList(final ArrayMap<String, LobbyPlayerStatus> playerList) { this.playerList = playerList; }

    public ArrayList<LobbyPlayerStatus> getPlayerListArray() { return new ArrayList<>(playerList.values()); }

    public boolean isDone() {
        int doneCount = 0;
        for (final Map.Entry<String, LobbyPlayerStatus> stringLobbyPlayerStatusEntry : playerList.entrySet()) {
            if (stringLobbyPlayerStatusEntry.getValue().getScore() > 0) {
                doneCount++;
            }
        }
        return doneCount == playerList.size();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(playerList.values().size() << 2);
//        String s = "";
        sb.append("Word = ").append(word);
        sb.append(" , ");
        sb.append("names = ");
//        s += "Word = " + word;
//        s += " , names = ";
        for (final LobbyPlayerStatus status : playerList.values()) sb.append(status.getName()).append(' '); // += status.getmName() + " ";
        return sb.toString();
    }

    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(word);

        // this is just a simple method of writing the whole damn thing..
        // REASON : LobbyPlayerStatus is ALSO a Parcel :-)
        dest.writeInt(playerList.size());
        for (final Map.Entry<String, LobbyPlayerStatus> stringLobbyPlayerStatusEntry : playerList.entrySet()) {
            dest.writeString(stringLobbyPlayerStatusEntry.getKey());
            dest.writeParcelable(stringLobbyPlayerStatusEntry.getValue(), flags);
        }
    }

    private LobbyDTO(final Parcel in) {
        word = in.readString();
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            playerList.put(in.readString(), (LobbyPlayerStatus) in.readParcelable(LobbyPlayerStatus.class.getClassLoader()));
        }
    }

    public static final Parcelable.Creator<LobbyDTO> CREATOR = new LobbyDTOCreator();

    private static class LobbyDTOCreator implements Creator<LobbyDTO> {
        @Override
        public LobbyDTO createFromParcel(final Parcel source) {return new LobbyDTO(source);}

        @Override
        public LobbyDTO[] newArray(final int size) {return new LobbyDTO[size];}
    }
}
