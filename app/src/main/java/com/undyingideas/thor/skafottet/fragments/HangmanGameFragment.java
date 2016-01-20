package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.GameActivity;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.GameSoundNotifier;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.camera.Hangman3dView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

    private ShimmerTextView textViewWord, textViewStatus;

    private Vibrator vibrator;
    private Shimmer shimmerWord, shimmerStatus;
    private View sepKnown, sepStatus;

    private Animation sepAnimation, sepAnimation2;
    private OnButtonClickListener onButtonClickListener;

    private GameSoundNotifier gameSoundNotifier;

    public static HangmanGameFragment newInstance(final SaveGame saveGame) {
        final HangmanGameFragment hangmanGameFragment = new HangmanGameFragment();
        final Bundle args = new Bundle();
        args.putParcelable(Constant.KEY_SAVE_GAME, saveGame);
        hangmanGameFragment.setArguments(args);
        try {
            GameUtility.mpc.setRunnable(null);
        } catch (Exception e) {
            Log.e("starthangman", "remove runnable " + e.getMessage());
        }
        return hangmanGameFragment;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        ((GameActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
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

        textViewWord = (ShimmerTextView) root.findViewById(R.id.hangman_game_known_letters);
        textViewStatus = (ShimmerTextView) root.findViewById(R.id.hangman_game_status);

        sepKnown = root.findViewById(R.id.sepKnown);
        sepStatus = root.findViewById(R.id.sepStatus);

        sepAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_repeat_backwards);
        sepAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_hangman_game_seperator);

        sepKnown.setAnimation(sepAnimation);
        sepStatus.setAnimation(sepAnimation2);

        onButtonClickListener = new OnButtonClickListener(this);

        resetButtons();

        return root;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof GameSoundNotifier) {
            gameSoundNotifier = (GameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GameSoundNotifier");
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            readFromBundle(getArguments());
        }
        final View v = getView();
        if (v != null) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new OnBackKeyListener());
        }

        applySaveGameStatus();

        // testing :
        if (currentGame.isMultiPlayer()) {
            WindowLayout.showSnack(getOppNames(currentGame.getNames()[1], currentGame.getNames()[0]), textViewWord, true);
        }
        //Toast.makeText(getContext(), currentGame.getLogic().getTheWord(), Toast.LENGTH_SHORT).show();

        shimmerWord = new Shimmer();
        shimmerWord.setRepeatCount(1);
        shimmerWord.start(textViewWord);
        shimmerStatus = new Shimmer();
        shimmerStatus.setDirection(Shimmer.ANIMATION_DIRECTION_RTL);
        shimmerStatus.setDuration(3000);
        shimmerStatus.setStartDelay(300);
        shimmerStatus.start(textViewStatus);
    }

    @Override
    public void onDetach() {
        GameUtility.s_preferences.putObject(Constant.KEY_SAVE_GAME, currentGame);
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        GameUtility.s_preferences.putObject(Constant.KEY_SAVE_GAME, currentGame);
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
        shimmerWord.cancel();
        shimmerStatus.cancel();
        sepKnown.setAnimation(null);
        sepStatus.setAnimation(null);
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (shimmerWord == null) {
            shimmerWord = new Shimmer();
            shimmerWord.setDuration(400);
        }
        if (shimmerStatus == null) {
            shimmerStatus = new Shimmer();
            shimmerStatus.setDuration(800);
        }
        shimmerWord.start(textViewWord);
        shimmerStatus.start(textViewStatus);
        sepKnown.setAnimation(sepAnimation);
        sepStatus.setAnimation(sepAnimation2);
    }

    private static String getOppNames(final String lobbykey, final String yourname) {
        try {
            final LobbyDTO dto = GameUtility.mpc.lc.lobbyList.get(lobbykey);
            String s = "Modstander: ";
            for (final LobbyPlayerStatus lps : dto.getPlayerList()) if (!lps.getName().equals(yourname)) s += lps.getName() + " , ";
            return s.length() > 3 ? s.substring(0, s.length() - 3) : s;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void applySaveGameStatus() {
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        if (currentGame.isMultiPlayer()) {
            textViewStatus.setText(getOppNames(currentGame.getNames()[1], currentGame.getNames()[0]));
        } else {
            textViewStatus.setText("Du kæmper for føden");
        }
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
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_SAVE_GAME)) {
                // restore complete game state!!
                currentGame = bundle.getParcelable(Constant.KEY_SAVE_GAME);
            }
        }
    }

    private void resetButtons() {
        new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[0]);
                YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[1]);
                YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[2]);
                YoYo.with(Techniques.FadeIn).duration(400).playOn(buttonRows[3]);
                YoYo.with(Techniques.FadeIn).duration(400).playOn(noose);
                for (final Button button : listOfButtons) {
                    YoYo.with(Techniques.FadeIn).duration(400).playOn(button);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(onButtonClickListener);
                }
            }
        }.run();
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for (int i = 0; i < vg.getChildCount(); i++) a.add((Button) vg.getChildAt(i));
        return a;
    }

    private void updateScreen() {
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        if (!currentGame.getLogic().isLastLetterCorrect()) {
            noose.setErrors(currentGame.getLogic().getNumWrongLetters());
            YoYo.with(Techniques.Landing).duration(100).playOn(noose);
        }
    }

    private void startEndgame() {
        // TODO : Erstat DU med spillerens navn og HAM med modstanderens navn..
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, EndOfGameFragment.newInstance(currentGame)).commit();
        Log.d("play", "finishing");
    }

    private void guess(final String guess) {
        currentGame.getLogic().guessLetter(guess);
        shimmerWord.start(textViewWord);
        textViewWord.setText(currentGame.getLogic().getVisibleWord());
        if (currentGame.getLogic().isGameOver()) {
            GameUtility.writeNullGame();
            if (currentGame.getLogic().isGameWon()) {
                gameSoundNotifier.playGameSound(GameUtility.SFX_INTRO);
            } else {
                gameSoundNotifier.playGameSound(GameUtility.SFX_LOST);
            }
            startEndgame();
        } else {
            // save the game status!
            GameUtility.s_preferences.putObject(Constant.KEY_SAVE_GAME, currentGame);
            if (!currentGame.getLogic().isLastLetterCorrect()) {
                gameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_WRONG);
                if (vibrator != null) vibrator.vibrate(50);
                YoYo.with(Techniques.Flash).duration(300).playOn(textViewWord);
            } else {
                gameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_RIGHT);
            }
            updateScreen();
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {

        private final WeakReference<HangmanGameFragment> hangmanGameFragmentWeakReference;

        public OnButtonClickListener(final HangmanGameFragment hangmanGameFragment) {
            hangmanGameFragmentWeakReference = new WeakReference<>(hangmanGameFragment);
        }

        @Override
        public void onClick(final View v) {
            final HangmanGameFragment hangmanGameFragment = hangmanGameFragmentWeakReference.get();
            if (hangmanGameFragment != null) {
                final Button button = (Button) v;
                button.setClickable(false);
                YoYo.with(Techniques.FadeOut).duration(200).withListener(new OnButtonClickAnimatorListener(v)).playOn(v);
                hangmanGameFragment.listOfButtons.add(button);
                hangmanGameFragment.guess(button.getText().toString());
            }
        }
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

    private class OnBackKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                getActivity().finish();
                return true;
            }
            return false;
        }
    }
}
