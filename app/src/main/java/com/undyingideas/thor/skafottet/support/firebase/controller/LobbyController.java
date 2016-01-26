package com.undyingideas.thor.skafottet.support.firebase.controller;

import android.os.Bundle;
import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.game.HangedMan;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.support.firebase.LobbyListener;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.highscore.local.Player;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class LobbyController {

    private final Firebase ref;
    private final Firebase lobbyRef;
    private final MultiplayerController mpcRef;
    public final HashMap<String, LobbyDTO> lobbyList = new HashMap<>();

    public LobbyController(final MultiplayerController mp, final Firebase ref){
        this.ref = ref;
        lobbyRef = ref.child("Lobby");
        mpcRef = mp;
    }

    public void createLobby( final LobbyDTO lobbyDTO){

        //This genetates uniqe ID in firebase
        final Firebase newPostRef = lobbyRef.push();

        newPostRef.child("word").setValue(lobbyDTO.getWord());
        for(final LobbyPlayerStatus status: lobbyDTO.getPlayerList())
                newPostRef.child(status.getName()).setValue(status.getScore());

        // Add lobby id to players lobby list
        final String postId = newPostRef.getKey();
        for(final LobbyPlayerStatus s : lobbyDTO.getPlayerList()) {
            ref.child("Players").child(s.getName()).child("gameList").push().setValue(postId);
        }
    }

    public void addLobbyListener(final String key) {
        ref.child("Lobby").child(key).addChildEventListener(new LobbyListener(mpcRef));
    }

    public void updateLobby(final String s, final String s1, final int numWrongLetters) {
        ref.child("Lobby").child(s).child(s1).setValue(numWrongLetters);
    }

    public String getOppNames(final String lobbykey, final String yourname) {
        try {
            final LobbyDTO dto = lobbyList.get(lobbykey);
            StringBuilder sb = new StringBuilder(15);
            sb.append("Modstander: ");
            for (final LobbyPlayerStatus lps : dto.getPlayerList()) if (!lps.getName().equals(yourname)) sb.append(lps.getName()).append(" , ");
            return sb.length() > 12 ? sb.substring(0, sb.length() - 3) : sb.toString();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Bundle getFirstActiveGame() {
        if (GameUtility.mpc.name != null) {
            final ArrayList<LobbyDTO> dtolist = new ArrayList<>();
            dtolist.addAll(lobbyList.values());
            for (final LobbyDTO dto : dtolist) {
                for (final LobbyPlayerStatus status : dto.getPlayerList()) {
                    if (status.getScore() == -1 && status.getName().equals(GameUtility.mpc.name)) {
                        String k = "";
                        final Set<String> keys = lobbyList.keySet();
                        for (final String key : keys) {
                            if (dto.equals(lobbyList.get(key))) {
                                k = key;
                                break;
                            }
                        }
                        final Bundle bundle = new Bundle();
                        final SaveGame saveGame = new SaveGame(new HangedMan(dto.getWord()), true, GameUtility.me, new Player(k));
                        bundle.putParcelable(Constant.KEY_SAVE_GAME, saveGame);
                        return bundle;
                    }
                }
            }
        } else {
            Log.e("GameActivity 168", "not logged in error");
        }
        return null;
    }
}
