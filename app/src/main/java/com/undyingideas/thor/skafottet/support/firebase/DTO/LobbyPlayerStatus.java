package com.undyingideas.thor.skafottet.support.firebase.DTO;

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

    protected LobbyPlayerStatus(final Parcel in) {
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
