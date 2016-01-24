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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetReciever;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetRecieverData;
import com.undyingideas.thor.skafottet.fragments.AboutFragment;
import com.undyingideas.thor.skafottet.fragments.CreateLobbyFragment;
import com.undyingideas.thor.skafottet.fragments.EndOfGameFragment;
import com.undyingideas.thor.skafottet.fragments.HangmanGameFragment;
import com.undyingideas.thor.skafottet.fragments.HelpFragment;
import com.undyingideas.thor.skafottet.fragments.LobbySelectorFragment;
import com.undyingideas.thor.skafottet.fragments.MenuFragment;
import com.undyingideas.thor.skafottet.fragments.WordListFragment;
import com.undyingideas.thor.skafottet.game.HangedMan;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.services.MusicPlay;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.FontUtils;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Set;

/**
 * The main game activity.
 * It has the responsibility of showing the game itself and the end game display to the user.
 * <p/>
 * This contains the basic stuff for handling all the fragments which are displayed here.
 *
 * @author rudz
 */
public class GameActivity extends SoundAbstract implements
        LobbySelectorFragment.OnMultiPlayerPlayerFragmentInteractionListener,
        CreateLobbyFragment.OnCreateLobbyFragmentInteractionListener,
        IGameSoundNotifier, IFragmentFlipper,
        InternetRecieverData.InternetRecieverInterface
{

    private static final String TAG = "GameActivity";
    /* to handle backpressed when in the menu fragment */
    private static final int BACK_PRESSED_DELAY = 2000;
    /* what where when how who why? */
    private Fragment currentFragment; // what are we?
    private int currentMode; // where are we?
    private long backPressed;

    private InternetRecieverData internetRecieverData;

//    private ProgressBar topProgressBar;
//
//    private MarqueeView mv;
//    private TextView tv;
//    private Handler handler;
//    private Runnable mvReset;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FontUtils.setDefaultFont(getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
        FontUtils.setDefaultFont(getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
        FontUtils.setDefaultFont(getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
        FontUtils.setDefaultFont(getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);

        // take over the loader toast for this act..
        WindowLayout.s_LoadToast = new LoadToast(this);

        if (getIntent() != null && getIntent().getExtras() != null) { // TODO : Figure out more direct game controlling modes..
            final Bundle bundle = getIntent().getExtras().getBundle(Constant.KEY_MODE);
            if (bundle != null) {
                final int gameMode = bundle.containsKey(Constant.KEY_MODE) ? bundle.getInt(Constant.KEY_MODE) : Constant.MODE_MENU;
                switch (gameMode) {
                    case Constant.MODE_MENU:
                        currentMode = Constant.MODE_MENU;
                        addFragment(new MenuFragment());
                        break;
                }
                return;
            }
        }
        currentMode = Constant.MODE_MENU;
        addFragment(new MenuFragment());
    }

    @Override
    protected void onResume() {
        if (soundThread == null) {
            initSound();
            Log.d(TAG, "Sound started");
        }
        internetRecieverData = new InternetRecieverData(this);
        InternetReciever.addObserver(internetRecieverData);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        soundThread.interrupt();
        soundThread = null;
        Log.d(TAG, "Sound removed");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        InternetReciever.removeObserver(internetRecieverData);
        super.onStop();
    }

    private static Bundle getFirstActiveGame() {
        if (GameUtility.mpc.name != null) {
            final ArrayList<LobbyDTO> dtolist = new ArrayList<>();
            dtolist.addAll(GameUtility.mpc.lc.lobbyList.values());
            for (final LobbyDTO dto : dtolist) {
                for (final LobbyPlayerStatus status : dto.getPlayerList()) {
                    if (status.getScore() == -1 && status.getName().equals(GameUtility.mpc.name)) {
                        String k = "";
                        final Set<String> keys = GameUtility.mpc.lc.lobbyList.keySet();
                        for (final String key : keys) {
                            if (dto.equals(GameUtility.mpc.lc.lobbyList.get(key))) {
                                k = key;
                                break;
                            }
                        }
                        final Bundle bundle = new Bundle();
                        final SaveGame saveGame = new SaveGame(new HangedMan(dto.getWord()), true, GameUtility.mpc.name, k);
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

    @Override
    public void finish() {
        playSound(GameUtility.SFX_LOST);
        getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof MenuFragment) {
            if (backPressed + BACK_PRESSED_DELAY > System.currentTimeMillis()) {
                stopService(MusicPlay.intent);
                finish();
            } else {
                WindowLayout.showSnack("Tryk tilbage igen for at flygte i rædsel.", findViewById(R.id.fragment_content), false);
                backPressed = System.currentTimeMillis();
            }
        } else if (currentFragment instanceof WordListFragment && ((WordListFragment) currentFragment).mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            ((WordListFragment) currentFragment).mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            replaceFragment(new MenuFragment());
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
            WindowLayout.hideStatusBar(getWindow(), null);
        }
    }

    private void addFragment(final Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).addToBackStack(null).commit();
    }

    private void replaceFragment(final Fragment fragment) {
        currentFragment = fragment;
        playSound(GameUtility.SFX_MENU_CLICK);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
    }

    /* interface implementations */

    @Override
    public void playGameSound(final int sound) {
        playSound(sound);
    }

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

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    @Override
    public final void flipFragment(final int gameMode) {
        currentMode = gameMode;
//        if (gameMode == Constant.MODE_BACK_PRESSED) {
//            onBackPressed();
        if (gameMode == Constant.MODE_MENU) {
            replaceFragment(new MenuFragment());
        } else if (gameMode == Constant.MODE_SINGLE_PLAYER) {
            replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
        } else if (gameMode == Constant.MODE_MULTI_PLAYER) {
            final Bundle bundle = getFirstActiveGame();
            if (bundle != null) {
                replaceFragment(HangmanGameFragment.newInstance(bundle));
            } else {
                WindowLayout.showSnack("Du har ikke flere spil åbne.", findViewById(R.id.fragment_content), false);
                if (currentFragment instanceof MenuFragment) {
                    ((MenuFragment) currentFragment).showAll();
                }
            }
        } else if (gameMode == Constant.MODE_MULTI_PLAYER_2) {
            replaceFragment(LobbySelectorFragment.newInstance(true));
        } else if (gameMode == Constant.MODE_MULTI_PLAYER_LOBBY) {
            replaceFragment(CreateLobbyFragment.newInstance(true));
        } else if (gameMode == Constant.MODE_ABOUT) {
            replaceFragment(new AboutFragment());
        } else if (gameMode == Constant.MODE_HELP) {
            replaceFragment(new HelpFragment());
        } else if (gameMode == Constant.MODE_WORD_LIST) {
            // this is an activity, so we end the menu.. // TODO : remake as fragments...
            replaceFragment(WordListFragment.newInstance());
//            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
//            final Intent intent = new Intent(this, WordListActivity.class);
//            startActivity(intent);
//            finish();
        } else if (gameMode == Constant.MODE_HIGHSCORE) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            final Intent intent = new Intent(this, PlayerListActivity.class);
            startActivity(intent);
            finish();
        } else if (gameMode == Constant.MODE_FINISH) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            stopService(MusicPlay.intent);
            finish();
        }
    }

    @Override
    public final void flipFragment(final int gameMode, final Bundle bundle) {
        playSound(GameUtility.SFX_MENU_CLICK);
        if (gameMode == Constant.MODE_CONT_GAME) {
            // when the current save game is to be continued.
            replaceFragment(HangmanGameFragment.newInstance((SaveGame) bundle.getParcelable(Constant.KEY_SAVE_GAME)));
        } else if (gameMode == Constant.MODE_END_GAME) {
            // when the game is done and the end game fragment needs to be shown.
            replaceFragment(EndOfGameFragment.newInstance((SaveGame) bundle.getParcelable(Constant.KEY_SAVE_GAME)));
        }
    }

    @Override
    public void onInternetStatusChanged(final int connectionState) {
        GameUtility.connectionStatus = connectionState;
        // TODO : Handle future stuff like if in multiplayer game, notify about game not being able to be saved online.
        if (connectionState == -1) {
            GameUtility.mpc.name = null;
        }
        if (currentFragment instanceof MenuFragment) {
            ((MenuFragment) currentFragment).setLoginButton(connectionState);
        }
    }

    @Override
    public void onInternetStatusChanged(final String connectionState) {
        GameUtility.connectionStatusName = connectionState;
        WindowLayout.showSnack("Forbindelse : " + connectionState, findViewById(R.id.fragment_content), true);
    }
}

