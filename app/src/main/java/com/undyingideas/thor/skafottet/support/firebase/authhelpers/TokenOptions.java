package com.undyingideas.thor.skafottet.support.firebase.authhelpers;

/**
 * Created on 17-01-2016, 22:53.
 * Project : skafottet
 * From : https://github.com/firebase/firebase-token-generator-java/blob/master/src/main/java/com/firebase/security/token/TokenOptions.java <br>
 * - Added Parcel code
 *
 * @author rudz
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Token options.
 *
 * @author vikrum
 */
public class TokenOptions implements Parcelable {

    private Date expires;
    private Date notBefore;
    private boolean admin;
    private boolean debug;

    /**
     * Copies the date, since Date objects are mutable.
     */
    private static Date copyDate(final Date date) {
        return date != null ? new Date(date.getTime()) : null;
    }

    /**
     * Default constructor.
     */
    public TokenOptions() {
        expires = null;
        notBefore = null;
        admin = false;
        debug = false;
    }

    /**
     * Parametrized constructor.
     *
     * @param expires   The date/time at which the token should no longer be considered valid. (default is never).
     * @param notBefore The date/time before which the token should not be considered valid. (default is now).
     * @param admin     Set to true to bypass all security rules (you can use this for trusted server code).
     * @param debug     Set to true to enable debug mode (so you can see the results of Rules API operations).
     */
    public TokenOptions(final Date expires, final Date notBefore, final boolean admin, final boolean debug) {
        super();
        this.expires = copyDate(expires);
        this.notBefore = copyDate(notBefore);
        this.admin = admin;
        this.debug = debug;
    }

    /**
     * @return the expires
     */
    public Date getExpires() { return expires; }

    /**
     * @param expires the expires to set
     */
    public void setExpires(final Date expires) { this.expires = copyDate(expires); }

    /**
     * @return the notBefore
     */
    public Date getNotBefore() { return notBefore; }

    /**
     * @param notBefore the notBefore to set
     */
    public void setNotBefore(final Date notBefore) { this.notBefore = copyDate(notBefore); }

    /**
     * @return the admin
     */
    public boolean isAdmin() { return admin; }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(final boolean admin) { this.admin = admin; }

    /**
     * @return the debug
     */
    public boolean isDebug() { return debug; }

    /**
     * @param debug the debug to set
     */
    public void setDebug(final boolean debug) { this.debug = debug; }


    /* parcel code, by rudz */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(expires != null ? expires.getTime() : -1);
        dest.writeLong(notBefore != null ? notBefore.getTime() : -1);
        dest.writeByte(admin ? (byte) 1 : (byte) 0);
        dest.writeByte(debug ? (byte) 1 : (byte) 0);
    }

    private TokenOptions(final Parcel in) {
        final long tmpExpires = in.readLong();
        expires = tmpExpires == -1 ? null : new Date(tmpExpires);
        final long tmpNotBefore = in.readLong();
        notBefore = tmpNotBefore == -1 ? null : new Date(tmpNotBefore);
        admin = in.readByte() != 0;
        debug = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TokenOptions> CREATOR = new TokenOptionsCreator();

    private static class TokenOptionsCreator implements Creator<TokenOptions> {
        @Override
        public TokenOptions createFromParcel(final Parcel source) {return new TokenOptions(source);}

        @Override
        public TokenOptions[] newArray(final int size) {return new TokenOptions[size];}
    }
}