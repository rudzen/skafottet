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

package com.undyingideas.thor.skafottet.support.highscore.online;

import android.support.annotation.NonNull;

import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;

import java.util.ArrayList;

/**
 * Created on 08-01-2016, 12:13.
 * Project : skafottet
 * @author Gump
 */
public class HighScoreDTO implements Comparable {

    private PlayerDTO player;
    private final ArrayList<LobbyPlayerStatus> lobbyList;


    public HighScoreDTO(final PlayerDTO player) {
        this.player = player;
        lobbyList = new ArrayList<>();
    }



    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(final PlayerDTO player) {
        this.player = player;
    }

    public ArrayList<LobbyPlayerStatus> getLobbyList() {
        return lobbyList;
    }

    public void addLobbyStatus(final LobbyPlayerStatus l){
        lobbyList.add(l);
    }

    public void setLobbyList(final ArrayList<LobbyPlayerStatus> lobbyList) {
        this.lobbyList.clear();
        this.lobbyList.addAll(lobbyList);
    }

    @Override
    public int compareTo(@NonNull final Object temp) {
        final HighScoreDTO other = (HighScoreDTO) temp;
        //High score gets highest
        if(player.getScore() < other.getPlayer().getScore()){
            return 1;
        }else return player.getScore() > other.getPlayer().getScore() ? -1 : 0;
    }
}
