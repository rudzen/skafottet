package com.undyingideas.thor.skafottet.firebase.WordList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 08-01-2016.
 */
public class WordListDTO extends ArrayList<String> {
    public WordListDTO(ArrayList<String> wordList){

    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    ArrayList<String> wordList = new ArrayList<>();
}
