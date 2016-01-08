package com.undyingideas.thor.skafottet.firebase.WordList;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Emil on 08-01-2016.
 */
public class WordListController {
    private final Firebase firebase;
    public HashMap<String, WordListDTO> wordList = new HashMap<>();




    public WordListController(final Firebase firebase){
        this.firebase=firebase;
        this.firebase.child("WordList").addChildEventListener(new WordGetter(this));

    }
    public void addList(final WordListDTO wordListDTO) {

        final ArrayList<String> words = wordListDTO.getWordList();
        final String title = wordListDTO.getTitle();
        Log.d("emil", "addlist started " + words.size());
        for(final String s : words){
            Log.d("emil", s);
            final Firebase wordRef = firebase.child("Wordlist").child(title);
            final FireBaseCreate h = new FireBaseCreate(title , words);
            wordRef.push().setValue(s);
        }
    }
}

class FireBaseCreate implements Transaction.Handler{
    boolean succes;
    final ArrayList<String> wordList;
    final String title;
    FireBaseCreate(final String title, final ArrayList<String> wordList){
        this.wordList = wordList;
        this.title = title;
    }

    @Override
    public Transaction.Result doTransaction(final MutableData mutableData) {
        if (mutableData.getValue() == null) {
            mutableData.setValue(new WordListDTO(title,wordList));
            return Transaction.success(mutableData);
        } else {
            return Transaction.abort();
        }
    }

    @Override
    public void onComplete(final FirebaseError firebaseError, final boolean b, final DataSnapshot dataSnapshot) {
        succes = b;
        Log.d("firebase", "succes = " + b);
    }
}

class WordGetter implements ChildEventListener {
    final WordListController wlcRef;
    public WordGetter(final WordListController wlcRef) {
        this.wlcRef = wlcRef;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        wlcRef.wordList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
        Log.d("emil", dataSnapshot.toString());
        }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        Log.d("firebaseError", "childchangede"+dataSnapshot.toString());
        }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {

        }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        // void
    }

    @Override
    public void onCancelled(final FirebaseError firebaseError) {
        // void
    }

    protected static WordListDTO getDTO(final DataSnapshot dataSnapshot) {
        final WordListDTO dto = new WordListDTO();
        for(final DataSnapshot s : dataSnapshot.getChildren()) dto.getWordList().add(s.getValue().toString());

        //dto.setScore(Integer.valueOf(dataSnapshot.child("score").getValue().toString()));
        //if (dataSnapshot.hasChild("gameList"))
          //  for(DataSnapshot ds : dataSnapshot.child("gameList").getChildren())
            //    dto.getGameList().add(ds.getValue().toString());
        //Log.d("firebase dto", dto.getName() + " " + dto.getScore());
        return dto;
    }
}


