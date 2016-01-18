package com.undyingideas.thor.skafottet.support.highscore.online;

import android.support.annotation.NonNull;

import com.undyingideas.thor.skafottet.support.firebase.gufguf.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.firebase.gufguf.PlayerDTO;

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
