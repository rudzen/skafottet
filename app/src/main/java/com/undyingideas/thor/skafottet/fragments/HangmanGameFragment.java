/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private final LinearLayout[] mButtonRows = new LinearLayout[4];

    private ArrayList<Button> mListOfButtons;
    private SaveGame mCurrentGame;

    private Hangman3dView mNoose;

    private HTextView mTextWord;
    private HTextView mTextStatus;
    private Vibrator mVibrator;
    private View mSeperatorKnown, mSeperatorStatus;

    private Animation mSeperatorAnimation;
    private OnButtonClickListener mOnButtonClickListener;

    private Drawable mMenuButton;

    private IGameSoundNotifier mGameSoundNotifier;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IGameSoundNotifier && context instanceof IFragmentFlipper) {
            mGameSoundNotifier = (IGameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IGameSoundNotifier.");
        }
    }

    @SuppressWarnings({"AccessStaticViaInstance", "deprecation"})
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (GameUtility.getSettings().prefsHeptic) {
            try {
                mVibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_hangman_game, container, false);

        mNoose = (Hangman3dView) root.findViewById(R.id.imageView);

        mListOfButtons = new ArrayList<>(28);

        mButtonRows[0] = (LinearLayout) root.findViewById(R.id.linearLayout1);
        mButtonRows[1] = (LinearLayout) root.findViewById(R.id.linearLayout2);
        mButtonRows[2] = (LinearLayout) root.findViewById(R.id.linearLayout3);
        mButtonRows[3] = (LinearLayout) root.findViewById(R.id.linearLayout4);

        for (final LinearLayout linearLayout : mButtonRows) {
            mListOfButtons.addAll(getChildren(linearLayout));
        }

        mTextWord = (HTextView) root.findViewById(R.id.hangman_game_known_letters);
        mTextWord.setAnimateType(HTextViewType.SCALE);

        mTextStatus = (HTextView) root.findViewById(R.id.hangman_game_status);
        mTextStatus.setAnimateType(HTextViewType.SCALE);

        mSeperatorKnown = root.findViewById(R.id.sepKnown);
        mSeperatorStatus = root.findViewById(R.id.sepStatus);

        mSeperatorAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_repeat_backwards);

        mSeperatorKnown.setAnimation(mSeperatorAnimation);
        mSeperatorStatus.setAnimation(mSeperatorAnimation);

        mOnButtonClickListener = new OnButtonClickListener(this);

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
//        if (mCurrentGame.isMultiPlayer()) {
//            WindowLayout.showSnack(getOppNames(mCurrentGame.getNames()[1], mCurrentGame.getNames()[0]), mTextWord, true);
//        }
        //Toast.makeText(getContext(), mCurrentGame.getLogic().getTheWord(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onViewStateRestored(final Bundle savedInstanceState) {
        readFromBundle(savedInstanceState);
        applySaveGameStatus();
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        mSeperatorKnown.setAnimation(mSeperatorAnimation);
        mSeperatorStatus.setAnimation(mSeperatorAnimation);
        applyColours();
        super.onResume();
    }

    @Override
    public void onPause() {
        mSeperatorKnown.setAnimation(null);
        mSeperatorStatus.setAnimation(null);
        super.onPause();
    }

    @Override
    public void onDetach() {
        GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, mCurrentGame);
        mGameSoundNotifier = null;
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
        GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, mCurrentGame);
        outState.putParcelable(Constant.KEY_SAVE_GAME, mCurrentGame);
        super.onSaveInstanceState(outState);
    }

    private void applyColours() {
        /* apply colours to all controls except guess buttons. */
        mTextWord.setTextColor(GameUtility.getSettings().textColour);
        mTextStatus.setTextColor(GameUtility.getSettings().textColour);
    }

    @SuppressWarnings("ConstantConditions")
    private void applySaveGameStatus() {
        mTextWord.animateText(mCurrentGame.getLogic().getVisibleWord());
//        if (mCurrentGame.isMultiPlayer()) {
//            mTextStatus.animateText(GameUtility.mpc.lc.getOppNames(mCurrentGame.getPlayers()[1].getmName(), mCurrentGame.getPlayers()[0].getmName()) + " / " + Integer.toString(mCurrentGame.getPlayers()[0].getmScore()));
//        } else {
            mTextStatus.animateText(Integer.toString(mCurrentGame.getPlayers()[0].getScore()));
//        }
        resetButtons();

        // set the button status for the already guessed letters, this is just a quick and dirty hack :-)
        Log.d("Game", String.valueOf(mCurrentGame.getLogic().getNumWrongLetters()));
        if (mCurrentGame.getLogic().getNumWrongLetters() > 0) {
            Log.d("Game", mCurrentGame.getLogic().getUsedLetters().toString());
            for (final Button button : mListOfButtons) {
                if (mCurrentGame.getLogic().getUsedLetters().contains(button.getText().toString())) {
                    button.setClickable(false);
                    button.setVisibility(View.INVISIBLE);
                }
            }
        }
        mListOfButtons.clear();

        mNoose.setErrors(mCurrentGame.getLogic().getNumWrongLetters());
        //mNoose.setImageBitmap(GameUtility.invert(getContext(), mCurrentGame.getLogic().getNumWrongLetters()));
    }

    private void readFromBundle(final Bundle bundle) {
        if (bundle != null && bundle.containsKey(Constant.KEY_SAVE_GAME)) {
            // restore complete game state!!
            mCurrentGame = bundle.getParcelable(Constant.KEY_SAVE_GAME);
        }
    }

    private void resetButtons() {
        YoYo.with(Techniques.FadeIn).duration(400).playOn(mButtonRows[0]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(mButtonRows[1]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(mButtonRows[2]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(mButtonRows[3]);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(mNoose);
        for (final Button button : mListOfButtons) {
            button.setBackgroundColor(GameUtility.getSettings().prefsColour);
            button.setTextColor(GameUtility.getSettings().textColour);
            YoYo.with(Techniques.FadeIn).duration(400).playOn(button);
            button.setVisibility(View.VISIBLE);
            button.setBackgroundColor(GameUtility.getSettings().prefsColour);
            button.setOnClickListener(mOnButtonClickListener);
        }
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for (int i = 0; i < vg.getChildCount(); i++) a.add((Button) vg.getChildAt(i));
        return a;
    }

    private void updateScreen() {
        mTextWord.animateText(mCurrentGame.getLogic().getVisibleWord());
        mTextStatus.animateText(Integer.toString(mCurrentGame.getPlayers()[0].getScore()));
        if (!mCurrentGame.getLogic().isLastLetterCorrect()) {
            mNoose.setErrors(mCurrentGame.getLogic().getNumWrongLetters());
            YoYo.with(Techniques.Flash).duration(100).playOn(mNoose);
        }
    }

    private void startEndgame() {
        // TODO : Erstat DU med spillerens navn og HAM med modstanderens navn..
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, EndOfGameFragment.newInstance(mCurrentGame)).commit();
        Log.d("play", "finishing");
    }

    private void guess(final String guess) {
        mCurrentGame.getLogic().guessLetter(guess);

        if (!mCurrentGame.getLogic().isLastLetterCorrect()) {
            mCurrentGame.getPlayers()[0].addPoints(-100);
            mGameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_WRONG);
            if (mVibrator != null) mVibrator.vibrate(50);
            YoYo.with(Techniques.Flash).duration(300).playOn(mTextWord);
        } else {
            mCurrentGame.getPlayers()[0].addPoints(500 * mCurrentGame.getLogic().getNumCorrectLettersLast());
            mGameSoundNotifier.playGameSound(GameUtility.SFX_GUESS_RIGHT);
        }

        if (!mCurrentGame.getLogic().isGameOver()) {
            // save the game status!
            GameUtility.getPrefs().putObject(Constant.KEY_SAVE_GAME, mCurrentGame);
            updateScreen();
        } else {
            GameUtility.writeNullGame();
            if (mCurrentGame.getLogic().isGameWon()) {
                mGameSoundNotifier.playGameSound(GameUtility.SFX_INTRO);
            } else {
                mGameSoundNotifier.playGameSound(GameUtility.SFX_LOST);
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
            final HangmanGameFragment hangmanGameFragment = mWeakReference.get();
            if (hangmanGameFragment != null) {
                final Button button = (Button) v;
                button.setClickable(false);
                YoYo.with(Techniques.FadeOut).duration(200).withListener(new OnButtonClickAnimatorListener(v)).playOn(v);
                hangmanGameFragment.mListOfButtons.add(button);
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
            final View view = mWeakReference.get();
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
