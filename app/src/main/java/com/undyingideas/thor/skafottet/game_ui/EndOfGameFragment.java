package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.undyingideas.thor.skafottet.game.Hanged;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 *
 * @author Thor
 */
public class EndOfGameFragment extends Fragment {
    private static final String KEY_ATTEMPTS = "try";
    private static final String KEY_MULTIPLAYER = "mul";
    private static final String KEY_WON = "won";
    private static final String KEY_PLAYER_THIS = "ply";
    private static final String KEY_PLAYER_OPPONENT = "opp";
    private static final String KEY_THE_WORD = "wor";

    private static final String TAG = "EndGameFragment";

    // TODO : Skal omkrives til hurtigere og nemmere layout.

    private WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    private ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    String resultText;

    @Nullable
    private OnEndGameButtonClickListenerInterface mListener;

    public static EndOfGameFragment newInstance(final boolean isGameWon, final int wrongGuessCount, final CharSequence theWord, final CharSequence thisPlayerName, final CharSequence opponentPlayerName, final boolean isHotSeat) {
        final EndOfGameFragment endOfGameFragment = new EndOfGameFragment();
        final Bundle args = new Bundle();
        args.putBoolean(KEY_WON, isGameWon);
        args.putInt(KEY_ATTEMPTS, wrongGuessCount);
        args.putCharSequence(KEY_THE_WORD, theWord);
        args.putCharSequence(KEY_PLAYER_THIS, thisPlayerName);
        args.putCharSequence(KEY_PLAYER_OPPONENT, opponentPlayerName);
        args.putBoolean(KEY_MULTIPLAYER, isHotSeat);
        endOfGameFragment.setArguments(args);
        return endOfGameFragment;
    }

    public static EndOfGameFragment newInstance(final Hanged gameLogic, final boolean isMultiPlayer, final CharSequence ... playerNames) {
        final EndOfGameFragment endOfGameFragment = new EndOfGameFragment();
        final Bundle args = new Bundle();
        args.putBoolean(KEY_MULTIPLAYER, isMultiPlayer);
        args.putParcelable(Constant.KEY_GAME_LOGIC, gameLogic);
        args.putCharSequence(KEY_PLAYER_THIS, playerNames[0]);
        if (isMultiPlayer) args.putCharSequence(KEY_PLAYER_OPPONENT, playerNames[1]);
        endOfGameFragment.setArguments(args);
        return endOfGameFragment;
    }

    /**
     * As per Google's recommendation, an interface to communicate back to activity.
     * Instructing it what to do, mainly because the fragments should not be allowed to replace themselves.
     */
    public interface OnEndGameButtonClickListenerInterface {
        void onEndGameButtonClicked(final boolean newGame);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnEndGameButtonClickListenerInterface) {
            mListener = (OnEndGameButtonClickListenerInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEndGameButtonClickListenerInterface");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.KEY_GAME_LOGIC)) {
                // TODO : read in the stuff to local variables.
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.fragment_end_game, container, false);
        endImage = (ImageView) root.findViewById(R.id.end_game_image);
        resultaterDisp = (WebView) root.findViewById(R.id.end_game_web_view);

        final Button endGameBtn = (Button) root.findViewById(R.id.end_game_quit);
        endGameBtn.setOnClickListener(new EndGameListener());

        final Button newGameBtn = (Button) root.findViewById(R.id.end_game_new_game);
        newGameBtn.setOnClickListener(new StartGameListener());

        displayResults(getArguments());
        return root;
    }

    private void displayResults(final Bundle args, final boolean isMultiplayer) {

    }


    private void displayResults(final Bundle gameData) {

        if (gameData.getBoolean(KEY_WON)) {//checkes if the game is won
            endImage.setImageResource(R.drawable.game_end_won);
            resultText = "<html><body>Tilykke " + gameData.getCharSequence(KEY_PLAYER_THIS) + ", du har vundet <br> <br> " + gameData.getInt(KEY_PLAYER_THIS) + " gættede forkert <b> " + gameData.getInt(KEY_ATTEMPTS) + " gange</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        } else {// or lost
            endImage.setImageResource(R.drawable.game_end_lost);
            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + gameData.getString(KEY_THE_WORD) + "</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        }
        Log.d(TAG, "data: " + gameData.getString("spiller ") + " " + gameData.getString(KEY_ATTEMPTS) + " " + gameData.getBoolean(KEY_WON));
    }

    private class EndGameListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            Log.d(TAG, "going to start Screen");
            mListener.onEndGameButtonClicked(false);
//            final FragmentMenu_OLD fragmentMenuOLD = new FragmentMenu_OLD();
//            getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragmentMenuOLD).commit();
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
                final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
                hangmanGameFragment.setArguments(gameData);
                getFragmentManager().beginTransaction().replace(R.id.fragment_content, hangmanGameFragment).commit();
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