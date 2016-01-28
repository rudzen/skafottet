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
import android.support.annotation.NonNull;

import com.undyingideas.thor.skafottet.support.firebase.controller.WordListController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
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
    private Comparator<WordItem> wordItemComparator;

    public WordController() {reset(); }

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
//        wordItemComparator = new WordItemComparator();
        indexLocale = 0;
        isLocal = true;
    }

    public ArrayList<String> getLocalWordList(final int index) {
        final ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(localWords.get(index).getWords());
        return returnList;
    }

    @SuppressWarnings("MethodWithMultipleReturnPoints")
    public ArrayList<String> getCurrentList() {
//        Log.d("WordController", String.valueOf(isLocal));
        if (isLocal) {
            if (indexLocale > 0 && localWords.get(indexLocale).getWords().isEmpty()) {
                indexLocale = 0;
            }
            return localWords.get(indexLocale).getWords();
        } else {
            return WordListController.wordList.get(indexRemote).getWords();
        }
    }

    private void addLocalWordList(final String title, final String url, final ArrayList<String> theList) {
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
        final String returnString;
        if (isLocal || WordListController.wordList == null || WordListController.wordList.isEmpty()) {
            // using inlined method for empty list fix..
            returnString = getCurrentList().get((int) (Math.random() * localWords.get(indexLocale).getWordListSize()));
        } else {
            returnString = WordListController.wordList.get(indexRemote).getWords().get((int) (Math.random() * WordListController.wordList.get(indexRemote).getWordListSize()));
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
            if (localWords.get(i).getTitle().equalsIgnoreCase(wordItem.getTitle()) || localWords.get(i).getUrl().equalsIgnoreCase(wordItem.getUrl())) {
                // so we keep the index!
                localWords.get(i).setTitle(wordItem.getTitle());
                localWords.get(i).setUrl(wordItem.getUrl());
                localWords.get(i).setWords(wordItem.getWords());
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

//    /**
//     * Sorts the current local lists, the build-in default list is not sorted, and thus will always be the first.
//     */
//    public void sortLocalLists() {
//        Collections.sort(localWords, wordItemComparator);
//    }
//
//    /**
//     *  comparator for sorting the local list */
//    private static class WordItemComparator implements Comparator<WordItem> {
//        @Override
//        public int compare(final WordItem lhs, final WordItem rhs) {
//            /* avoid sorting the local list(s) */
//            if (!Objects.equals(lhs.getUrl(), "Lokal") && !Objects.equals(rhs.getUrl(), "Lokal")) {
//                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
//            }
//            return 0;
//        }
//    }

    /* getters and setters */

    public ArrayList<WordItem> getLocalWords() { return localWords; }

    public void setLocalWords(final ArrayList<WordItem> localWords) { this.localWords = localWords; }

    public boolean isLocal() { return isLocal; }

    public void setIsLocal(final boolean isLocal) { this.isLocal = isLocal; }

    public int getIndexLocale() { return indexLocale; }

    public void setIndexLocale(final int indexLocale) { this.indexLocale = indexLocale; }

    public String getIndexRemote() { return indexRemote; }

    public void setIndexRemote(final String indexRemote) { this.indexRemote = indexRemote; }

/* overrides */

    @Override
    public String toString() {
        return "WordController{" +
                "localWords=" + localWords +
                ", isLocal=" + isLocal +
                ", indexLocale=" + indexLocale +
                ", indexRemote='" + indexRemote + '\'' +
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
    }

    private WordController(final Parcel in) {
        localWords = in.createTypedArrayList(WordItem.CREATOR);
        isLocal = in.readByte() != 0;
        indexLocale = in.readInt();
        indexRemote = in.readString();
    }

    public static final Creator<WordController> CREATOR = new WordControllerCreator();

    private static class WordControllerCreator implements Creator<WordController> {
        @Override
        public WordController createFromParcel(final Parcel source) {return new WordController(source);}

        @Override
        public WordController[] newArray(final int size) {return new WordController[size];}
    }
}
