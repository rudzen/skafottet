/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.wordlist;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * WorldList container class.
 * Created by rudz on 09-11-2015.
 */
public class WordList implements Serializable {

    // TODO : Clean up, too many methods

    private static final long serialVersionUID = 14324;
    private static final Pattern httpKill = Pattern.compile("http://");

    /* the word item list */
    private LinkedList<WordItem> words = new LinkedList<>();

    /* the default list words */
    private static final String DEFAULT_KEY = "Standard";

    /* the current active list */
    private int currentList;



    /**
     * Instantiates a new Word list.
     */
    public WordList() {

        final ArrayList<String> defaults = new ArrayList<>();
        defaults.add("bil");
        defaults.add("computer");
        defaults.add("programmering");
        defaults.add("motorvej");
        defaults.add("gangsti");
        defaults.add("skovsnegl");
        defaults.add("solsort");

        words.addFirst(new WordItem(DEFAULT_KEY, DEFAULT_KEY, defaults));

        currentList = 0;

        /*************************/
        /* DEMONSTRATION MODE -> */
        /*************************/

        words.addLast(new WordItem("DR.dk", "http://dr.dk"));
        words.addLast(new WordItem("Politiken.dk", "http://politiken.dk"));
        words.addLast(new WordItem("Meyers Indledning", "http://meyersfremmedordbog.dk/om-ordbogen-indledning"));

        /*************************/
        /* <- DEMONSTRATION MODE */
        /*************************/

    }

    /**
     * Gets word list count.
     *
     * @return the word list count
     */
    public int getWordListCount() {
        return words.size();
    }

    /**
     * Gets current list.
     *
     * @return the current list
     */
    public ArrayList<String> getCurrentActiveList() {
        return words.get(currentList).getWords();
    }

    public int getCurrentList() {
        return currentList;
    }

    /**
     * Gets list by index.
     *
     * @param index
     *         the index
     * @return the list by index
     */
    public ArrayList<String> getListByIndex(final int index) {
        return words.get(index).getWords();
    }

    /**
     * Gets list by title.
     *
     * @param title
     *         the title
     * @return the list by title
     */
    public ArrayList<String> getListByTitle(final String title) {
        for (final WordItem wi : words) if (wi.getTitle().equals(title)) return wi.getWords();
        return null;
    }

    /**
     * Gets title by index.
     *
     * @param index
     *         the index
     * @return the title by index
     */
    public String getTitleByIndex(final int index) {
        return words.get(index).getTitle();
    }


    public int getIndexByTitle(final String title) {
        int i = 0;
        for (final WordItem wi : words) {
            if (wi.getTitle().equals(title)) break;
            i++;
        }
        return i;
    }

    /**
     * Gets current word list title.
     *
     * @return the current word list title
     */
    public String getCurrentWordListTitle() {
        return words.get(currentList).getTitle();
    }

    /**
     * Gets word list by title.
     *
     * @param title
     *         the title
     * @return the word list by title
     */
    public ArrayList<String> getWordListByTitle(final String title) {
        for (final WordItem wi : words) if (wi.getTitle().equals(title)) return wi.getWords();
        return null;
    }

    /**
     * Gets word list by url.
     *
     * @param url
     *         the url
     * @return the word list by url
     */
    public ArrayList<String> getWordListByURL(final String url) {
        for (final WordItem wi : words) if (wi.getUrl().equals(url)) return wi.getWords();
        return null;
    }

    public WordItem getWordItemByURL(final String url) {
        for (final WordItem wi : words) {
            Log.d("getWordItemByURL", "Leder efter : " + url);
            Log.d("getWordItemByURL", "Fandt       : " + wi.getUrl());
            if (wi.getUrl().equals(url)) return wi;
        }
        return null;
    }


    /**
     * Sets current list.
     *
     * @param currentList
     *         the list index
     */
    public void setCurrentList(final int currentList) {
        this.currentList = currentList;
    }


    /**
     * Sets current list by url.
     *
     * @param url
     *         the url
     * @return List number which was set to if suceeded, otherwise -1
     */
    public int setCurrentListByURL(final String url) {
        int i = 0;
        for (final WordItem wi : words) {
            Log.d("WordList", "Søger efter : " + url);
            Log.d("WordList", "Fandt       : " + wi.getUrl());
            if (wi.getUrl().equals(url)) {
                currentList = i;
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Sets default words.
     */
    public void setDefaultWords() {
        currentList = 0;
    }

    /**
     * Add word list boolean.
     *
     * @param url
     *         the url
     * @return the boolean
     */
    public boolean addWordList(final String title, final String url) {
        String theTitle = title;
        Log.d("addWordList", "Forsøger at tilføje");
        Log.d("addWordList", "title : " + theTitle);
        Log.d("addWordList", "url   : " + url);

        if (url != null && url.isEmpty()) return false;
        if (theTitle != null && theTitle.isEmpty()) theTitle = httpKill.matcher(url).replaceAll("");

        words.addLast(new WordItem(theTitle, url));
        return true;

//        boolean alreadyExists = false;
//        for (WordItem wi : words) {
//            if (wi.exists(title, url)) {
//                alreadyExists = true;
//                break;
//            }
//        }
//        if (!alreadyExists) {
//            if (list == null || list.isEmpty()) words.addLast(new WordItem(title, url));
//            else words.addLast(new WordItem(title, url, list));
//            return true;
//        }
//        return false;
    }

    public void addWordToList(final String word, final String listUrl) {
        for (final WordItem wi : words) {
            if (wi.getUrl().equals(listUrl)) {
                wi.addWord(word);
                break;
            }
        }
    }


    public void appendWordList(final WordItem wordItem) {
        if (words.contains(wordItem)) {
            for (final WordItem wi : words)
                if (wordItem.equals(wi)) for (final String s : wordItem.getWords()) wi.addWord(s);
        } else {
            words.addLast(wordItem);
        }
    }

    public boolean addWordListDirect(final WordItem wordItem) {
        words.addLast(new WordItem(wordItem.getTitle(), wordItem.getUrl()));
        return true;
    }

    /**
     * Append word list boolean.
     *
     * @param title
     *         the key
     * @param list
     *         the list
     * @return 0 if appended, otherwise -1
     */
    public int appendWordListByTitle(final String title, final ArrayList<String> list) {
        if (!title.equals(DEFAULT_KEY)) {
            for (final WordItem wi : words) {
                if (wi.getTitle().equals(title)) {
                    for (final String s : list) wi.addWord(s);
                    return 0;
                }
            }
        }
        return -1;
    }


    /**
     * Delete list by title boolean.
     *
     * @param title
     *         the title
     * @return 0 if deleted, otherwise -1
     */
    public int deleteListByTitle(final String title) {
        for (final WordItem wi : words) {
            if (wi.getTitle().equals(title)) {
                words.remove(wi);
                return 0;
            }
        }
        return -1;
    }

    /**
     * Delete list by index boolean.
     *
     * @param index
     *         the index
     * @return the index removed, otherwise -1
     */
    public int deleteListByIndex(final int index) {
        int i = 0;
        for (final WordItem wi : words) {
            if (i++ == index) {
                words.remove(wi);
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * Gets key list.
     *
     * @return the key list
     */
    public ArrayList<String> getWordTitleList() {
        final ArrayList<String> ret = new ArrayList<>();
        for (final WordItem wi : words) ret.add(wi.getTitle());
        ret.trimToSize();
        return ret;
    }

    public String[] getUrls() {
        final String[] ret = new String[words.size() - 1];
        int i = 0;
        for (final WordItem wi : words) if (!wi.getUrl().equals(DEFAULT_KEY)) ret[i++] = wi.getUrl();
        return ret;
    }

    public void setWords(final LinkedList<WordItem> words) {
        this.words = words;
    }

    public LinkedList<WordItem> getWords() {
        return words;
    }



}
