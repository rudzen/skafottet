package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
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
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HangmanGameFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_MULTIPLAYER = "mp";
    private static final String KEY_GAME_STATE = "gs";
    private static final String KEY_WORD_LIST = "posw";
    private static final String KEY_WORD_SINGLE = "sinw";

    private static final String KEY_OPPONENT_NAME = "on";

    private final LinearLayout[] buttonRows = new LinearLayout[4];

    private ArrayList<Button> listOfButtons;
    private SaveGame currentGame;

    private ImageView noose;

    private TextView usedLetters, textViewWord, status;

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

        resetButtons();


        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            readFromBundle(getArguments());
        }

        applySaveGameStatus();

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
        applySaveGameStatus();
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        GameUtility.s_prefereces.putObject(Constant.KEY_SAVE_GAME, currentGame);
        super.onPause();
    }

    private void applySaveGameStatus() {
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        resetButtons();
        noose.setImageResource(GameUtility.imageRefs[currentGame.getLogic().getNumWrongLetters()]);
    }

    private void readFromBundle(final Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_SAVE_GAME)) {
                // restore complete game state!!
                currentGame = bundle.getParcelable(Constant.KEY_SAVE_GAME);
            }
        }
    }

    private void resetButtons() {
        YoYo.with(Techniques.RotateInDownLeft).duration(400).playOn(buttonRows[0]);
        YoYo.with(Techniques.RotateInDownRight).duration(400).playOn(buttonRows[0]);
        YoYo.with(Techniques.RotateInUpLeft).duration(400).playOn(buttonRows[0]);
        YoYo.with(Techniques.RotateInUpRight).duration(400).playOn(buttonRows[0]);

        for (final Button button : listOfButtons) {
            YoYo.with(Techniques.RollIn).duration(400).playOn(button);
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
            noose.setImageResource(GameUtility.imageRefs[currentGame.getLogic().getNumWrongLetters()]);
            YoYo.with(Techniques.Landing).duration(100).playOn(noose);
        }
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
