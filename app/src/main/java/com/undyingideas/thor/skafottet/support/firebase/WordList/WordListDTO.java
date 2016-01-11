package com.undyingideas.thor.skafottet.support.firebase.WordList;

import java.util.ArrayList;

/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 * @author Emil
 */
public class WordListDTO  {
    private String title;
    private ArrayList<String> wordList = new ArrayList<>();

    public WordListDTO() {
    }

    public WordListDTO(final String title, final ArrayList<String> wordList){
        this.title = title;
        this.wordList = wordList;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(final ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
