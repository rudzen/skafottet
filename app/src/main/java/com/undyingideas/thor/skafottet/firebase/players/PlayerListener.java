package com.undyingideas.thor.skafottet.firebase.players;

import android.os.Handler;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
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
public class PlayerListener {
    // TODO : Code the bastard

    private final Handler handler = new Handler();

    private final ArrayList<PlayerListenerSlave> slaves = new ArrayList<>();

    private final static String PLAYERS = "users";

    private final static String GAME_LIST = "gameList";
    private final static String SCORE = "score";
    private final static String EMAIL = "email";
    private final static String NAME = "name";
    public final static String HAS_LOGGED_IN_PW = "hasLoggedInWithPassword";
    public final static String TIMESTAMP_VALUE = "timestamp";
    public final static String TIMESTAMP_CHILD = TIMESTAMP_VALUE + "Joined";


    private final GameListListener gameListListener;
    private final NameGetter nameGetter;

    public PlayerListener() {
        gameListListener = new GameListListener();
        nameGetter = new NameGetter();
    }

    public void setListeners() {
        firebase.child(PLAYERS).keepSynced(true);
        if (settings.auth_status > 0) {
            firebase.child(PLAYERS).addChildEventListener(nameGetter);
            if (settings.auth_status == SettingsDTO.AUTH_USER) { // play nice, could be the user has logged in for real since last check.
                firebase.child(PLAYERS).child(EMAIL).child(GAME_LIST).addChildEventListener(gameListListener);
            }
        }

    }

    public void removeListeners() {
        firebase.child(PLAYERS).keepSynced(false);
        firebase.child(PLAYERS).removeEventListener(nameGetter);
        firebase.child(PLAYERS).child(EMAIL).child(GAME_LIST).removeEventListener(gameListListener);
    }

    public void addSlave(final PlayerListenerSlave playerListenerSlave) {
        slaves.add(playerListenerSlave);
    }

    public void removeSlave(final PlayerListenerSlave playerListenerSlave) {
        slaves.remove(playerListenerSlave);
    }

    public void clearSlaves() {
        slaves.clear();
    }

    public boolean hasSlave(final PlayerListenerSlave playerListenerSlave) {
        return slaves.contains(playerListenerSlave);
    }

    private void runSlaves() {
        for (final PlayerListenerSlave slave : slaves) {
            handler.post(slave);
        }
    }

    private class NameGetter implements ChildEventListener {

        private static final String TAG = "NameGetter";

//        private PlayerDTO getDTO(final DataSnapshot dataSnapshot) {
//            try {
//                final PlayerDTO dto = new PlayerDTO(dataSnapshot.getKey());
//                dto.setHasLoggedInWithPassword(dataSnapshot.child());
//                dto.setScore(Integer.valueOf(dataSnapshot.child(SCORE).getValue().toString()));
//                dto.setPassword(dataSnapshot.child(PASSWORD).getValue().toString());
//                if (dataSnapshot.hasChild(GAME_LIST)) {
//                    for (final DataSnapshot ds : dataSnapshot.child(GAME_LIST).getChildren()) {
//                        dto.getGameList().add(ds.getValue().toString());
//                    }
//                }
//                return dto;
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }

        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
            Log.d(TAG, "onChildAdded : " + dataSnapshot.toString() + " and " + s);
            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                for (final PlayerListenerSlave slave : slaves) {
                    slave.addPlayer(dataSnapshot.getKey(), dataSnapshot.getValue(PlayerDTO.class));
                    slave.setAborted(false);
                    handler.post(slave);
                }
            }
        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
            Log.d(TAG, "onChildChanged : " + dataSnapshot.toString() + " and " + s);
            for (final PlayerListenerSlave slave : slaves) {
                slave.getPlayerList().remove(dataSnapshot.getKey());
                slave.getPlayerList().put(dataSnapshot.getKey(), (PlayerDTO) dataSnapshot.getValue(PlayerDTO.class));
                slave.setAborted(false);
                handler.post(slave);
            }
        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved : " + dataSnapshot.toString());
            for (final PlayerListenerSlave slave : slaves) {
                slave.getPlayerList().remove(dataSnapshot.getKey());
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
            for (final PlayerListenerSlave slave : slaves) {
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
