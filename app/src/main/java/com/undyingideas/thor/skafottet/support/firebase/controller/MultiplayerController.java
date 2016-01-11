package com.undyingideas.thor.skafottet.support.firebase.controller;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;
import com.undyingideas.thor.skafottet.support.highscore.online.HighScoreContent;
import com.undyingideas.thor.skafottet.support.highscore.online.HighScoreDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 06-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class MultiplayerController {
    @Nullable
    public LobbyController lc;
    public PlayerController pc;
    public WordListController wlc;
    private final Firebase ref;
    @Nullable
    public String name;
    private Handler updateHandler;
    private Runnable playerUpdater;

    public MultiplayerController(final Firebase ref, final Runnable playerUpdater) {
        this.ref = ref;
        pc = new PlayerController(this, ref);
        lc = new LobbyController(this,ref);
        wlc = new WordListController(ref);
        Log.d("firebaseMulti", "hej");
        updateHandler = new Handler();
        this.playerUpdater = playerUpdater;
    }

    public MultiplayerController(final Firebase fb) {
        ref = fb;
        pc = new PlayerController(this, ref);
        lc = new LobbyController(this,ref);
        wlc = new WordListController(ref);
    }

    public void setRunnable(final Runnable r) {
        if(updateHandler == null) updateHandler = new Handler();
        playerUpdater = r;
        update();
    }

    public void createLobby(final LobbyDTO dto) {
        if (lc != null) {
            lc.createLobby(dto);
        }
    }

    public boolean login(final String name) {
        if (pc.playerList.containsKey(name)) {
            logout();
            this.name = name;
            pc.addListener(name);
            lc = new LobbyController(this, ref);
            for (final String key : pc.playerList.get(name).getGameList())
            {Log.d("firebase", key); lc.addLobbyListener(key);}
            return true;
        } else return false;
    }

    private void logout() {
        if (name == null) return;
        name = null;
        lc = null;
    }

    public void update() {
        if (updateHandler != null )
            updateHandler.post(playerUpdater);
    }

    public void lobbyUpdate() {
        if (updateHandler != null )
            updateHandler.post(playerUpdater);
    }

    public List<HighScoreContent.HighScoreItem> getHighScoreItems() {
        final ArrayList<PlayerDTO> players = new ArrayList<>();
        final ArrayList<LobbyDTO> playerLobbys = new ArrayList<>(); // aldrig brugt?!?!

        final ArrayList<HighScoreDTO> playerHighScoreList = new ArrayList<>();

        players.clear();
        players.addAll(pc.playerList.values());
        playerLobbys.clear();

        if (lc != null) {
            playerLobbys.addAll(lc.lobbyList.values());
        }
        //We look inside all lobbys
        //Then inside all lobbys we look at all players
        //If the player match with the lobby being looked at we add the score and word from the lobby.

        //Make the list that the player is in
        final ArrayList<LobbyPlayerStatus> gameStatus = new ArrayList<>();

        for (final PlayerDTO player : players) {
            //Make a HighScoreDTO
            final HighScoreDTO highScoreDTO = new HighScoreDTO(player);

            //Clear the list
            gameStatus.clear();

            //Look at all lobbys where the player has a key to
            for (final String gamekey : player.getGameList()) {
                //I get the correct lobby status in the lobby with the key
                //Make sure not to get null into loop
                if (!lc.lobbyList.isEmpty()) {
                    for (final LobbyPlayerStatus status : lc.lobbyList.get(gamekey).getPlayerList()) {
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
        final List<HighScoreContent.HighScoreItem> list = new ArrayList<>();

      //  playerHighScoreList = sortHighScoreList(playerHighScoreList);
        Collections.sort(playerHighScoreList);
        //Now i have to make the list of highscore dtos finally.
        for (final HighScoreDTO highScoreDTO : playerHighScoreList) {
            //Add a Item with all of the players lobby words.
            item = HighScoreContent.createHighScoreItem(i++, highScoreDTO.getPlayer(), highScoreDTO.getLobbyList());
            list.add(item);
            HighScoreContent.addItem(item);
        }



        return list;
    }


}