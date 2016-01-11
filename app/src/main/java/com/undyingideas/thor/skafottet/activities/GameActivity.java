package com.undyingideas.thor.skafottet.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.fragments.AboutFragment;
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

public class GameActivity extends AppCompatActivity implements
        MultiPlayerPlayerFragment.OnMultiPlayerPlayerFragmentInteractionListener,
        EndOfGameFragment.OnEndGameButtonClickListenerInterface,
        ProgressBarInterface,
        Login.LoginListener
{

    private static final String TAG = "GameActivity";
    private static String s_possibleWord;
    private Fragment currentFragment;

    private ProgressBar topProgressBar;

    public TextView t;
    public static String getS_possibleWord() {
        return s_possibleWord;
    }

    public static void setS_possibleWord(final String s_possibleWord) {
        GameActivity.s_possibleWord = s_possibleWord;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            tb.setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            t = new TextView(this);
            t.setText("      login");
            t.setOnClickListener(new LoginClick());
            tb.addView(t);
        }

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
            } else if (gameMode == Constant.MODE_MULTI_PLAYER || gameMode == Constant.MODE_MULTI_PLAYER_2) {
                // just show the current player list
                addFragment(MultiPlayerPlayerFragment.newInstance(true));
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
    public void onFinishLoginDialog(String title, String pass) {
        Log.d("Login", title + pass);

    }

    @Override
    public void onCancel() {
        ;
    }

    private class LoginClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Login.newInstance("Login", "OK", "Cancel", true).show(getSupportFragmentManager(), "Login");
        }
    }
}

