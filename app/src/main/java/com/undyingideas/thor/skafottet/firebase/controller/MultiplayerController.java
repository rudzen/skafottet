package com.undyingideas.thor.skafottet.firebase.controller;

import android.os.Handler;
import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;
import com.undyingideas.thor.skafottet.game_ui.hichscorecontent.HighScoreContent;
import com.undyingideas.thor.skafottet.game_ui.hichscorecontent.HighScoreDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by theis on 06-01-2016.
 */
public class MultiplayerController {
    public ArrayList<LobbyDTO> games;
    public LobbyController lc;
    public PlayerController pc;
    final Firebase ref;
    String name;
    public HashMap<String, PlayerDTO> playerList = new HashMap<>();
    public HashMap<String, LobbyDTO> lobbyList = new HashMap<>();
    private Handler updateHandler;
    private Runnable playerUpdater;

    public MultiplayerController(final Firebase ref, final Runnable playerUpdater) {
        this.ref = ref;
        games = new ArrayList<>();
        lc = new LobbyController(this, ref);
        pc = new PlayerController(this, ref);
        updateHandler = new Handler();
        this.playerUpdater = playerUpdater;
        Log.d("firebase", "Creating MultiplayerController");
    }

    public void createLobby(LobbyDTO dto) {
        lc.createLobby(dto);
    }

    public boolean login(String name) {
        if (playerList.containsKey(name)) {
            logout();
            this.name = name;
            pc.addListener(name);
            return true;
        } else return false;
    }

    public void logout() {
        if (name == null) return;
        lc = new LobbyController(this, ref);
        games.clear();
    }

    public void update() {
        updateHandler.post(playerUpdater);
    }

    public void lobbyUpdate() {
        updateHandler.post(playerUpdater);
    }

    public List<HighScoreContent.HighScoreItem> getHighScoreItems() {
        final ArrayList<PlayerDTO> players = new ArrayList<>();
        final ArrayList<LobbyDTO> playerLobbys = new ArrayList<>();

        ArrayList<HighScoreDTO> playerHighScoreList = new ArrayList<>();

        players.clear();
        players.addAll(playerList.values());
        playerLobbys.clear();

        playerLobbys.addAll(lobbyList.values());
        //We look inside all lobbys
        //Then inside all lobbys we look at all players
        //If the player match with the lobby being looked at we add the score and word from the lobby.

        for (PlayerDTO player : players) {
            //Make a HighScoreDTO
            HighScoreDTO highScoreDTO = new HighScoreDTO(player);
            //Make the list that the player is in
            ArrayList<LobbyPlayerStatus> gameStatus = new ArrayList<>();

            //Look at all lobbys where the player has a key to
            for (String gamekey : player.getGameList()) {
                //I get the correct lobby status in the lobby with the key
                //Make sure not to get null into loop
                if (!lobbyList.isEmpty()) {
                    for (LobbyPlayerStatus status : lobbyList.get(gamekey).getPlayerList()) {
                        //I then look at all playerStatus in the lobby and compare it with the name im looking for.
                        if (status.getName().equals(player.getName())) {
                            gameStatus.add(status);
                        }
                    }
                }

            }
            highScoreDTO.setLobbyList(gameStatus);
            playerHighScoreList.add(highScoreDTO);
        }


        //Now we have a arraylist of playerHighScoreDTOs with the matching name and lobbystatus.

        HighScoreContent.HighScoreItem item;

        int i = 1;
        List<HighScoreContent.HighScoreItem> list = new ArrayList<>();

      //  playerHighScoreList = sortHighScoreList(playerHighScoreList);
        Collections.sort(playerHighScoreList);
        //Now i have to make the list of highscore dtos finally.
        for (HighScoreDTO highScoreDTO : playerHighScoreList) {
            //Add a Item with all of the players lobby words.
            item = HighScoreContent.createHighScoreItem(i++, highScoreDTO.getPlayer(), highScoreDTO.getLobbyList());
            list.add(item);
            HighScoreContent.addItem(item);
        }



        return list;
    }


}
