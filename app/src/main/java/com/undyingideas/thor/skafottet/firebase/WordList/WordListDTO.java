package com.undyingideas.thor.skafottet.firebase.WordList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 08-01-2016.
 */
public class WordListDTO  {
    String title;
    ArrayList<String> wordList = new ArrayList<>();

    public WordListDTO() {
    }

    public WordListDTO(String title, ArrayList<String> wordList){
        this.title = title;
        this.wordList = wordList;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
