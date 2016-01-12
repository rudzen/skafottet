package com.undyingideas.thor.skafottet.support.firebase.WordList;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 * @author Emil
 */
public class WordListController {

    private final Firebase firebase;
    public HashMap<String, WordItem> wordList = new HashMap<>();

    public WordListController(final Firebase firebase){
        this.firebase=firebase;
        this.firebase.child("Wordlist").addChildEventListener(new WordGetter(this));
        Log.d("firebasewlc", firebase.toString());
    }

    public ArrayList<WordItem> getArray(){
        return new ArrayList<>(wordList.values());
    }

    public void addList(final WordItem wordItem) {

        final String title = wordItem.getTitle();
        final Firebase listRef = firebase.child("Wordlist").child(title);
        for(final String s : wordItem.getWords()) {
            listRef.child(s).setValue(s);
        }
    }
}

class WordGetter implements ChildEventListener {
    private final WordListController wlcRef;
    public WordGetter(final WordListController wlcRef) {
        this.wlcRef = wlcRef;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        Log.d("UpdateList", "firebase" + dataSnapshot.getKey());
        wlcRef.wordList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        wlcRef.wordList.remove(s);
        wlcRef.wordList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        wlcRef.wordList.remove(dataSnapshot.getKey());
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
        final WordItem dto = new WordItem();
        dto.setTitle(dataSnapshot.getKey());
        for(final DataSnapshot s : dataSnapshot.getChildren()) dto.addWord(s.getValue().toString());
        return dto;
    }
}


