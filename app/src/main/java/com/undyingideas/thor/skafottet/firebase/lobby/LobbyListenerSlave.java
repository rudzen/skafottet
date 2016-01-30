package com.undyingideas.thor.skafottet.firebase.lobby;

import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;

import java.util.HashMap;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.players<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:12
 * </p>
 *
 * The slave class for the {@link LobbyListener} class.<br>
 * It contains the interface which is linked to the relevant activities and/or fragments.<br>
 * It's responsible for holding data which indicates if there has been an update in the lobby list.<br>
 *
 * The data is binded to the class, this makes it more efficient as we just want a single item to update throughout the application.
 */
@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
public class LobbyListenerSlave extends WeakReferenceHolder<LobbyListenerSlave.LobbyListenerReceiver> implements Runnable {

    /* reflects the data structure, String as unique key (email) and playerDTO as the value */
    private static final HashMap<String, LobbyDTO> lobbyList = new HashMap<>();

    private static boolean aborted;

    protected LobbyListenerSlave(final LobbyListenerReceiver lobbyListenerReceiver) {
        super(lobbyListenerReceiver);
    }

    public interface LobbyListenerReceiver {
        /** Called when lobby data is altered. */
        void onLobbyDataReceived(final LobbyListenerSlave playerListenerReceiver);

        /** when operation was aborted (somehow) */
        void onLobbyDataTransferAbort(final boolean aborted);
    }

    @Override
    public void run() {
        final LobbyListenerReceiver playerListenerReceiver = weakReference.get();
        if (playerListenerReceiver != null) {
            if (!aborted) {
                playerListenerReceiver.onLobbyDataReceived(this);
            } else {
                playerListenerReceiver.onLobbyDataTransferAbort(true);
            }
        }

    }

    public HashMap<String, LobbyDTO> getLobbyList() {
        return lobbyList;
    }

    public void addLobby(final String key, final LobbyDTO lobbyDTO) {
        /* to avoid potential reference issues, just duplicate the parsed dto */
        lobbyList.put(key, new LobbyDTO(lobbyDTO));
    }

    public LobbyDTO getLobby(final String key) {
        return lobbyList.get(key);
    }

    public void setAborted(final boolean newValue) {
        aborted = newValue;
    }

    public boolean isAborted() {
        return aborted;
    }

}
