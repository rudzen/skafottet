package com.undyingideas.thor.skafottet.firebase.players;

import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.players<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:12
 * </p>
 *
 * The slave class for the {@link PlayerListener} class.<br>
 * It contains the interface which is linked to the relevant activities and/or fragments.<br>
 * It's responsible for holding data which indicates if there has been an update in the player list.<br>
 *
 * The data is binded to the class, this makes it more efficient as we just want a single item to update throughout the application.
 */
@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
public class PlayerListenerSlave extends WeakReferenceHolder<PlayerListenerSlave.PlayerListenerReceiver> implements Runnable {

    /* reflects the data structure, String as unique key (email) and playerDTO as the value */
    private static final HashMap<String, PlayerDTO> playerList = new HashMap<>();

    private final Comparator<PlayerDTO> comparator = new PlayerDTOScoreComparator();

    private static boolean aborted;

    public PlayerListenerSlave(final PlayerListenerReceiver playerListenerReceiver) {
        super(playerListenerReceiver);
    }

    public interface PlayerListenerReceiver {
        /** Called when player data is altered. */
        void onPlayerDataReceived(final PlayerListenerSlave playerListenerReceiver);

        /** when operation was aborted (somehow) */
        void onPlayerDataTransferAbort(final boolean aborted);
    }

    @Override
    public void run() {
        final PlayerListenerReceiver playerListenerReceiver = mWeakReference.get();
        if (playerListenerReceiver != null) {
            if (!aborted) {
                playerListenerReceiver.onPlayerDataReceived(this);
            } else {
                playerListenerReceiver.onPlayerDataTransferAbort(true);
            }
        }

    }

    public HashMap<String, PlayerDTO> getPlayerList() {
        return playerList;
    }

    public ArrayList<PlayerDTO> getHighScoreItems() {
        final ArrayList<PlayerDTO> returnList = new ArrayList<>(playerList.size());
        for (final PlayerDTO playerDTO : playerList.values()) {
            returnList.add(playerDTO);
        }
        Collections.sort(returnList, comparator);
        return returnList;
    }

    public void addPlayer(final String key, final PlayerDTO playerDTO) {
        /* to avoid potential reference issues, just duplicate the parsed dto */
        playerList.put(key, new PlayerDTO(playerDTO));
    }

    public PlayerDTO getPlayerDTO(final String key) {
        return playerList.get(key);
    }

    public void setAborted(final boolean newValue) {
        aborted = newValue;
    }

    public boolean isAborted() {
        return aborted;
    }

    private static class PlayerDTOScoreComparator implements Comparator<PlayerDTO> {
        @Override
        public int compare(final PlayerDTO lhs, final PlayerDTO rhs) {
            if (lhs.getScore() < rhs.getScore()) return -1;
            if (lhs.getScore() > rhs.getScore()) return 1;
            return 0;
        }
    }
}
