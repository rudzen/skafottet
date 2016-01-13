package com.undyingideas.thor.skafottet.support.wordlist;

import android.support.annotation.NonNull;

import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;

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
public final class Words {

    private ArrayList<WordItem> localWords;

    private int currentLocalList;
    private boolean isLocal;
    private int indexLocale;
    private String indexRemote;
    private Random random;


    public Words() {
        localWords = new ArrayList<>();
        currentLocalList = 0;
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
        if (isLocal) {
            return localWords.get(indexLocale).getWords().get(random.nextInt(localWords.get(indexLocale).getWordListSize()));
        } else {
            return WordListController.wordList.get(indexRemote).getWords().get(random.nextInt(WordListController.wordList.get(indexRemote).getWordListSize()));
       }
    }




    public boolean existsLocal(@NonNull final WordItem newWordItem) {
        for (final WordItem wordItem : localWords) {
            if (Objects.equals(wordItem.getTitle(), newWordItem.getTitle())) return false;
        }
        return true;
    }


    /* Future update for other use of firebase word lists */

//    public String getFirebaseWord(final String key, final int index) {
//
//    }
//
//    public HashMap<String, WordItem> getFireBaseWords() {
//        return fireBaseWords;
//    }
//
//    public WordItem getFireBaseWordItem(final String key) {
//        return fireBaseWords.get(key);
//    }
//
//    public void setFireBaseList(final String key, final WordItem wordItem) {
//        fireBaseWords.put(key, wordItem);
//    }
//
//    public ArrayList<String> getFireBaseWordList(final String key) {
//        return fireBaseWords.get(key).getWords();
//    }
//
//    public void setFireBaseWordList(@NonNull final String key, @NonNull final ArrayList<String> list) {
//        fireBaseWords.get(key).getWords().clear();
//        fireBaseWords.get(key).getWords().addAll(list);
//    }
//
//    public boolean removeFirebaseWord(@NonNull final String key, @NonNull final String theWord) {
//        return fireBaseWords.get(key).removeWord(theWord);
//    }
//
//    public void updateFireBaseWordList(@NonNull final String key, @NonNull final ArrayList<String> listToUpdateWith) {
//        final WordItem wordItem = fireBaseWords.get(key);
//        for (final String theNewWord : listToUpdateWith) {
//            if (!wordItem.getWords().contains(theNewWord)) {
//                wordItem.addWord(theNewWord);
//            }
//        }
//        fireBaseWords.put(key, wordItem);
//    }



}
