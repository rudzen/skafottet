package com.undyingideas.thor.skafottet.support.wordlist;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Created on 13-01-2016, 11:51.
 * Project : skafottet
 *
 * @author rudz
 */
public final class WordController implements Serializable, Parcelable {

    private ArrayList<WordItem> localWords;

    private int currentLocalList;
    private boolean isLocal;
    private int indexLocale;
    private String indexRemote;
    private Random random;


    public WordController() {
        localWords = new ArrayList<>();
        currentLocalList = 0;
    }

    public WordController(final ArrayList<String> defaultList) {
        this();
        localWords.add(new WordItem("Lande", "Lokal", defaultList));
    }

    public WordController(final String[] defaultList) {
        this();
        localWords.add(new WordItem("Lande", "Lokal", defaultList));
    }

    public ArrayList<String> getLocalWordList(final int index) {
        final ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(localWords.get(index).getWords());
        return returnList;
    }

    public void addLocalWordList(final String title, final String url, final ArrayList<String> theList) {
        localWords.add(new WordItem(title, url, theList));
    }

    public void addLocalWordList(final String title, final String url, final Set<String> theList) {
        final ArrayList<String> daList = new ArrayList<>();
        daList.addAll(theList);
        addLocalWordList(title, url, daList);
    }

    // hacked together...
    public String getRandomWord() {
        random = new Random(System.currentTimeMillis());
        final String returnString;
        if (isLocal) {
            returnString = localWords.get(indexLocale).getWords().get(random.nextInt(localWords.get(indexLocale).getWordListSize()));
        } else {
            returnString = WordListController.wordList.get(indexRemote).getWords().get(random.nextInt(WordListController.wordList.get(indexRemote).getWordListSize()));
        }
        return returnString;
    }

    public boolean existsLocal(@NonNull final WordItem newWordItem) {
        for (final WordItem wordItem : localWords) {
            if (Objects.equals(wordItem.getTitle(), newWordItem.getTitle())) return false;
        }
        return true;
    }

    /* getters and setters */

    public boolean isLocal() { return isLocal; }

    public void setIsLocal(final boolean isLocal) { this.isLocal = isLocal; }

    public int getIndexLocale() { return indexLocale; }

    public void setIndexLocale(final int indexLocale) { this.indexLocale = indexLocale; }

    public String getIndexRemote() { return indexRemote; }

    public void setIndexRemote(final String indexRemote) { this.indexRemote = indexRemote; }

    public int getCurrentLocalList() { return currentLocalList; }

    public void setCurrentLocalList(final int currentLocalList) { this.currentLocalList = currentLocalList; }

    public ArrayList<WordItem> getLocalWords() { return localWords; }

    public void setLocalWords(final ArrayList<WordItem> localWords) { this.localWords = localWords; }

    /* overrides */

    @Override
    public String toString() {
        return "WordController{" +
                "localWords=" + localWords +
                ", currentLocalList=" + currentLocalList +
                ", isLocal=" + isLocal +
                ", indexLocale=" + indexLocale +
                ", indexRemote='" + indexRemote + '\'' +
                ", random=" + random +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /* parcel code .. DON'T TOUCH! */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedList(localWords);
        dest.writeInt(currentLocalList);
        dest.writeByte(isLocal ? (byte) 1 : (byte) 0);
        dest.writeInt(indexLocale);
        dest.writeString(indexRemote);
        dest.writeSerializable(random);
    }

    protected WordController(final Parcel in) {
        localWords = in.createTypedArrayList(WordItem.CREATOR);
        currentLocalList = in.readInt();
        isLocal = in.readByte() != 0;
        indexLocale = in.readInt();
        indexRemote = in.readString();
        random = (Random) in.readSerializable();
    }

    public static final Parcelable.Creator<WordController> CREATOR = new WordControllerCreator();

    private static class WordControllerCreator implements Creator<WordController> {
        @Override
        public WordController createFromParcel(final Parcel source) {return new WordController(source);}

        @Override
        public WordController[] newArray(final int size) {return new WordController[size];}
    }
}
