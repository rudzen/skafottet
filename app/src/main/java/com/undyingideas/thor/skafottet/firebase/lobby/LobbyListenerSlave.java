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
        final LobbyListenerReceiver playerListenerReceiver = mWeakReference.get();
        if (playerListenerReceiver != null) {
            if (!aborted) {
                playerListenerReceiver.onLobbyDataReceived(this);
            } else {
                playerListenerReceiver.onLobbyDataTransferAbort(true);
            }
        }

    }

    public static HashMap<String, LobbyDTO> getLobbyList() {
        return lobbyList;
    }

    public static void addLobby(final String key, final LobbyDTO lobbyDTO) {
        /* to avoid potential reference issues, just duplicate the parsed dto */
        lobbyList.put(key, new LobbyDTO(lobbyDTO));
    }

    public static LobbyDTO getLobby(final String key) {
        return lobbyList.get(key);
    }

    public static void setAborted(final boolean newValue) {
        aborted = newValue;
    }

    public static boolean isAborted() {
        return aborted;
    }

}
