/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.fragments.AboutFragment;
import com.undyingideas.thor.skafottet.fragments.CreateLobbyFragment;
import com.undyingideas.thor.skafottet.fragments.EndOfGameFragment;
import com.undyingideas.thor.skafottet.fragments.HangmanGameFragment;
import com.undyingideas.thor.skafottet.fragments.HelpFragment;
import com.undyingideas.thor.skafottet.fragments.LobbySelectorFragment;
import com.undyingideas.thor.skafottet.game.HangedMan;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.GameSoundNotifier;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.FontUtils;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.util.ArrayList;
import java.util.Set;

/**
 * The main game activity.
 * It has the responsibility of showing the game itself and the end game display to the user.
 *
 * This contains the basic stuff for handling all the fragments which are displayed here.
 * @author rudz
 */
public class GameActivity extends SoundAbstract implements
        LobbySelectorFragment.OnMultiPlayerPlayerFragmentInteractionListener,
        EndOfGameFragment.OnEndGameButtonClickListenerInterface,
        CreateLobbyFragment.OnCreateLobbyFragmentInteractionListener,
        GameSoundNotifier
{

    private static final String TAG = "GameActivity";
    private static String s_possibleWord;
    private Fragment currentFragment;

    private HangmanGameFragment hangmanGameFragment;
    private EndOfGameFragment endOfGameFragment;
    private Toolbar toolbar;

//    private ProgressBar topProgressBar;
//
//    private MarqueeView mv;
//    private TextView tv;
//    private Handler handler;
//    private Runnable mvReset;

    public static String getS_possibleWord() {
        return s_possibleWord;
    }

    public static void setS_possibleWord(final String s_possibleWord) {
        GameActivity.s_possibleWord = s_possibleWord;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FontUtils.setDefaultFont(getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
        FontUtils.setDefaultFont(getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
        FontUtils.setDefaultFont(getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
        FontUtils.setDefaultFont(getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);

        initSound();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            toolbar.setLogo(R.drawable.icon);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* configure the custom marquee view linked with a textview */
//        mvReset = new StartMarquee();
//        handler = new Handler();

//        mv = (MarqueeView) findViewById(R.id.game_marqueeView);
//        tv = (TextView) findViewById(R.id.game_text_view_marq);
//        tv.setText(GameUtility.mpc.name == null ? "Du er ikke logget ind, log ind for at hænge andre." : "Du er logget ind som " + GameUtility.mpc.name);
//        mv.setPauseBetweenAnimations(500);
//        mv.setSpeed(10);
//        handler.postDelayed(mvReset, 500);

//        topProgressBar = (ProgressBar) findViewById(R.id.topProgressBar);
//        topProgressBar.setVisibility(View.INVISIBLE);

        if (getIntent() != null) {
            final Bundle bundle = getIntent().getExtras();
            final int gameMode = bundle.getInt(Constant.KEY_MODE);
            switch (gameMode){
            case Constant.MODE_CONT_GAME:
                // throw in the game from the preferences
                addFragment(HangmanGameFragment.newInstance((SaveGame) bundle.getParcelable(Constant.KEY_SAVE_GAME))); //  (SaveGame) GameUtility.s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class)));
                break;
                case Constant.MODE_SINGLE_PLAYER :
                addFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
                break;
                case Constant.MODE_MULTI_PLAYER:
                    getFirstActiveGame();
                    break;
                case Constant.MODE_MULTI_PLAYER_2:
                addFragment(LobbySelectorFragment.newInstance(true));
                    break;
                case Constant.MODE_MULTI_PLAYER_LOBBY:
                addFragment(CreateLobbyFragment.newInstance(true));
                    break;
                case Constant.MODE_ABOUT:
                addFragment(new AboutFragment());
                    break;
                case Constant.MODE_HELP:
                addFragment(new HelpFragment());
                    break;
            }
        } else {
            addFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
        }
    }

    private void getFirstActiveGame(){
        if(GameUtility.mpc.name != null){
            ArrayList<LobbyDTO> dtolist = new ArrayList<>();
            dtolist.addAll(GameUtility.mpc.lc.lobbyList.values());
            for (LobbyDTO dto : dtolist)
                for(LobbyPlayerStatus status : dto.getPlayerList())
                    if (status.getName().equals(GameUtility.mpc.name) && status.getScore() == -1){
                        String k = "";
                        final Set<String> keys = GameUtility.mpc.lc.lobbyList.keySet();
                        for (final String key : keys)
                            if (dto.equals(GameUtility.mpc.lc.lobbyList.get(key)))
                                { k = key; break; }
                        currentFragment = HangmanGameFragment.newInstance(new SaveGame(new HangedMan(dto.getWord()), true, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du", k));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, currentFragment).commit(); // custom commit without backstack
                    }

        } else {
            Log.e("GameActivity 168", "not logged in error");
            onBackPressed();
        }
        WindowLayout.showSnack("Du har ingen spil klar", toolbar.getRootView(), false);
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }
    }

    private void addFragment(final Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).commit();
    }

    private void replaceFragment(final Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void playGameSound(final int sound) {
        playSound(sound);
    }

//    private class StartMarquee implements Runnable {
//        @Override
//        public void run() {
//            mv.startMarquee();
//        }
//    }

    @Override
    public void onPlayerClicked(final String playerName) {
        Log.d(TAG, playerName + " clicked.");
    }

    @Override
    public void startNewMultiplayerGame(final String opponentName, final String theWord) {
        Log.d(TAG, "Want to start new game against : " + opponentName + " with word : " + theWord);
        GameUtility.mpc.setRunnable(null); // hack til at fjerne updaterings fejl
        replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(theWord), true, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du", opponentName)));
    }

    @Override
    public void onEndGameButtonClicked(final boolean newGame) {
        if (newGame) {
            replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
        } else {
            // go back to the menu
            finish();
        }
    }
}

