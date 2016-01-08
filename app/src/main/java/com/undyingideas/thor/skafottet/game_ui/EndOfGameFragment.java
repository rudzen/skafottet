package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.utility.GameUtility;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 *
 * @author Thor
 */
public class EndOfGameFragment extends Fragment {
    private static final String KEY_ATTEMPTS = "forsøg";
    private static final String KEY_MULTIPLAYER = "wasHotSeat";
    private static final String KEY_VUNDET = "vundet";
    private static final String KEY_PLAYER_THIS = "spiller";
    private static final String KEY_PLAYER_OPPONENT = "modstander";
    private static final String KEY_THE_WORD = "ordet";

    private static final String TAG = "EndGameFragment";

    // TODO : Skal omkrives til hurtigere og nemmere layout.

    private WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    private ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    String resultText;

    public static EndOfGameFragment newInstance(final boolean isGameWon, final int wrongGuessCount, final CharSequence theWord, final CharSequence thisPlayerName, final CharSequence opponentPlayerName, final boolean isHotSeat) {
        final EndOfGameFragment endOfGameFragment = new EndOfGameFragment();
        final Bundle args = new Bundle();
        args.putBoolean(KEY_VUNDET, isGameWon);
        args.putInt(KEY_ATTEMPTS, wrongGuessCount);
        args.putCharSequence(KEY_THE_WORD, theWord);
        args.putCharSequence(KEY_PLAYER_THIS, thisPlayerName);
        args.putCharSequence(KEY_PLAYER_OPPONENT, opponentPlayerName);
        args.putBoolean(KEY_MULTIPLAYER, isHotSeat);
        endOfGameFragment.setArguments(args);
        return endOfGameFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.activity_end_of_game, container, false);
        endImage = (ImageView) root.findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) root.findViewById(R.id.SpilresultaterWebView);

        final Button endGameBtn = (Button) root.findViewById(R.id.end_game_quit);
        endGameBtn.setOnClickListener(new EndGameListener());

        final Button newGameBtn = (Button) root.findViewById(R.id.end_game_new_game);
        newGameBtn.setOnClickListener(new StartGameListener());

        displayResults(getArguments());
        return root;
    }

    private void displayResults(final Bundle gameData) {

        if (gameData.getBoolean(KEY_VUNDET)) {//checkes if the game is won
            endImage.setImageResource(R.drawable.game_end_won);
            resultText = "<html><body>Tilykke " + gameData.getCharSequence(KEY_PLAYER_THIS) + ", du har vundet <br> <br> " + gameData.getInt(KEY_PLAYER_THIS) + " gættede forkert <b> " + gameData.getInt(KEY_ATTEMPTS) + " gange</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        } else {// or lost
            endImage.setImageResource(R.drawable.game_end_lost);
            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + gameData.getString(KEY_THE_WORD) + "</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        }
        Log.d(TAG, "data: " + gameData.getString("spiller ") + " " + gameData.getString(KEY_ATTEMPTS) + " " + gameData.getBoolean(KEY_VUNDET));
    }

    private class EndGameListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            Log.d(TAG, "going to start Screen");
            final FragmentMenu_OLD fragmentMenuOLD = new FragmentMenu_OLD();
            getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragmentMenuOLD).commit();
        }
    }

    private class StartGameListener implements View.OnClickListener {
        Bundle gameData = new Bundle();

        @Override
        public void onClick(final View v) {

            if (getArguments().getBoolean(KEY_MULTIPLAYER, false)) {//starting new multiyPlayer game by going to wordPicker
                gameData.putBoolean(GameUtility.KEY_IS_HOT_SEAT, true);
                final WordPickerFragment newMultiPGame = new WordPickerFragment();
                newMultiPGame.setArguments(gameData);
                getFragmentManager().beginTransaction().replace(R.id.fragment_content, newMultiPGame).commit();
            } else {//starting new singleplayergame
                gameData.putBoolean(GameUtility.KEY_IS_HOT_SEAT, false);
                final HangmanButtonFragment hangmanButtonFragment = new HangmanButtonFragment();
                hangmanButtonFragment.setArguments(gameData);
                getFragmentManager().beginTransaction().replace(R.id.fragment_content, hangmanButtonFragment).commit();
            }
        }
    }

    @Override
    public void onStart() {//this actually removes the keyboard
        super.onStart();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}