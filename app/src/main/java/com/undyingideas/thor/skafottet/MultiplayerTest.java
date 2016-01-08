package com.undyingideas.thor.skafottet;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerLobbyAdapter;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerPlayersAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MultiplayerTest extends AppCompatActivity implements Runnable {

    private ListView listView;
    private final ArrayList<PlayerDTO> players = new ArrayList<>();
    private MultiplayerController multiplayerController;
    private Firebase myFirebaseRef;
    private MultiplayerPlayersAdapter playerAdapter;
    private MultiplayerLobbyAdapter lobbyAdapter;
    private ArrayList<LobbyDTO> l;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_test);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloatListener());

        listView = (ListView) findViewById(R.id.multiplayer_player_list);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hangmandtu.firebaseio.com");

        multiplayerController = new MultiplayerController(myFirebaseRef, this);

        playerAdapter = new MultiplayerPlayersAdapter(this, R.layout.multiplayer_player_list_row, players);
        listView.setAdapter(playerAdapter);
        listView.setOnItemClickListener(new OnMultiPlayerPlayerClick());
        Log.d("firebase", "Multiplayertest started");
    }

    @Override
    public void run() {
        if (multiplayerController.name == null) {
            playerAdapter = new MultiplayerPlayersAdapter(this, R.layout.multiplayer_player_list_row, players);
            listView.setAdapter(playerAdapter);
            players.clear();
            players.addAll(multiplayerController.pc.playerList.values());
            playerAdapter.notifyDataSetChanged();
        } else {
            l = new ArrayList<>();
            l.addAll(multiplayerController.lc.lobbyList.values());
            lobbyAdapter = new MultiplayerLobbyAdapter(multiplayerController.name, this, R.layout.multiplayer_player_list_row, l);
            listView.setAdapter(lobbyAdapter);
            lobbyAdapter.notifyDataSetChanged();
        }
    }

    protected void login(final String name) {
        Log.d("firebaselogin", "login");
        //startActivity(new Intent(this, MultiplayerTestLobby.class).putExtra("name", name));
        multiplayerController.login(name);
    }

    private class OnMultiPlayerPlayerClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WeakReference<Context> contextWeakReference = new WeakReference<>(view.getContext());
            final Context context =  contextWeakReference.get();
            if (context != null) {
                Log.d("NG", String.valueOf(id));
                // do stuff!!!
                Snackbar.make(view, ((MultiplayerTest) context).players.get(position).getName(), Snackbar.LENGTH_SHORT).show();
                if(multiplayerController.name == null)
                    login(((MultiplayerTest) context).players.get(position).getName());
                else {
                    for (final LobbyPlayerStatus s : l.get(position).getPlayerList())
                        if (s.getName().equals(multiplayerController.name))
                            for(final WordStatus w : s.getWordList()) {
                                if (w.getScore() == -1) {
                                    Log.d("firebaseopengame", w.getWordID());
                                    //TODO
                                    return;
                                }
                            }

                }
            }
        }
    }

    private static class FloatListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}
