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

package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.WindowManager;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.login.CreateAccountActivity;
import com.undyingideas.thor.skafottet.activities.login.LoginActivity;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetReciever;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetRecieverData;
import com.undyingideas.thor.skafottet.fragments.AboutFragment;
import com.undyingideas.thor.skafottet.fragments.EndOfGameFragment;
import com.undyingideas.thor.skafottet.fragments.HangmanGameFragment;
import com.undyingideas.thor.skafottet.fragments.HelpFragment;
import com.undyingideas.thor.skafottet.fragments.HighscoreFragment;
import com.undyingideas.thor.skafottet.fragments.LobbySelectorFragment;
import com.undyingideas.thor.skafottet.fragments.MenuFragment;
import com.undyingideas.thor.skafottet.fragments.WordListFragment;
import com.undyingideas.thor.skafottet.game.HangedMan;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.services.MusicPlay;
import com.undyingideas.thor.skafottet.support.classes.SetLoadToaster;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

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
        IGameSoundNotifier, IFragmentFlipper,
        InternetRecieverData.InternetRecieverInterface {

//    CreateLobbyFragment.OnCreateLobbyFragmentInteractionListener,

    protected GoogleApiClient googleApiClient;
    protected Firebase.AuthStateListener authStateListener;
    protected Firebase firebase;

    private static final String TAG = "GameActivity";
    /* to handle backpressed when in the menu fragment */
    private static final int BACK_PRESSED_DELAY = 2000;
    /* what where when how who why? */
    private Fragment mCurrentFragment; // what are we?
    private int mCurrentMode; // where are we?
    private long mBackPressed;
    private boolean mQuitMode;

    private InternetRecieverData mInternetRecieverData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // take over the loader toast for this activity..
        new SetLoadToaster(this).run();

        if (getIntent() != null && getIntent().getExtras() != null) { // TODO : Figure out more direct game controlling modes..
            final Bundle bundle = getIntent().getExtras().getBundle(Constant.KEY_MODE);
            if (bundle != null) {
                final int gameMode = bundle.containsKey(Constant.KEY_MODE) ? bundle.getInt(Constant.KEY_MODE) : Constant.MODE_MENU;
                switch (gameMode) {
                    case Constant.MODE_MENU:
                        mCurrentMode = Constant.MODE_MENU;
                        addFragment(new MenuFragment());
                        break;
                }
                return;
            }
        }
        mCurrentMode = Constant.MODE_MENU;
        addFragment(new MenuFragment());
    }

    @Override
    protected void onResume() {
        if (GameUtility.getSettings().prefsMusic) {
            GameUtility.getMusicPLayIntent().setAction(MusicPlay.ACTION_PLAY);
            startService(GameUtility.getMusicPLayIntent());
        } else {
            GameUtility.getMusicPLayIntent().setAction(MusicPlay.ACTION_STOP);
            startService(GameUtility.getMusicPLayIntent());
        }
        if (mSoundThread == null) {
            initSound();
            Log.d(TAG, "Sound started");
        }
        mInternetRecieverData = new InternetRecieverData(this);
        InternetReciever.addObserver(mInternetRecieverData);
        super.onResume();
    }

    @Override
    protected void onStop() {
        InternetReciever.removeObserver(mInternetRecieverData);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mSoundThread.interrupt();
        mSoundThread = null;
        Log.d(TAG, "Sound removed");
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (mQuitMode) playSound(GameUtility.SFX_LOST);
        getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        // First check if the dialog is showing, if so, close it and don't care about the rest!.
        if (WindowLayout.getMd() != null && WindowLayout.getMd().isShowing()) {
            WindowLayout.getMd().dismiss();
        } else {
            if (mCurrentFragment instanceof MenuFragment) {
                if (mBackPressed + BACK_PRESSED_DELAY > System.currentTimeMillis()) {
                    stopService(GameUtility.getMusicPLayIntent());
                    finish();
                } else {
                    WindowLayout.showSnack("Tryk tilbage igen for at flygte i rædsel.", findViewById(R.id.fragment_content), false);
                    mBackPressed = System.currentTimeMillis();
                }
            } else if (mCurrentFragment instanceof WordListFragment && ((WordListFragment) mCurrentFragment).mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                ((WordListFragment) mCurrentFragment).mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                replaceFragment(new MenuFragment());
            }
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
        mCurrentFragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).addToBackStack(null).commit();
    }

    private void replaceFragment(final Fragment fragment) {
        mCurrentFragment = fragment;
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
//        GameUtility.mpc.setRunnable(null); // hack til at fjerne updaterings fejl
        replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(theWord), true, new PlayerDTO(GameUtility.getMe()), new PlayerDTO(opponentName))));
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    @Override
    public final void flipFragment(final int gameMode) {
        mCurrentMode = gameMode;
//        if (gameMode == Constant.MODE_BACK_PRESSED) {
//            onBackPressed();
        if (gameMode == Constant.MODE_MENU) {
            replaceFragment(new MenuFragment());
        } else if (gameMode == Constant.MODE_SINGLE_PLAYER) {
            replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, new PlayerDTO(GameUtility.getMe()))));
        } else if (gameMode == Constant.MODE_MULTI_PLAYER_2) {
            replaceFragment(LobbySelectorFragment.newInstance(true));
        } else if (gameMode == Constant.MODE_MULTI_PLAYER_LOBBY) {
//            replaceFragment(CreateLobbyFragment.newInstance(true));
        } else if (gameMode == Constant.MODE_ABOUT) {
            replaceFragment(new AboutFragment());
        } else if (gameMode == Constant.MODE_HELP) {
            replaceFragment(new HelpFragment());
        } else if (gameMode == Constant.MODE_WORD_LIST) {
            replaceFragment(WordListFragment.newInstance());
        } else if (gameMode == Constant.MODE_HIGHSCORE) {
//            mQuitMode = false;
            // TODO : Facilitate local highscore..
//            getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
//            final Intent intent = new Intent(this, PlayerListActivity.class);
//            startActivity(intent);
//            finish();
            replaceFragment(HighscoreFragment.newInstance(true));
        } else if (gameMode == Constant.MODE_FINISH) {
            mQuitMode = true;
            getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            stopService(GameUtility.getMusicPLayIntent());
            finish();
        } else if (gameMode == Constant.MODE_SETTINGS) {
            mQuitMode = false;
            getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            final Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            finish();
        } else if (gameMode == Constant.MODE_LOGIN) {
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (gameMode == Constant.MODE_LOGOUT) {
            logout();
            if (mCurrentFragment instanceof MenuFragment) {
                ((MenuFragment) mCurrentFragment).setLoginButton();
            }
        } else if (gameMode == Constant.MODE_CREATE_ACCOUNT) {
            final Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public final void flipFragment(final int gameMode, @NonNull final Bundle bundle) {
        playSound(GameUtility.SFX_MENU_CLICK);
        if (gameMode == Constant.MODE_CONT_GAME || gameMode == Constant.MODE_MULTI_PLAYER) {
            // when the current save game is to be continued.
            replaceFragment(HangmanGameFragment.newInstance((SaveGame) bundle.getParcelable(Constant.KEY_SAVE_GAME)));
        } else if (gameMode == Constant.MODE_END_GAME) {
            // when the game is done and the end game fragment needs to be shown.
            replaceFragment(EndOfGameFragment.newInstance((SaveGame) bundle.getParcelable(Constant.KEY_SAVE_GAME)));
        }
    }

    @Override
    public void flipFragment(@NonNull final Intent intent) throws android.content.ActivityNotFoundException {
        startActivity(intent);
//        finish();
    }

    @Override
    public void onInternetStatusChanged(final int connectionState) {
        GameUtility.setConnectionStatus(connectionState);
        // TODO : Handle future stuff like if in multiplayer game, notify about game not being able to be saved online.
//        if (connectionState == -1) {
//            GameUtility.mpc.name = null;
//        }
//        if (mCurrentFragment instanceof MenuFragment) {
//            ((MenuFragment) mCurrentFragment).setLoginButton();
//        }
    }

    @Override
    public void onInternetStatusChanged(final String connectionState) {
        GameUtility.setConnectionStatusName(connectionState);
        WindowLayout.showSnack("Forbindelse : " + connectionState, findViewById(R.id.fragment_content), true);
    }


    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        WindowLayout.showSnack("Forbindelse til google fejlede.", findViewById(R.id.fragment_content), true);
    }
}

