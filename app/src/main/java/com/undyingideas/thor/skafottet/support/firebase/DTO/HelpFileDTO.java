package com.undyingideas.thor.skafottet.support.firebase.DTO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 10-01-2016, 15:30.
 * Project : skafottet
 * To facilitate future updates where the help file can be upgraded!
 *
 * @author rudz
 */
@SuppressWarnings("StringBufferField")
public class HelpFileDTO implements Parcelable {

    private StringBuilder helpData;

    public HelpFileDTO() {
        this(500);
    }

    public HelpFileDTO(final int size) { helpData = new StringBuilder(size); }

    public HelpFileDTO(final String helpData) {
        this.helpData = new StringBuilder(helpData.length());
        this.helpData.append(helpData);
    }

    public void addStringData(final StringBuilder data) { helpData.append(data); }

    public void addStringData(final char data) { helpData.append(data); }

    public void addStringData(final String data) { helpData.append(data); }

    public void addStringData(final CharSequence data) { helpData.append(data); }

    public void addStringData(final char[] data) { helpData.append(data); }

    public void addStringData(final HelpFileDTO data) { helpData.append(data.getStringData()); }

    public String getStringData() { return helpData.toString(); }

    public void setStringData(final StringBuilder newData) { helpData = newData; }

    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) { dest.writeSerializable(helpData); }

    protected HelpFileDTO(final Parcel in) { helpData = (StringBuilder) in.readSerializable(); }

    public static final Creator<HelpFileDTO> CREATOR = new HelpDTOCreator();

    private static class HelpDTOCreator implements Creator<HelpFileDTO> {
        @Override
        public HelpFileDTO createFromParcel(final Parcel source) { return new HelpFileDTO(source); }

        @Override
        public HelpFileDTO[] newArray(final int size) { return new HelpFileDTO[size]; }
    }
}
