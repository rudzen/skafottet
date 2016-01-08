package com.undyingideas.thor.skafottet.game_ui.hichscorecontent;

import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;

/**
 * Created by Gump on 08-01-2016.
 */
public class HighScoreDTO implements Comparable {

    PlayerDTO player;
    ArrayList<LobbyPlayerStatus> lobbyList;


    public HighScoreDTO(PlayerDTO player) {
        this.player = player;
        lobbyList = new ArrayList<>();
    }



    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public ArrayList<LobbyPlayerStatus> getLobbyList() {
        return lobbyList;
    }

    public void addLobbyStatus(LobbyPlayerStatus l){
        lobbyList.add(l);
    }

    public void setLobbyList(ArrayList<LobbyPlayerStatus> lobbyList) {
        this.lobbyList = lobbyList;
    }

    @Override
    public int compareTo(Object temp) {
        HighScoreDTO other = (HighScoreDTO) temp;
        //High score gets highest
        if(player.getScore() < other.getPlayer().getScore()){
            return 1;
        }else if(player.getScore() > other.getPlayer().getScore()){
            return -1;
        }
        else return 0;
    }
}
