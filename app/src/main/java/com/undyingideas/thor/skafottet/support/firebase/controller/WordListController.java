/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
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

package com.undyingideas.thor.skafottet.support.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 */
public class WordListController {

    private static final String TAG = "WordListController";

    private final Firebase firebase;
    private static HashMap<String, WordItem> wordList = new HashMap<>();

    public WordListController(final Firebase firebase){
        this.firebase=firebase;
        this.firebase.child("Wordlist").addChildEventListener(new WordGetter(this));
        Log.d("firebasewlc", firebase.toString());
    }

    public static ArrayList<WordItem> getArray(){
        return new ArrayList<>(wordList.values());
    }

    public static HashMap<String, WordItem> getWordList() {
        return wordList;
    }

    public static void setWordList(final HashMap<String, WordItem> wordList) {
        WordListController.wordList = wordList;
    }

    public void addList(final WordItem wordItem) {
        final String title = wordItem.getTitle();
        final Firebase listRef = firebase.child("Wordlist").child(title);
        for(final String s : wordItem.getWords()) {
            listRef.child(s).setValue(s);
        }
    }

    private static void saveList() {
        GameUtility.getPrefs().putObject(Constant.KEY_WORDS_FIREBASE, wordList);
    }

    public static ArrayList<String> getKeyList() {
        final ArrayList<String> returnList = new ArrayList<>(wordList.size());
        returnList.addAll(wordList.keySet());
        Log.d(TAG, returnList.toString());
        Collections.sort(returnList);
        return returnList;
    }

    @SuppressWarnings("unchecked")
    public static void loadList() {
        wordList = (HashMap<String, WordItem>) GameUtility.getPrefs().getObject(Constant.KEY_WORDS_FIREBASE, HashMap.class);
    }

    static class WordGetter implements ChildEventListener {
        private final WordListController wlcRef;
        public WordGetter(final WordListController wlcRef) {
            this.wlcRef = wlcRef;
        }

        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
            Log.d("UpdateList", "firebase" + dataSnapshot.getKey());
            getWordList().put(dataSnapshot.getKey(), getDTO(dataSnapshot));
            saveList();
        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
            getWordList().remove(s);
            getWordList().put(dataSnapshot.getKey(), getDTO(dataSnapshot));
            saveList();
        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            getWordList().remove(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
            // void
        }

        @Override
        public void onCancelled(final FirebaseError firebaseError) {
            Log.e("firebaseerror", firebaseError.getDetails());
        }

        private static WordItem getDTO(final DataSnapshot dataSnapshot) {
            final WordItem dto = new WordItem(dataSnapshot.getKey(), null);
            for(final DataSnapshot snapshot : dataSnapshot.getChildren()) dto.addWord(snapshot.getValue().toString());
            return dto;
        }
    }
}




