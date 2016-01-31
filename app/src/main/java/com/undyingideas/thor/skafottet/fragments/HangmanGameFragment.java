package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.views.camera.Hangman3dView;

import java.util.ArrayList;

//import com.undyingideas.thor.skafottet.support.abstractions.FragmentOnBackClickListener;

/**
 * ??.??.2015, Thor
 * - Created the first draft
 * <p/>
 * ??.??.2015-6, Theis
 * - Modified the button management to be simpler
 * <p/>
 * ??.??.2016, rudz
 * - Re-wrote some logic and added features.
 */
public class HangmanGameFragment extends Fragment {

    private final LinearLayout[] buttonRows = new LinearLayout[4];

    private ArrayList<Button> listOfButtons;
    private SaveGame currentGame;

    private Hangman3dView noose;

    private HTextView textWord;
    private HTextView textStatus;
    private Vibrator vibrator;
    private View sepKnown, sepStatus;

    private Animation sepAnimation;
    private OnButtonClickListener onButtonClickListener;

    private Drawable menuButton;

    private IGameSoundNotifier iGameSoundNotifier;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IGameSoundNotifier && context instanceof IFragmentFlipper) {
            iGameSoundNotifier = (IGameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IGameSoundNotifier.");
        }
    }

    @SuppressWarnings({"AccessStaticViaInstance", "deprecation"})
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (GameUtility.getSettings().prefsHeptic) {
            try {
                vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_hangman_game, container, false);

        noose = (Hangman3dView) root.findViewById(R.id.imageView);

        listOfButtons = new ArrayList<>(28);

        buttonRows[0] = (LinearLayout) root.findViewById(R.id.linearLayout1);
        buttonRows[1] = (LinearLayout) root.findViewById(R.id.linearLayout2);
        buttonRows[2] = (LinearLayout) root.findViewById(R.id.linearLayout3);
        buttonRows[3] = (LinearLayout) root.findViewById(R.id.linearLayout4);

        for (final LinearLayout linearLayout : buttonRows) {
            listOfButtons.addAll(getChildren(linearLayout));
        }

        textWord = (HTextView) root.findViewById(R.id.hangman_game_known_letters);
        textWord.setAnimateType(HTextViewType.SCALE);

        textStatus = (HTextView) root.findViewById(R.id.hangman_game_status);
        textStatus.setAnimateType(HTextViewType.SCALE);

        sepKnown = root.findViewById(R.id.sepKnown);
        sepStatus = root.findViewById(R.id.sepStatus);

        sepAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_repeat_backwards);

        sepKnown.setAnimation(sepAnimation);
        sepStatus.setAnimation(sepAnimation);

        onButtonClickListener = new OnButtonClickListener(this);

        resetButtons();

        applyColours();

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
//        if (currentGame.isMultiPlayer()) {
//            WindowLayout.showSnack(getOppNames(currentGame.getNames()[1], currentGame.getNames()[0]), textWord, true);
//        }
        //Toast.makeText(getContext(), currentGame.getLogic().getTheWord(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onViewStateRestored(final Bundle savedInstanceState) {
        readFromBundle(savedInstanceState);
        applySaveGameStatus();
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        sepKnown.setAnimation(sepAnimation);
        sepStatus.setAnimation(sepAnimation);
        applyColours();
        super.onResume();
    }

    @Override
    public void onPause() {
        sepKnown.setAnimation(null);
        sepStatus.setAnimation(null);
        super.onPause();
    }

    @Override
    public void onDetach() {
        GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, currentGame);
        iGameSoundNotifier = null;
        super.onDetach();
    }

    public static HangmanGameFragment newInstance(final SaveGame saveGame) {
        final Bundle args = new Bundle();
        args.putParcelable(Constant.KEY_SAVE_GAME, saveGame);
        final HangmanGameFragment hangmanGameFragment = newInstance(args); //new HangmanGameFragment();
//        try {
//            GameUtility.mpc.setRunnable(null);
//        } catch (final Exception e) {
//            Log.e("starthangman", "remove runnable " + e.getMessage());
//        }
        return hangmanGameFragment;
    }

    private static HangmanGameFragment newInstance(final Bundle bundle) {
        final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
        hangmanGameFragment.setArguments(bundle);
        return hangmanGameFragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, currentGame);
        outState.putParcelable(Constant.KEY_SAVE_GAME, currentGame);
        super.onSaveInstanceState(outState);
    }

    private void applyColours() {
        /* apply colours to all controls except guess buttons. */
        textWord.setTextColor(GameUtility.getSettings().textColour);
        textStatus.setTextColor(GameUtility.getSettings().textColour);
    }

    @SuppressWarnings("ConstantConditions")
    private void applySaveGameStatus() {
        textWord.animateText(currentGame.getLogic().getVisibleWord());
//        if (currentGame.isMultiPlayer()) {
//            textStatus.animateText(GameUtility.mpc.lc.getOppNames(currentGame.getPlayers()[1].getName(), currentGame.getPlayers()[0].getName()) + " / " + Integer.toString(currentGame.getPlayers()[0].getScore()));
//        } else {
            textStatus.animateText(Integer.toString(currentGame.getPlayers()[0].getScore()));
//        }
        resetButtons();

        // set the button status for the already guessed letters, this is just a quick and dirty hack :-)
        Log.d("Game", String.valueOf(currentGame.getLogic().getNumWrongLetters()));
        if (currentGame.getLogic().getNumWrongLetters() > 0) {
            Log.d("Game", currentGame.getLogic().getUsedLetters().toString());
            for (final Button button : listOfButtons) {
                if (currentGame.getLogic().getUsedLetters().contains(button.getText().toString())) {
                    button.setClickable(false);
                    button.setVisibility(View.INVISIBLE);
                }
            }
        }
        listOfButtons.clear();

        noose.setErrors(currentGame.getLogic().getNumWrongLetters());
        //noose.setImageBitmap(GameUtility.invert(getContext(), currentGame.getLogic().getNumWrongLetters()));
    }

    private void readFromBundle(final Bundle bundle) {
        if (bundle != null && bundle.containsKey(Constant.KEY_SAVE_GAME)) {
            // restore complete game state!!
            currentGame = bundle.getParcelable(Constant.KEY_SAVE_GAME);
        }
    }

    private void resetButtons() {
        YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[0]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[1]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[2]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[3]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(noose);
        for (final Button button : listOfButtons) {
            button.setBackgroundColor(GameUtility.getSettings().prefsColour);
            button.setTextColor(GameUtility.getSettings().textColour);
            YoYo.with(Techniques.FadeIn).duration(400).playOn(button);
            button.setVisibility(View.VISIBLE);
            button.setBackgroundColor(GameUtility.getSettings().prefsColour);
            button.setOnClickListener(onButtonClickListener);
        }
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for (int i = 0; i < vg.getChildCount(); i++) a.add((Button) vg.getChildAt(i));
        return a;
    }

    private void updateScreen() {
        textWord.animateText(currentGame.getLogic().getVisibleWord());
        textStatus.animateText(Integer.toString(currentGame.getPlayers()[0].getScore()));
        if (!currentGame.getLogic().isLastLetterCorrect()) {
            noose.setErrors(currentGame.getLogic().getNumWrongLetters());
            YoYo.with(Techniques.Flash).duration(100).playOn(noose);
        }
    }

    private void startEndgame() {
        // TODO : Erstat DU med spillerens navn og HAM med modstanderens navn..
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, EndOfGameFragment.newInstance(currentGame)).commit();
        Log.d("play", "finishing");
    }

    private void guess(final String guess) {
        currentGame.getLogic().guessLetter(guess);

        if (!currentGame.getLogic().isLastLetterCorrect()) {
            currentGame.getPlayers()[0].addPoints(-100);
            iGameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_WRONG);
            if (vibrator != null) vibrator.vibrate(50);
            YoYo.with(Techniques.Flash).duration(300).playOn(textWord);
        } else {
            currentGame.getPlayers()[0].addPoints(500 * currentGame.getLogic().getNumCorrectLettersLast());
            iGameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_RIGHT);
        }

        if (!currentGame.getLogic().isGameOver()) {
            // save the game status!
            GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, currentGame);
            updateScreen();
        } else {
            GameUtility.writeNullGame();
            if (currentGame.getLogic().isGameWon()) {
                iGameSoundNotifier.playGameSound(GameUtility.SFX_INTRO);
            } else {
                iGameSoundNotifier.playGameSound(GameUtility.SFX_LOST);
            }
            startEndgame();
        }
    }

    private static class OnButtonClickListener extends WeakReferenceHolder<HangmanGameFragment> implements View.OnClickListener {

        public OnButtonClickListener(final HangmanGameFragment hangmanGameFragment) {
            super(hangmanGameFragment);
        }

        @Override
        public void onClick(final View v) {
            final HangmanGameFragment hangmanGameFragment = weakReference.get();
            if (hangmanGameFragment != null) {
                final Button button = (Button) v;
                button.setClickable(false);
                YoYo.with(Techniques.FadeOut).duration(200).withListener(new OnButtonClickAnimatorListener(v)).playOn(v);
                hangmanGameFragment.listOfButtons.add(button);
                hangmanGameFragment.guess(button.getText().toString());
            }
        }
    }

    private static class OnButtonClickAnimatorListener extends WeakReferenceHolder<View> implements Animator.AnimatorListener {

        public OnButtonClickAnimatorListener(final View view) {
            super(view);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final View view = weakReference.get();
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }
}
