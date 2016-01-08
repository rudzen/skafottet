package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.dialogs.YesNo;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;

public class MainActivity extends AppCompatActivity implements YesNo.YesNoResultListener, MultiPlayerPlayerFragment.OnMultiPlayerPlayerFragmentInteractionListener {
    
    private static String s_possibleWord;

    /*
     1 = single player
     2 = multiplayer
     */
    private int gameMode;

    public static String getS_possibleWord() {
        return s_possibleWord;
    }

    public static void setS_possibleWord(final String s_possibleWord) {
        MainActivity.s_possibleWord = s_possibleWord;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            final Bundle bundle = getIntent().getExtras();
            gameMode = bundle.getInt(Constant.KEY_GAME_MODE);

            if (gameMode == Constant.KEY_SINGLE_PLAYER) {
                addFragment(HangmanButtonFragment.newInstance(0, false, GameUtility.s_prefereces.getListString(GameUtility.KEY_MULIGE_ORD)));
            } else if (gameMode == Constant.KEY_MULTI_PLAYER) {
                // just show the current player list
                addFragment(MultiPlayerPlayerFragment.newInstance(true));
            }
        } else {
            addFragment(HangmanButtonFragment.newInstance(0, false, GameUtility.s_prefereces.getListString(GameUtility.KEY_MULIGE_ORD)));
        }
    }

    private void addFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onDone(final boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            final HangmanButtonFragment startGame = new HangmanButtonFragment();
            final Bundle data = new Bundle();
            data.putBoolean(GameUtility.KEY_IS_HOT_SEAT, true);
            data.putString("theWord", s_possibleWord);
            startGame.setArguments(data);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, startGame)
                        .addToBackStack(null)
                            .commit();
        }
        else Log.d("wordPicer", "wordDenied");
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //onBackPressed();
            Log.d("main", "button pressed");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        final int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onPlayerClicked(final String playerName) {
        Log.d("MainActivity", playerName + " clicked.");
    }

    @Override
    public void startNewMultiplayerGame(final String opponentName, final String theWord) {
        Log.d("MainActivity", "Wan't to start new game against : " + opponentName + " with word : " + theWord);
        addFragment(HangmanButtonFragment.newInstance(opponentName, theWord));
    }
}
