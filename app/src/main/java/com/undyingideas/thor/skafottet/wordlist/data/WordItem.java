/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.wordlist.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rudz on 19-11-2015.
 *
 * @author rudz
 */
public class WordItem implements Serializable {

    private static final long serialVersionUID = 5789789;

    private String title;
    private String url;

    /* not used yet */
    //private Bitmap icon;

    /* used when user is creating list */
    private boolean dlNow;

    /* arraylist because android likes that */
    private final ArrayList<String> words = new ArrayList<>();

    public WordItem(final String title, final String url, final boolean dlNow) {
        this.title = title;
        this.url = url;
        this.dlNow = dlNow;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
        final WordItem other = (WordItem) o;
        return title.equals(other.title) && url.equals(other.url);
    }

    public WordItem(final String title, final String url, final ArrayList<String> list) {
        this(title, url, false);
        if (list != null) setWords(list);
    }

    public WordItem(final String title, final String url) {
        this(title, url, false);
    }

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

    public boolean hasWord(final String word) {
        return words.contains(word);
    }

    public String getWord(int index) {
        if (words.size() < index) return "";
        return words.get(index);
    }

    public boolean exists(final String title, final String url) {
        /* yes, it's possible to have different cased entries :) */
        return (this.title.equals(title) && this.url.equals(url));
    }

    /* -------------- Getters & Setters -------------- */

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        /* since we are using a final list, just add the new words manually */
        this.words.clear();
        this.words.addAll(words);
        this.words.trimToSize();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDlNow() {
        return dlNow;
    }

    public void setDlNow(boolean dlNow) {
        this.dlNow = dlNow;
    }
}
