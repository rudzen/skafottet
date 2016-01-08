package com.undyingideas.thor.skafottet.firebase.WordList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 08-01-2016.
 */
public class WordListDTO {
    WordListDTO(List<String> wordList){

    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    List<String> wordList = new ArrayList<>();
}
