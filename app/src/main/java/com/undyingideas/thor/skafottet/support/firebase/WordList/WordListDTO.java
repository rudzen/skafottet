package com.undyingideas.thor.skafottet.support.firebase.WordList;

import java.util.ArrayList;

/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 * @author Emil
 */
@Deprecated
public class WordListDTO  {

    @Deprecated
    private String title;

    @Deprecated
    private ArrayList<String> wordList = new ArrayList<>();

    @Deprecated
    public WordListDTO() {
    }

    @Deprecated
    public WordListDTO(final String title, final ArrayList<String> wordList){
        this.title = title;
        this.wordList = wordList;
    }

    @Deprecated
    public ArrayList<String> getWordList() {
        return wordList;
    }

    @Deprecated
    public void setWordList(final ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    @Deprecated
    public String getTitle() {
        return title;
    }

    @Deprecated
    public void setTitle(final String title) {
        this.title = title;
    }

}
