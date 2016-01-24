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
 * Created on 10-01-2016, 15:30.
 * Project : skafottet
 * To facilitate future updates where the help file can be upgraded!
 *
 * @author rudz
 */
@SuppressWarnings("StringBufferField")
public class HelpFileDTO implements Parcelable {

    private String helpData;

    // the firebase version will be downloaded and installed in prefs if it's version is higher !
    private int version;

    public HelpFileDTO(final String helpData) {
        this.helpData = helpData;
    }

    public String getStringData() { return helpData; }

    public void setStringData(final String newData) { helpData = newData; }

    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(helpData);
        dest.writeInt(version);
    }

    protected HelpFileDTO(final Parcel in) {
        helpData = in.readString();
        version = in.readInt();
    }

    public static final Creator<HelpFileDTO> CREATOR = new HelpDTOCreator();

    private static class HelpDTOCreator implements Creator<HelpFileDTO> {
        @Override
        public HelpFileDTO createFromParcel(final Parcel source) { return new HelpFileDTO(source); }

        @Override
        public HelpFileDTO[] newArray(final int size) { return new HelpFileDTO[size]; }
    }
}
