package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HangmanGameFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_MULTIPLAYER = "mp";
    private static final String KEY_GAME_STATE = "gs";
    private static final String KEY_WORD_LIST = "posw";
    private static final String KEY_WORD_SINGLE = "sinw";

    private static final String KEY_OPPONENT_NAME = "on";


    private LinearLayout[] buttonRows = new LinearLayout[4];

    private ArrayList<Button> listOfButtons;
    private SaveGame currentGame;
    private @DrawableRes final int[] imageRefs = new int[6];

//    private Bundle lastBundle;

//    private Hanged hangedLogic;
//    private final ArrayList<String> possibleWords = new ArrayList<>();
//    private boolean isHotSeat;
//    private String theGuess; //bruges til at holde det aktuelle gæt
    private ImageView noose;
//    private String multiPlayerWord;
//    private String multiPlayerOpponent;
//    private boolean isMultiplayer;

    private TextView usedLetters, textViewWord, status;
//    private EditText input;

//    public static HangmanGameFragment newInstance(final int gameState, final boolean multiPlayer, final ArrayList<String> possibleWords) {
//        final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
//        final Bundle args = new Bundle();
//        args.putInt(KEY_GAME_STATE, gameState);
//        args.putBoolean(KEY_MULTIPLAYER, multiPlayer);
//        args.putStringArrayList(KEY_WORD_LIST, possibleWords);
//        hangmanGameFragment.setArguments(args);
//        return hangmanGameFragment;
//    }
//
//    public static HangmanGameFragment newInstance(final String opponentName, final String theWord) {
//        final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
//        final Bundle args = new Bundle();
//        args.putBoolean(KEY_MULTIPLAYER, true);
//        args.putString(KEY_OPPONENT_NAME, opponentName);
//        args.putString(KEY_WORD_SINGLE, theWord);
//        hangmanGameFragment.setArguments(args);
//        return hangmanGameFragment;
//    }

    public static HangmanGameFragment newInstance(final SaveGame saveGame) {
        final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
        final Bundle args = new Bundle();
        args.putParcelable(Constant.KEY_SAVE_GAME, saveGame);
        hangmanGameFragment.setArguments(args);
        return hangmanGameFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.fragment_hangman_game, container, false);

        noose = (ImageView) root.findViewById(R.id.imageView);

        imageRefs[0] = R.drawable.galge;
        imageRefs[1] = R.drawable.forkert1;
        imageRefs[2] = R.drawable.forkert2;
        imageRefs[3] = R.drawable.forkert3;
        imageRefs[4] = R.drawable.forkert4;
        imageRefs[5] = R.drawable.forkert5;
        imageRefs[6] = R.drawable.forkert6;

        listOfButtons = new ArrayList<>();

        buttonRows[0] = (LinearLayout) root.findViewById(R.id.linearLayout1);
        buttonRows[1] = (LinearLayout) root.findViewById(R.id.linearLayout2);
        buttonRows[2] = (LinearLayout) root.findViewById(R.id.linearLayout3);
        buttonRows[3] = (LinearLayout) root.findViewById(R.id.linearLayout4);

        for (final LinearLayout linearLayout : buttonRows) {
            listOfButtons.addAll(getChildren(linearLayout));
        }

        textViewWord = (TextView) root.findViewById(R.id.visibleText);

        //adding clickhandlers
        for (final Button btn : listOfButtons) {
            btn.setOnClickListener(this);
        }

        if (getArguments() != null) {
            readFromBundle(getArguments());
        }

        resetButtons();


        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setSaveGameStatus();

//        noose.init();
//        noose.setState(gameState);


        // testing :
        if (currentGame.isMultiPlayer()) {
            WindowLayout.showSnack("Modstander: " + currentGame.getNames()[1], textViewWord, true);
        }
        Toast.makeText(getContext(), currentGame.getLogic().getTheWord(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putParcelable(Constant.KEY_SAVE_GAME, currentGame);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(final Bundle savedInstanceState) {
        readFromBundle(savedInstanceState);
        setSaveGameStatus();
        super.onViewStateRestored(savedInstanceState);
    }

    private void setSaveGameStatus() {
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        resetButtons();
        noose.setImageResource(imageRefs[currentGame.getLogic().getNumWrongLetters()]);
    }

    private Bundle setBundle() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.KEY_SAVE_GAME, currentGame);
        return bundle;
    }

    private void readFromBundle(final Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_SAVE_GAME)) {
                // restore complete game state!!
                currentGame = bundle.getParcelable(Constant.KEY_SAVE_GAME);
            }

//            isMultiplayer = bundle.containsKey(KEY_MULTIPLAYER) && bundle.getBoolean(KEY_MULTIPLAYER) && bundle.containsKey(KEY_OPPONENT_NAME);
//            if (isMultiplayer) {
//                multiPlayerOpponent = bundle.getString(KEY_OPPONENT_NAME);
//                multiPlayerWord = bundle.getString(KEY_WORD_SINGLE);
//                hangedLogic = bundle.getParcelable(Constant.KEY_GAME_LOGIC);
//            } else {
//                isHotSeat = bundle.getBoolean(KEY_MULTIPLAYER);
//                gameState = bundle.getInt(KEY_GAME_STATE);
//                possibleWords.clear();
//                //noinspection ConstantConditions
//                if (bundle.containsKey(KEY_WORD_LIST)) {
//                    //noinspection ConstantConditions
//                    possibleWords.addAll(bundle.getStringArrayList(KEY_WORD_LIST));
//                }
//            }
        }
    }

    private void resetButtons() {
        for (final Button button : listOfButtons) {
            YoYo.with(Techniques.RollIn).duration(250).playOn(button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
        }
        listOfButtons.clear();
    }

    @Override
    public void onClick(final View v) {
        YoYo.with(Techniques.ZoomOutUp).duration(200).withListener(new OnButtonClickAnimatorListener(v)).playOn(v);
        Log.d("buttons", "button clicked");
        listOfButtons.add((Button) v);
        guess(((Button) v).getText().toString());
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for (int i = 0; i < vg.getChildCount(); i++) a.add((Button) vg.getChildAt(i));
        return a;
    }

    private static class OnButtonClickAnimatorListener implements Animator.AnimatorListener {

        private final WeakReference<View> viewWeakReference;

        public OnButtonClickAnimatorListener(final View view) {
            viewWeakReference = new WeakReference<>(view);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final View view = viewWeakReference.get();
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private void updateScreen() {
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        if (!currentGame.getLogic().isLastLetterCorrect()) {
            noose.setImageResource(imageRefs[currentGame.getLogic().getNumWrongLetters()]);
            YoYo.with(Techniques.Landing).duration(100).playOn(noose);
        }
    }

    private void CheckGameType() {
//        if (hangedLogic != null && hangedLogic.isGameOver()) {
//            hangedLogic.reset(4);
//        } else {// for det tilfældes skyld at der er tale om et nyt spil
//            // TODO : Handle multiplayer shit here! :-)
////            possibleWords.addAll((HashSet<String>) s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
//        }
////        Log.d("Play", "CheckGameType : MultiPlayer = " + isMultiplayer);
////        hangedLogic = isMultiplayer ? new Hanged(multiPlayerWord) : new Hanged(possibleWords);
    }

    private void StartEndgame() {// gathers need data for starting up the endgame Fragment
        // TODO : Erstat DU med spillerens navn og HAM med modstanderens navn..
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, EndOfGameFragment.newInstance(currentGame)).commit();
        Log.d("play", "finishing");
    }

    private void guess(final String guess) {
        currentGame.getLogic().guessLetter(guess);
        if (currentGame.getLogic().isGameOver()) {
            StartEndgame();
        } else {
            updateScreen();
        }
    }

}
