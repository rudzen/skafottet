package com.undyingideas.thor.skafottet.firebase.lobby;

import android.os.Handler;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.utility.SettingsDTO;

import java.util.ArrayList;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.firebase;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.settings;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.players<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:12
 * </p>
 * This class handles the data transfer and update of current players.<br>
 * It will download the current player list and monitor for changes, so the current player always has the most
 * up-to-date version of the list.
 * This is also required for logged in players to issue a challenge to another player.
 *
 * @author rudz
 */
public class LobbyListener {
    // TODO : Code the bastard

    private final Handler handler = new Handler();

    private final ArrayList<LobbyListenerSlave> slaves = new ArrayList<>();

    private final static String LOBBY = "Lobby";
    private final static String GAME_LIST = "gameList";
    private final static String SCORE = "score";
    private final static String EMAIL = "email";

    private final GameListListener gameListListener;
    private final NameGetter nameGetter;

    public LobbyListener() {
        gameListListener = new GameListListener();
        nameGetter = new NameGetter();
    }

    public void setListeners() {
        if (settings.auth_status > 0) {
            firebase.child(LOBBY).keepSynced(true);
            firebase.child(LOBBY).addChildEventListener(nameGetter);
            if (settings.auth_status == SettingsDTO.AUTH_USER) { // play nice, could be the user has logged in for real since last check.
                firebase.child(LOBBY).child(EMAIL).child(GAME_LIST).addChildEventListener(gameListListener);
            }
        }

    }

    public void removeListeners() {
        firebase.child(LOBBY).keepSynced(false);
        firebase.child(LOBBY).removeEventListener(nameGetter);
        firebase.child(LOBBY).child(EMAIL).child(GAME_LIST).removeEventListener(gameListListener);
    }

    public void addSlave(final LobbyListenerSlave playerListenerSlave) {
        slaves.add(playerListenerSlave);
    }

    public void removeSlave(final LobbyListenerSlave playerListenerSlave) {
        slaves.remove(playerListenerSlave);
    }

    public void clearSlaves() {
        slaves.clear();
    }

    public boolean hasSlave(final LobbyListenerSlave playerListenerSlave) {
        return slaves.contains(playerListenerSlave);
    }

    private void runSlaves() {
        for (final LobbyListenerSlave slave : slaves) {
            handler.post(slave);
        }
    }

    private class NameGetter implements ChildEventListener {

        private static final String TAG = "NameGetter";

        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
            Log.d(TAG, "onChildAdded : " + dataSnapshot.toString() + " and " + s);
            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                for (final LobbyListenerSlave slave : slaves) {
                    slave.addLobby(dataSnapshot.getKey(), dataSnapshot.getValue(LobbyDTO.class));
                    slave.setAborted(false);
                    handler.post(slave);
                }
            }
        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
            Log.d(TAG, "onChildChanged : " + dataSnapshot.toString() + " and " + s);
            for (final LobbyListenerSlave slave : slaves) {
                slave.getLobbyList().remove(dataSnapshot.getKey());
                slave.getLobbyList().put(dataSnapshot.getKey(), (LobbyDTO) dataSnapshot.getValue());
                slave.setAborted(false);
                handler.post(slave);
            }
        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved : " + dataSnapshot.toString());
            for (final LobbyListenerSlave slave : slaves) {
                slave.getLobbyList().remove(dataSnapshot.getKey());
                slave.setAborted(false);
                handler.post(slave);
            }
        }

        @Override
        public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
            Log.d(TAG, "onChildMoved : " + dataSnapshot.toString() + " and " + s);
        }

        @Override
        public void onCancelled(final FirebaseError firebaseError) {
            Log.d(TAG, "onCancelled : " + firebaseError.toString());
            for (final LobbyListenerSlave slave : slaves) {
                slave.setAborted(true);
                handler.post(slave);
            }
        }
    }

    static class GameListListener implements ChildEventListener {
        // TODO : This class should be moved to the LobbyListener class........
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {


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
