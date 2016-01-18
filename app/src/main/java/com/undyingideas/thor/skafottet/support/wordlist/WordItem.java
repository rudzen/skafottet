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

package com.undyingideas.thor.skafottet.support.wordlist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rudz on 19-11-2015.
 *
 * @author rudz
 */
@SuppressWarnings("ClassWithTooManyConstructors")
public class WordItem implements Parcelable, Serializable {

    private static final long serialVersionUID = 4;

    private String title;
    private String url;

    /* used when user is creating list */
    private boolean dlNow;

    /* arraylist because android likes that */
    private ArrayList<String> words = new ArrayList<>();

    /* for the serialization */
    public WordItem() { }

    public WordItem(final String title, final String url, final boolean dlNow) {
        this();
        this.title = title;
        this.url = url;
        this.dlNow = dlNow;
    }

    public WordItem(final String title, final String url, final ArrayList<String> list) {
        this(title, url, false);
        if (list != null) {
            words.addAll(list);
        }
    }

    public WordItem(final String title, final String url, final String[] list) {
        this(title, url, false);
        Collections.addAll(words, list);
    }

    public WordItem(final String title, final String url) {
        this(title, url, false);
    }

    public WordItem(final String title, final String url, final int startSize) {
        this(title, url);
        words.ensureCapacity(startSize);
    }

    /* -------------- Helper Methods  -------------- */

    public void sortList() {
        Collections.sort(words);
    }

    public boolean addWord(final String word) {
        return !words.contains(word) && words.add(word);
    }

    public boolean removeWord(final String word) {
        return words.remove(word);
    }

    public boolean hasWord(final String word) {
        return words.contains(word);
    }

    public String getWord(final int index) {
        return words.get(index);
    }

    public boolean exists(final String title, final String url) {
        /* yes, it's possible to have different cased entries :) */
        return this.title.equals(title) && this.url.equals(url);
    }

    public int getWordListSize() {
        return words.size();
    }

    public void replaceWordList(final ArrayList<String> newWords) {
        words.clear();
        words.addAll(newWords);
    }

    /* -------------- Getters & Setters -------------- */

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(final ArrayList<String> words) {
        this.words = words;
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
