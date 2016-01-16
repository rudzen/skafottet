package com.undyingideas.thor.skafottet.support.firebase.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 *
 * @author theis
 *
 * 16-01-2016
 * - As parcel
 * @author rudz
 */
public class PlayerDTO implements Parcelable {

    private String name;
    private int score;
    private String password;
    private ArrayList<String> gameList = new ArrayList<>();

    public PlayerDTO() { }

    public PlayerDTO(final String name) {
        this(name, 0, new ArrayList<String>(), null);
    }

    public PlayerDTO(final String name, final String password) {
        this(name, 0, new ArrayList<String>(), password);
    }

    public PlayerDTO(final String name, final int score, final ArrayList<String> gameList, final String password) {
        this.name = name;
        this.score = score;
        this.gameList = gameList;
        this.password = password;
    }

    public String getName() { return name; }

    public void setName(final String name) { this.name = name; }

    public int getScore() { return score; }

    public void setScore(final int score) { this.score = score; }

    public ArrayList<String> getGameList() { return gameList; }

    public void setGameList(final ArrayList<String> gameList) { this.gameList = gameList; }

    public String getPassword() { return password; }

    public void setPassword(final String password) { this.password = password; }


    /* begin parcelazation :) */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeInt(score);
        dest.writeStringList(gameList);
    }

    protected PlayerDTO(final Parcel in) {
        name = in.readString();
        password = in.readString();
        score = in.readInt();
        gameList = in.createStringArrayList();
    }

    public static final Parcelable.Creator<PlayerDTO> CREATOR = new PlayerDTOCreator();

    private static class PlayerDTOCreator implements Creator<PlayerDTO> {
        @Override
        public PlayerDTO createFromParcel(final Parcel source) {return new PlayerDTO(source);}

        @Override
        public PlayerDTO[] newArray(final int size) {return new PlayerDTO[size];}
    }
}