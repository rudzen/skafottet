package com.undyingideas.thor.skafottet.support.firebase.controller;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.undyingideas.thor.skafottet.support.firebase.DTO.PlayerDTO;

import java.util.HashMap;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 * @author rudz
 *
 * - rudz, 16-01-2016
 * - Re-designed classes for less memory use.
 */
public class PlayerController {

    private final static String PLAYERS = "Players";
    private final static String GAME_LIST = "gameList";
    private final static String SCORE = "score";

    private final Firebase ref;
    private final MultiplayerController mpcRef;
    public HashMap<String, PlayerDTO> playerList = new HashMap<>();

    public PlayerController(final MultiplayerController mp, final Firebase ref) {
        this.ref = ref;
        mpcRef = mp;
        this.ref.child(PLAYERS).addChildEventListener(new NameGetter(mpcRef));
    }

    public void createPlayer(final String name, final String password) {
        final Firebase playersRef = ref.child(PLAYERS).child(name);
        final FireBaseCreate h = new FireBaseCreate(name, password);
        playersRef.runTransaction(h);
    }

    public void addListener(final String name) {
        ref.child(PLAYERS).child(name).child(GAME_LIST).addChildEventListener(new GameListListener(mpcRef));
    }

    public void updatePlayerScore(final String name, final int newScore) {
        ref.child(PLAYERS).child(name).child(SCORE).setValue(newScore);
    }


    static class FireBaseCreate implements Transaction.Handler {
        private boolean succes;
        private final String name;
        private final String password;

        FireBaseCreate(final String name, final String password) {
            this.password = password;
            this.name = name;
        }

        @Override
        public Transaction.Result doTransaction(final MutableData mutableData) {
            if (mutableData.getValue() == null) {
                mutableData.setValue(new PlayerDTO(name, password));
                return Transaction.success(mutableData);
            } else {
                return Transaction.abort();
            }
        }

        @Override
        public void onComplete(final FirebaseError firebaseError, final boolean b, final DataSnapshot dataSnapshot) {
            succes = b;
        }
    }

    static class NameGetter implements ChildEventListener {
        private final MultiplayerController mpcref;

        public NameGetter(final MultiplayerController mpcref) { this.mpcref = mpcref; }

        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
            mpcref.pc.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
            mpcref.update();
        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
            mpcref.pc.playerList.remove(dataSnapshot.getKey());
            mpcref.pc.playerList.put(dataSnapshot.getKey(), getDTO(dataSnapshot));
            mpcref.update();
        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            mpcref.pc.playerList.remove(dataSnapshot.getKey());
            mpcref.update();
        }

        @Override
        public void onChildMoved(final DataSnapshot dataSnapshot, final String s) { }

        @Override
        public void onCancelled(final FirebaseError firebaseError) { }

        private static PlayerDTO getDTO(final DataSnapshot dataSnapshot) {
            final PlayerDTO dto = new PlayerDTO(dataSnapshot.getKey());
            dto.setScore(Integer.valueOf(dataSnapshot.child(SCORE).getValue().toString()));
            dto.setPassword(dataSnapshot.child("pass").getValue().toString());
            if (dataSnapshot.hasChild(GAME_LIST))
                for (final DataSnapshot ds : dataSnapshot.child(GAME_LIST).getChildren())
                    dto.getGameList().add(ds.getValue().toString());
            return dto;
        }
    }

    static class GameListListener implements ChildEventListener {
        private final MultiplayerController mpc;

        GameListListener(final MultiplayerController mpc) {
            this.mpc = mpc;
        }

        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
            if (mpc.lc != null) {
                mpc.lc.addLobbyListener(dataSnapshot.getValue().toString());
            }
        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
            // shouldn't happen
            Log.d("firebaseError", "childchangede" + dataSnapshot.toString());
        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            Log.d("firebase childremoved", dataSnapshot.toString());
        }

        @Override
        public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
            // shouldn't happen
            Log.d("firebaseError", "childMoved" + dataSnapshot.toString());
        }

        @Override
        public void onCancelled(final FirebaseError firebaseError) {
            Log.d("firebaseError", "cancelled" + firebaseError.toString());
        }
    }
}