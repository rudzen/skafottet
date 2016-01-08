package com.undyingideas.thor.skafottet.firebase.WordList;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.undyingideas.thor.skafottet.wordlist.data.WordList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Emil on 08-01-2016.
 */
public class WordListController {
    private final Firebase firebase;
    public HashMap<String, WordListDTO> wordList = new HashMap<>();
    WordListController wlcRef;



    WordListController(Firebase firebase){
        this.firebase=firebase;
        this.firebase.child("WordList").addChildEventListener(new WordGetter(wlcRef));

    }
    public void addList(WordListDTO wordListDTO) {
        List<String> words;
        words =wordListDTO.getWordList();
        for(int i=0; i > words.size();i++){
            Firebase wordRef = firebase.child("Wordlist").child(words.get(i));
            fireBaseCreate h = new fireBaseCreate(words);
            wordRef.runTransaction(h);
        }
    }
}

class fireBaseCreate implements Transaction.Handler{
    boolean succes;
    final List<String> wordList;
    fireBaseCreate(List<String> wordList){
        this.wordList = wordList;
    }

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        if (mutableData.getValue() == null) {
            mutableData.setValue(new WordListDTO(wordList));
            return Transaction.success(mutableData);
        } else {
            return Transaction.abort();
        }
    }

    @Override
    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
        succes = b;
        Log.d("firebase", "succes = " + b);
    }
}

class WordGetter implements ChildEventListener {
    final WordListController wlcRef;
    public WordGetter(WordListController wlcRef) {
        this.wlcRef = wlcRef;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        wlcRef.addList(getDTO(dataSnapshot));
        }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d("firebaseError", "childchangede"+dataSnapshot.toString());
        }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // void
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        // void
    }

    protected WordListDTO getDTO(DataSnapshot dataSnapshot) {
        WordListDTO dto = new WordListDTO((List<String>) dataSnapshot.getValue(WordListDTO.class));
        //dto.setScore(Integer.valueOf(dataSnapshot.child("score").getValue().toString()));
        //if (dataSnapshot.hasChild("gameList"))
          //  for(DataSnapshot ds : dataSnapshot.child("gameList").getChildren())
            //    dto.getGameList().add(ds.getValue().toString());
        //Log.d("firebase dto", dto.getName() + " " + dto.getScore());
        return dto;
    }
}
