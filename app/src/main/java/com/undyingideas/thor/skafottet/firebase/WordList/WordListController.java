package com.undyingideas.thor.skafottet.firebase.WordList;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.undyingideas.thor.skafottet.game_ui.wordlist.data.WordList;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 * @author Emil
 */
public class WordListController {

    private final Firebase firebase;
    public HashMap<String, WordListDTO> wordList = new HashMap<>();

    public WordListController(final Firebase firebase){
        this.firebase=firebase;
        this.firebase.child("Wordlist").addChildEventListener(new WordGetter(this));
        Log.d("firebasewlc", firebase.toString());
    }

    public ArrayList<WordListDTO> getArray(){
        return new ArrayList<WordListDTO>(wordList.values());
    }

    public void addList(final WordListDTO wordListDTO) {

        final String title = wordListDTO.getTitle();
        final Firebase listRef = firebase.child("Wordlist").child(title);
        for(final String s : wordListDTO.getWordList()) {
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

    private static WordListDTO getDTO(final DataSnapshot dataSnapshot) {
        final WordListDTO dto = new WordListDTO();
        for(final DataSnapshot s : dataSnapshot.getChildren()) dto.getWordList().add(s.getValue().toString());
        return dto;
    }
}


