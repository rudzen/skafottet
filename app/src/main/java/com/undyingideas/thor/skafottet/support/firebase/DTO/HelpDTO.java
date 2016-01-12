package com.undyingideas.thor.skafottet.support.firebase.DTO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 10-01-2016, 15:30.
 * Project : skafottet
 *
 * @author rudz
 */
@SuppressWarnings("StringBufferField")
public class HelpDTO implements Parcelable {

    private StringBuilder helpData;

    public HelpDTO() {
        this(500);
    }

    public HelpDTO(final int size) {
        helpData = new StringBuilder(size);
    }

    public HelpDTO(final String helpData) {
        this.helpData = new StringBuilder(helpData.length());
        this.helpData.append(helpData);
    }

    public void addStringData(final StringBuilder data) {
        helpData.append(data);
    }

    public void addStringData(final char data) {
        helpData.append(data);
    }

    public void addStringData(final String data) {
        helpData.append(data);
    }

    public void addStringData(final CharSequence data) {
        helpData.append(data);
    }

    public void addStringData(final char[] data) {
        helpData.append(data);
    }

    public void addStringData(final HelpDTO data) {
        helpData.append(data.getStringData());
    }

    public String getStringData() {
        return helpData.toString();
    }

    public void setStringData(final StringBuilder newData) {
        helpData = newData;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {dest.writeSerializable(helpData);}

    protected HelpDTO(final Parcel in) {helpData = (StringBuilder) in.readSerializable();}

    public static final Creator<HelpDTO> CREATOR = new HelpDTOCreator();

    private static class HelpDTOCreator implements Creator<HelpDTO> {
        @Override
        public HelpDTO createFromParcel(final Parcel source) {return new HelpDTO(source);}

        @Override
        public HelpDTO[] newArray(final int size) {return new HelpDTO[size];}
    }
}
