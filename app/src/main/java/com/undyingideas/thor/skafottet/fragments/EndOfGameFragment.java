/*
 * Copyright 2016 Rudy Alex Kohn
 *
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.support.abstractions.FragmentOnBackClickListener;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.views.AutoScaleTextView;

import java.lang.ref.WeakReference;

/**
 * <p>Created on 17-11-2015, 08:39.
 * Project : skafottet
 * This fragment is responsible for delivering end game result to the player.
 * <p/>
 * <p>
 * ??.01.2016, Theis<br>
 * - Added multiplayer information retrievel.
 * </p>
 * <p>
 * 23.01,2016, rudz<br>
 * - Replaced click interface with IFragmentFlipper<br>
 * - Cleanup<br>
 * - Sorted methods by overall category. (Overrides -> Helper methods -> Helper classs)<br>
 * </p>
 *
 * @author rudz
 */
public class EndOfGameFragment extends Fragment {

    private static final String TAG = "EndGameFragment";

    private ResultCalcHandler resultCalcHandler;

    private static final int MSG_RESULT_COMPLETE = 1;
    private static final String MSG_KEY_UPPER = "ku";
    private static final String MSG_KEY_MIDDLE = "km";
    private static final String MSG_KEY_LOWER = "kl";
    private static final String MSG_KEY_IMG = "ki";

    @NonNull
    private ImageView imageViewResult, buttonNewGame, buttonMenu;

    private TextView textViewTop;
    private TextView textViewMiddle;
    private TextView textViewLower;

    private SaveGame endGame;

    private EndGameClickListener endGameClickListener;

    @Nullable
    private IGameSoundNotifier iGameSoundNotifier;

    @Nullable
    private IFragmentFlipper iFragmentFlipper;

    /**
     * Constructs a new EndOfGameFragment.
     *
     * @param saveGame
     *         The savegame from game which was just finished.
     * @return The fragment
     */
    public static EndOfGameFragment newInstance(final SaveGame saveGame) {
        final EndOfGameFragment endOfGameFragment = new EndOfGameFragment();
        final Bundle args = new Bundle();
        args.putParcelable(Constant.KEY_SAVE_GAME, saveGame);
        endOfGameFragment.setArguments(args);
        return endOfGameFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        resultCalcHandler = new ResultCalcHandler(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.fragment_end_game, container, false);

        imageViewResult = (ImageView) root.findViewById(R.id.end_game_image_view);

        textViewTop = (AutoScaleTextView) root.findViewById(R.id.end_game_text_view_top);
        textViewMiddle = (AutoScaleTextView) root.findViewById(R.id.end_game_middle_status_text_view);
        textViewLower = (AutoScaleTextView) root.findViewById(R.id.end_game_lower_status_text_view);
        textViewTop.setVisibility(View.INVISIBLE);
        textViewMiddle.setVisibility(View.INVISIBLE);
        textViewLower.setVisibility(View.INVISIBLE);

        if (endGameClickListener == null) {
            endGameClickListener = new EndGameClickListener(this);
            Log.d(TAG, "ClickListener initiated");
        }

        buttonNewGame = (ImageView) root.findViewById(R.id.end_game_button_new_game);
        buttonNewGame.setClickable(false);
        buttonNewGame.setOnClickListener(endGameClickListener);
        buttonNewGame.setVisibility(View.INVISIBLE);

        buttonMenu = (ImageView) root.findViewById(R.id.end_game_button_main_menu);
        buttonMenu.setClickable(false);
        buttonMenu.setOnClickListener(endGameClickListener);
        buttonMenu.setVisibility(View.INVISIBLE);

        displayResults(getArguments());

        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        /* workaround for handling back pressed in fragments */
        final View v = getView();
        if (v != null) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new FragmentOnBackClickListener(iFragmentFlipper, Constant.MODE_MENU));
        }
        YoYo.with(Techniques.FadeIn).duration(2000).withListener(new EnterAnimatorHandler(this)).playOn(imageViewResult);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentFlipper && context instanceof IGameSoundNotifier) {
            iFragmentFlipper = (IFragmentFlipper) context;
            iGameSoundNotifier = (IGameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IFragmentFlipper & IGameSoundNotifier.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (endGameClickListener == null) {
            endGameClickListener = new EndGameClickListener(this);
        }
    }

    @Override
    public void onDestroy() {
        imageViewResult = null;
        buttonMenu = null;
        buttonNewGame = null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        iFragmentFlipper = null;
        iGameSoundNotifier = null;
        super.onDetach();
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.KEY_SAVE_GAME)) {
            Log.d(TAG, "SaveGame was found in onViewStateRestored()");
            displayResults(savedInstanceState);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putParcelable(Constant.KEY_SAVE_GAME, endGame);
        super.onSaveInstanceState(outState);
    }

    /* ***************************************************** */
    /* ****************** Helper methods ******************* */
    /* ***************************************************** */

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private class CalculateResults implements Runnable {

        private final static String WORD_WAS = "Ordet var ";

        @Override
        public void run() {
            /* set the correct response depending on the game just played */
            final Bundle results = new Bundle();

            if (!endGame.isMultiPlayer()) {
                if (endGame.getLogic().isGameLost()) {
                    results.putInt(MSG_KEY_IMG, R.drawable.reaper);
                    results.putString(MSG_KEY_UPPER, getString(R.string.game_lost));
                    results.putString(MSG_KEY_MIDDLE, "Dine gæt : " + endGame.getLogic().getUsedLetters());
                    results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord());
                } else {
                    results.putInt(MSG_KEY_IMG, R.drawable.trophy);
                    results.putString(MSG_KEY_UPPER, getString(R.string.game_won));
                    results.putString(MSG_KEY_MIDDLE, "Dine gæt var : " + endGame.getLogic().getUsedLetters() + " og du gættede forkert " + endGame.getLogic().getNumWrongLetters() + " gange. tsktsk.");
                    results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord());
                }
            } else {
                GameUtility.mpc.lc.updateLobby(endGame.getNames()[1], GameUtility.mpc.name, endGame.getLogic().getNumWrongLetters());
                final LobbyDTO dto = GameUtility.mpc.lc.lobbyList.get(endGame.getNames()[1]);
                boolean gameisDone = true;
                for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
                    if (!lps.getName().equals(GameUtility.mpc.name) && lps.getScore() == -1) gameisDone = false;
                }
                if (gameisDone) {
                    for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
                        if (!lps.getName().equals(GameUtility.mpc.name)) {
                            if (lps.getScore() < endGame.getLogic().getNumWrongLetters()) {
                                GameUtility.mpc.pc.updatePlayerScore(lps.getName(), 1);
                            } else {
                                GameUtility.mpc.pc.updatePlayerScore(GameUtility.mpc.name, 1);
                            }
                        }
                    }
                    if (endGame.getLogic().isGameLost()) {
                        results.putInt(MSG_KEY_IMG, R.drawable.reaper);
                        results.putString(MSG_KEY_UPPER, "Du er blever henrettet af");
                        results.putString(MSG_KEY_MIDDLE, getWinner(dto));
                        results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord());
                    } else {
                        results.putInt(MSG_KEY_IMG, R.drawable.trophy);
                        results.putString(MSG_KEY_UPPER, "Du undslap galgen! - Triumf over ");
                        results.putString(MSG_KEY_MIDDLE, getOther(dto, GameUtility.mpc.name));
                        results.putString(MSG_KEY_LOWER, "Du gættede ordet " + endGame.getLogic().getTheWord() + ", derved har du undgået at blive klynget op");
                    }
                } else {
                    if (endGame.getLogic().isGameLost()) {
                        results.putInt(MSG_KEY_IMG, R.drawable.reaper);
                        results.putString(MSG_KEY_UPPER, "Du er blevet hængt");
                        results.putString(MSG_KEY_MIDDLE, "men din modstander kan også nå at blive det");
                        results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord());
                    } else {
                        results.putInt(MSG_KEY_IMG, R.drawable.trophy);
                        results.putString(MSG_KEY_UPPER, "Du undslap galgen!");
                        results.putString(MSG_KEY_MIDDLE, "Din modstander kan dog stadig nå at gøre det bedre.");
                        results.putString(MSG_KEY_LOWER, "Du gættede ordet " + endGame.getLogic().getTheWord() + ", derved har du undgået at blive klynget op");
                    }
                }
            }
            final Message message = resultCalcHandler.obtainMessage(MSG_RESULT_COMPLETE);
            message.setData(results);
            message.sendToTarget();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void displayResults(final Bundle gameData) throws NullPointerException {
        /* Reads the savegame and set up the views according to it's content. */
        endGame = gameData.getParcelable(Constant.KEY_SAVE_GAME);

        // if this is null, we are screwed...
        if (endGame == null) {
            throw new NullPointerException("SaveGame not functional.");
        }
        Log.d(TAG, "SaveGame was loaded OK.");

        final CalculateResults calculateResults = new CalculateResults();
        Log.d(TAG, "Fetching result from savegame");
        resultCalcHandler.post(calculateResults);
    }

    private static class ResultCalcHandler extends Handler {

        final private WeakReference<EndOfGameFragment> endOfGameFragmentWeakReference;

        public ResultCalcHandler(final EndOfGameFragment endOfGameFragment) {
            endOfGameFragmentWeakReference = new WeakReference<>(endOfGameFragment);
        }

        @Override
        public void handleMessage(final Message msg) {
            if (msg != null && msg.what == MSG_RESULT_COMPLETE) {
                Log.d(TAG, "Result calculated, displaying..");
                final Bundle result = msg.getData();
                if (result != null) {
                    final EndOfGameFragment endOfGameFragment = endOfGameFragmentWeakReference.get();
                    if (endOfGameFragment != null) {
                        endOfGameFragment.imageViewResult.setImageResource(result.getInt(MSG_KEY_IMG));
                        endOfGameFragment.textViewTop.setText(result.getString(MSG_KEY_UPPER));
                        endOfGameFragment.textViewMiddle.setText(result.getString(MSG_KEY_MIDDLE));
                        endOfGameFragment.textViewLower.setText(result.getString(MSG_KEY_LOWER));
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

    private static String getOther(final LobbyDTO dto, final String name) {
        String s = "";
        for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
            if (!lps.getName().equals(name)) s += lps.getName() + " , ";
        }
        return s.substring(0, s.length() - 3);
    }

    private static String getWinner(final LobbyDTO dto) {
        for (int i = 0; i < 10; i++) {
            for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
                Log.d("getWinner", lps.toString() + " , score = " + lps.getScore());
                if (lps.getScore() == i) return lps.getName();
            }
        }
        return "Error - no winner";
    }


    /* ***************************************************** */
    /* *************** Static helper classes *************** */
    /* ***************************************************** */

    private static class EndGameClickListener extends WeakReferenceHolder<EndOfGameFragment> implements View.OnClickListener {

        public EndGameClickListener(final EndOfGameFragment endOfGameFragment) {
            super(endOfGameFragment);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(final View v) {
            final EndOfGameFragment endOfGameFragment = weakReference.get();
            if (endOfGameFragment != null) {
                v.setClickable(false);
                if (v == endOfGameFragment.buttonMenu) {
                    endOfGameFragment.buttonNewGame.setClickable(false);
                    YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttonNewGame);
                } else {
                    endOfGameFragment.buttonMenu.setClickable(false);
                    YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttonMenu);
                }
                endOfGameFragment.iGameSoundNotifier.playGameSound(GameUtility.SFX_MENU_CLICK);
                YoYo.with(Techniques.ZoomOut).duration(300).playOn(endOfGameFragment.imageViewResult);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewTop);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewMiddle);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewLower);
                YoYo.with(Techniques.Pulse).duration(400).withListener(new ExitAnimatorHandler(endOfGameFragment, (ImageView) v)).playOn(v);
            }
        }
    }

    private static class EnterAnimatorHandler extends WeakReferenceHolder<EndOfGameFragment> implements Animator.AnimatorListener {

        public EnterAnimatorHandler(final EndOfGameFragment endOfGameFragment) {
            super(endOfGameFragment);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onAnimationEnd(final Animator animation) {
            final EndOfGameFragment endOfGameFragment = weakReference.get();
            if (endOfGameFragment != null) {
                YoYo.with(Techniques.ZoomIn).duration(1000).playOn(endOfGameFragment.textViewTop);
                endOfGameFragment.textViewTop.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInRight).duration(1000).playOn(endOfGameFragment.textViewMiddle);
                endOfGameFragment.textViewMiddle.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInLeft).duration(1000).playOn(endOfGameFragment.textViewLower);
                endOfGameFragment.textViewLower.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInLeft).duration(1000).playOn(endOfGameFragment.buttonMenu);
                endOfGameFragment.buttonMenu.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInRight).duration(1000).playOn(endOfGameFragment.buttonNewGame);
                endOfGameFragment.buttonNewGame.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private static class ExitAnimatorHandler extends WeakReferenceHolder<EndOfGameFragment> implements Animator.AnimatorListener {

        private final ImageView clickedImageView;

        public ExitAnimatorHandler(final EndOfGameFragment endOfGameFragment, final ImageView clickedImageView) {
            super(endOfGameFragment);
            this.clickedImageView = clickedImageView;
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onAnimationEnd(final Animator animation) {
            final EndOfGameFragment endOfGameFragment = weakReference.get();
            if (endOfGameFragment != null) {
                endOfGameFragment.iFragmentFlipper.flipFragment(clickedImageView == endOfGameFragment.buttonNewGame ? Constant.MODE_SINGLE_PLAYER : Constant.MODE_MENU);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }
}