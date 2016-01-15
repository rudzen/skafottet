package com.undyingideas.thor.skafottet.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.fragments.AboutFragment;
import com.undyingideas.thor.skafottet.fragments.CreateLobbyFragment;
import com.undyingideas.thor.skafottet.fragments.EndOfGameFragment;
import com.undyingideas.thor.skafottet.fragments.HangmanGameFragment;
import com.undyingideas.thor.skafottet.fragments.HelpFragment;
import com.undyingideas.thor.skafottet.fragments.LobbySelectorFragment;
import com.undyingideas.thor.skafottet.game.HangedMan;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.ProgressBarInterface;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.FontUtils;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import asia.ivity.android.marqueeview.MarqueeView;

/**
 * The main game activity.
 * It has the responsibility of showing the game itself and the end game display to the user.
 *
 * This contains the basic stuff for handling all the fragments which are displayed here.
 * @author rudz
 */
public class GameActivity extends AppCompatActivity implements
        LobbySelectorFragment.OnMultiPlayerPlayerFragmentInteractionListener,
        EndOfGameFragment.OnEndGameButtonClickListenerInterface,
        ProgressBarInterface,
        CreateLobbyFragment.OnCreateLobbyFragmentInteractionListener
{

    private static final String TAG = "GameActivity";
    private static String s_possibleWord;
    private Fragment currentFragment;

    private ProgressBar topProgressBar;

    private MarqueeView mv;
    private TextView tv;
    private Handler handler;
    private Runnable mvReset;

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


        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            tb.setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* configure the custom marquee view linked with a textview */
        mvReset = new startMarquee();
        handler = new Handler();

        mv = (MarqueeView) findViewById(R.id.game_marqueeView);
        tv = (TextView) findViewById(R.id.game_text_view_marq);
        tv.setText(GameUtility.mpc.name == null ? "Du er ikke logget ind, log ind for at hænge andre." : "Du er logget ind som " + GameUtility.mpc.name);
        mv.setPauseBetweenAnimations(500);
        mv.setSpeed(10);
        handler.postDelayed(mvReset, 500);

        topProgressBar = (ProgressBar) findViewById(R.id.topProgressBar);
        topProgressBar.setVisibility(View.INVISIBLE);

        if (getIntent() != null) {
            final Bundle bundle = getIntent().getExtras();
            final int gameMode = bundle.getInt(Constant.KEY_MODE);
            if (gameMode == Constant.MODE_CONT_GAME) {
                // throw in the game from the preferences
                addFragment(HangmanGameFragment.newInstance((SaveGame) GameUtility.s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class)));
            } else if (gameMode == Constant.MODE_SINGLE_PLAYER) {
                addFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
            } else if (gameMode == Constant.MODE_MULTI_PLAYER || gameMode == Constant.MODE_MULTI_PLAYER_2) {
                addFragment(LobbySelectorFragment.newInstance(true));
            } else if (gameMode == Constant.MODE_MULTI_PLAYER_LOBBY) {
                addFragment(CreateLobbyFragment.newInstance(true));
            } else if (gameMode == Constant.MODE_ABOUT) {
                addFragment(new AboutFragment());
            } else if (gameMode == Constant.MODE_HELP) {
                addFragment(new HelpFragment());
            }
        } else {
            addFragment(HangmanGameFragment.newInstance(new SaveGame(new HangedMan(), false, GameUtility.mpc.name != null ? GameUtility.mpc.name : "Du")));
        }
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

    private class startMarquee implements Runnable {
        @Override
        public void run() {
            mv.startMarquee();
        }
    }

    @Override
    public void onPlayerClicked(final String playerName) {
        Log.d(TAG, playerName + " clicked.");
    }

    @Override
    public void startNewMultiplayerGame(final String opponentName, final String theWord) {
        Log.d(TAG, "Want to start new game against : " + opponentName + " with word : " + theWord);
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

    @Override
    public void setProgressBar(final boolean visible) {
        topProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

}

