/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.wordlist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by rudz on 19-11-2015.
 *
 * @author rudz
 */
public class WordItem implements Parcelable {

    private String title;
    private String url;

    /* used when user is creating list */
    private boolean dlNow;

    /* arraylist because android likes that */
    private ArrayList<String> words = new ArrayList<>();

    public WordItem(final String title, final String url, final boolean dlNow) {
        this.title = title;
        this.url = url;
        this.dlNow = dlNow;
    }

    public WordItem(final String title, final String url, final ArrayList<String> list) {
        this(title, url, false);
        if (list != null) setWords(list);
    }

    public WordItem(final String title, final String url) {
        this(title, url, false);
    }

    /* -------------- Helper Methods  -------------- */

    public void addWord(final String word) {
        if (!hasWord(word)) {
            words.add(word);
            words.trimToSize();
        }
    }

    public void removeWord(final String word) {
        words.remove(word);
        words.trimToSize();
    }

    private boolean hasWord(final String word) {
        return words.contains(word);
    }

    public String getWord(final int index) {
        if (words.size() < index) return "";
        return words.get(index);
    }

    public boolean exists(final String title, final String url) {
        /* yes, it's possible to have different cased entries :) */
        return this.title.equals(title) && this.url.equals(url);
    }

    /* -------------- Getters & Setters -------------- */

    public ArrayList<String> getWords() {
        return words;
    }

    private void setWords(final ArrayList<String> words) {
        this.words.clear();
        this.words.addAll(words);
        this.words.trimToSize();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public boolean isDlNow() {
        return dlNow;
    }

    public void setDlNow(final boolean dlNow) {
        this.dlNow = dlNow;
    }

    /* -------------- Overrides -------------- */

    @Override
    public String toString() {
        return "WordItem{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", dlNow=" + dlNow +
                ", words=" + words +
                '}';
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
        final WordItem other = (WordItem) o;
        return title.equals(other.title) && url.equals(other.url);
    }

    /* -------------- Parcel methods & classes -------------- */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeByte(dlNow ? (byte) 1 : (byte) 0);
        dest.writeStringList(words);
    }

    protected WordItem(final Parcel in) {
        title = in.readString();
        url = in.readString();
        dlNow = in.readByte() != 0;
        words = in.createStringArrayList();
    }

    public static final Creator<WordItem> CREATOR = new WordItemCreator();

    private static class WordItemCreator implements Creator<WordItem> {
        @Override
        public WordItem createFromParcel(final Parcel source) {return new WordItem(source);}

        @Override
        public WordItem[] newArray(final int size) {return new WordItem[size];}
    }
}
