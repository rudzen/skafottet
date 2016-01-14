package com.undyingideas.thor.skafottet.support.wordlist;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static final long serialVersionUID = 321;
    private ArrayList<WordItem> localWords;
    private boolean isLocal;
    private int indexLocale;
    private String indexRemote;
    private Random random;
    private Comparator<WordItem> wordItemComparator;

    public WordController() { }

    public WordController(final ArrayList<String> defaultList) {
        reset();
        localWords.add(new WordItem("Lande", "Lokal", defaultList));
    }

    public WordController(final String[] defaultList) {
        reset();
        localWords.add(new WordItem("Lande", "Lokal", defaultList));
    }

    private void reset() {
        localWords = new ArrayList<>();
        wordItemComparator = new WordItemComparator();
        indexLocale = 0;
        isLocal = true;
    }

    public ArrayList<String> getLocalWordList(final int index) {
        final ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(localWords.get(index).getWords());
        return returnList;
    }

    public ArrayList<String> getCurrentList() {
        Log.d("WordController", String.valueOf(isLocal));
        return isLocal ? localWords.get(indexLocale).getWords() : WordListController.wordList.get(indexRemote).getWords();
    }

    public void addLocalWordList(final String title, final String url, final ArrayList<String> theList) {
        localWords.add(new WordItem(title, url, theList));
        indexLocale = localWords.size() - 1;
    }

    public void addLocalWordList(final String title, final String url, final Set<String> theList) {
        final ArrayList<String> daList = new ArrayList<>();
        daList.addAll(theList);
        addLocalWordList(title, url, daList);
    }

    public void addLocalWordList(final String title, final String url) {
        addLocalWordList(title, url, new ArrayList<String>());
    }

    // hacked together...
    public String getRandomWord() {
        random = new Random(System.currentTimeMillis());
        final String returnString;
        if (isLocal || WordListController.wordList == null || WordListController.wordList.isEmpty()) {
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

    public void replaceLocalWordList(final WordItem wordItem) {
        // quick hack
        boolean replaced = false;
        for (int i = 0; i < localWords.size(); i++) {
            if (Objects.equals(wordItem.getTitle().toLowerCase(), localWords.get(i).getTitle().toLowerCase()) && Objects.equals(wordItem.getUrl().toLowerCase(), localWords.get(i).getUrl().toLowerCase())) {
                localWords.get(i).replaceWordList(wordItem.getWords());
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            addLocalWordList(wordItem.getTitle(), wordItem.getUrl(), wordItem.getWords());
        }
    }

    public int getListCount() {
        return localWords.size() + WordListController.wordList.size();
    }

    /**
     * Will remove the current LOCAL list and reset the currentList to one lower.
     * The first (default) list CAN NOT be removed.
     */
    public boolean removeCurrentList() {
        if (indexLocale > 0 && localWords.size() > 1) {
            localWords.remove(indexLocale--);
            localWords.trimToSize();
            return true;
        }
        return false;
    }

    /**
     * Sorts the current local lists, the build-in default list is not sorted, and thus will always be the first.
     */
    public void sortLocalLists() {
        Collections.sort(localWords, wordItemComparator);
    }

    /**
     *  comparator for sorting the local list */
    private static class WordItemComparator implements Comparator<WordItem> {
        @Override
        public int compare(final WordItem lhs, final WordItem rhs) {
            /* avoid sorting the local list(s) */
            if (!Objects.equals(lhs.getUrl(), "Lokal") && !Objects.equals(rhs.getUrl(), "Lokal")) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
            return 0;
        }
    }

    /* getters and setters */

    public ArrayList<WordItem> getLocalWords() { return localWords; }

    public void setLocalWords(final ArrayList<WordItem> localWords) { this.localWords = localWords; }

    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(final boolean isLocal) {
        this.isLocal = isLocal;
    }

    public int getIndexLocale() {
        return indexLocale;
    }

    public void setIndexLocale(final int indexLocale) {
        if (indexLocale > -1) {
            this.indexLocale = indexLocale;
        }
    }

    public String getIndexRemote() {
        return indexRemote;
    }

    public void setIndexRemote(final String indexRemote) {
        this.indexRemote = indexRemote;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(final Random random) {
        this.random = random;
    }

/* overrides */

    @Override
    public String toString() {
        return "WordController{" +
                "localWords=" + localWords +
                ", isLocal=" + isLocal +
                ", indexLocale=" + indexLocale +
                ", indexRemote='" + indexRemote + '\'' +
                ", random=" + random +
                '}';
    }


    /* parcel code .. DON'T TOUCH! */


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedList(localWords);
        dest.writeByte(isLocal ? (byte) 1 : (byte) 0);
        dest.writeInt(indexLocale);
        dest.writeString(indexRemote);
        dest.writeSerializable(random);
    }

    protected WordController(final Parcel in) {
        localWords = in.createTypedArrayList(WordItem.CREATOR);
        isLocal = in.readByte() != 0;
        indexLocale = in.readInt();
        indexRemote = in.readString();
        random = (Random) in.readSerializable();
    }

    public static final Creator<WordController> CREATOR = new WordControllerCreator();

    private static class WordControllerCreator implements Creator<WordController> {
        @Override
        public WordController createFromParcel(final Parcel source) {return new WordController(source);}

        @Override
        public WordController[] newArray(final int size) {return new WordController[size];}
    }
}
