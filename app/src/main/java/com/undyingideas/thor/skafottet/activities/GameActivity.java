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
import com.undyingideas.thor.skafottet.fragments.MultiPlayerPlayerFragment;
import com.undyingideas.thor.skafottet.fragments.dialogs.Login;
import com.undyingideas.thor.skafottet.game.Hanged;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.ProgressBarInterface;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import asia.ivity.android.marqueeview.MarqueeView;

public class GameActivity extends AppCompatActivity implements
        MultiPlayerPlayerFragment.OnMultiPlayerPlayerFragmentInteractionListener,
        EndOfGameFragment.OnEndGameButtonClickListenerInterface,
        ProgressBarInterface,
        Login.LoginListener,
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

        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            tb.setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//            mv = new MarqueeView(this);
//            tv = new TextView(this);
//
//            mv.setMinimumWidth(50);
//            tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            tv.setGravity(Gravity.RIGHT);
//            mv.setPauseBetweenAnimations(500);
//
//            tv.setText(GameUtility.mpc.name == null ? "Du er ikke logget ind, log ind for at hænge andre." : "Du er logget ind som " + GameUtility.mpc.name);
//            mv.addView(tv);
//            tb.addView(mv);
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
                addFragment(HangmanGameFragment.newInstance((SaveGame) GameUtility.s_prefereces.getObject(Constant.KEY_SAVE_GAME, SaveGame.class)));
            } else if (gameMode == Constant.MODE_SINGLE_PLAYER) {
                addFragment(HangmanGameFragment.newInstance(new SaveGame(new Hanged(GameUtility.s_prefereces.getListString(GameUtility.KEY_MULIGE_ORD)), false, "Du")));
            } else if (gameMode == Constant.MODE_MULTI_PLAYER) {
                Log.d("theis", "mode = " + gameMode);
                // just show the current player list
                addFragment(MultiPlayerPlayerFragment.newInstance(true));
            } else if (gameMode == Constant.MODE_MULTI_PLAYER_2) {
                addFragment(CreateLobbyFragment.newInstance(true));
            } else if (gameMode == Constant.MODE_ABOUT) {
                addFragment(new AboutFragment());
            } else if (gameMode == Constant.MODE_HELP) {
                addFragment(new HelpFragment());
            }
        } else {
            addFragment(HangmanGameFragment.newInstance(new SaveGame(new Hanged(GameUtility.s_prefereces.getListString(GameUtility.KEY_MULIGE_ORD)), false, "Du")));
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

//    @Override
//    public void onDone(final boolean result) {
//        if(result){
//            Log.d("wordPicker", "WordAccepted");
//            final HangmanGameFragment startGame = new HangmanGameFragment();
//            final Bundle data = new Bundle();
//            data.putBoolean(GameUtility.KEY_IS_HOT_SEAT, true);
//            data.putString("theWord", s_possibleWord);
//            startGame.setArguments(data);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_content, startGame)
//                    .addToBackStack(null)
//                    .commit();
//        }
//        else Log.d("wordPicer", "wordDenied");
//    }

    @Override
    public void onPlayerClicked(final String playerName) {
        Log.d(TAG, playerName + " clicked.");
    }

    @Override
    public void startNewMultiplayerGame(final String opponentName, final String theWord) {
        Log.d(TAG, "Want to start new game against : " + opponentName + " with word : " + theWord);
        replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new Hanged(theWord), true, "Du", opponentName)));
    }

    @Override
    public void onEndGameButtonClicked(final boolean newGame) {
        if (newGame) {
            // only allowed in single player for now.
            replaceFragment(HangmanGameFragment.newInstance(new SaveGame(new Hanged(GameUtility.s_prefereces.getListString(GameUtility.KEY_MULIGE_ORD)), false, "Du")));
        } else {
            finish();
        }
    }

    @Override
    public void setProgressBar(final boolean visible) {
        topProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onFinishLoginDialog(final String title, final String pass) {
        Log.d("Login", title + pass);

    }

    @Override
    public void onCancel() {

    }

    private class LoginClick implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            Login.newInstance("Login", "OK", "Cancel", true).show(getSupportFragmentManager(), "Login");
        }
    }
}

