package com.undyingideas.thor.skafottet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerPlayersAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MultiplayerTest extends AppCompatActivity {

    private ListView listView;
    private final ArrayList<PlayerDTO> players = new ArrayList<>();
    private MultiplayerController multiplayerController;
    private Firebase myFirebaseRef;
    private Handler handler;
    private MultiplayerPlayersAdapter adapter;
    private Runnable updater;

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
        myFirebaseRef = new Firebase("https://hangmandtu.firebaseio.com/");

        multiplayerController = new MultiplayerController(myFirebaseRef);

        adapter = new MultiplayerPlayersAdapter(this, R.layout.multiplayer_player_list_row, players);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnMultiPlayerPlayerClick());

        handler = new Handler();

        updater = new UpdateList();
        handler.postDelayed(updater, 2000);
    }

    private class UpdateList implements Runnable {

        @Override
        public void run() {
            Log.d("firebase", "Updater in action");

            players.clear();
            players.addAll(multiplayerController.playerList.values());

            Log.d("firebase", players.toString());


            adapter.notifyDataSetChanged();


//
//        newGameItems[0] = new NewGameItem(0, "Wuhuu..", "Bare start spillet mester", R.drawable.forkert6);
//        newGameItems[1] = new NewGameItem(1, "Anden mulighed", "For hulvate dude!", R.drawable.forkert5);
//        newGameItems[2] = new NewGameItem(1, "Nothing here!...", "Starter også bare spillet !", R.drawable.forkert4);

            //final ListView listViewItems = new ListView(this);
        }
    }

    private static class OnMultiPlayerPlayerClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WeakReference<Context> contextWeakReference = new WeakReference<>(view.getContext());
            final Context context =  contextWeakReference.get();
            if (context != null) {
                Log.d("NG", String.valueOf(id));
                // do stuff!!!

//                ((MenuActivity) context).md.dismiss();
//                ((MenuActivity) context).newGameID = id;
//                ((MenuActivity) context).endMenu("startNewGame", ((MenuActivity) context).buttons[BUTTON_NEW_GAME]);
                //((MenuActivity) context).startNewGame(id);
            }
        }
    }

    private static class FloatListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}
