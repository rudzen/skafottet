package com.undyingideas.thor.skafottet.firebase.WordList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 08-01-2016.
 */
public class WordListDTO  {
    ArrayList<String> wordList = new ArrayList<>();

    public WordListDTO() {
    }

    public WordListDTO(ArrayList<String> wordList){
        this.wordList = wordList;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }


}
