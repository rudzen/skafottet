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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.GameActivity;
import com.undyingideas.thor.skafottet.activities.support.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.GameSoundNotifier;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.views.AutoScaleTextView;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 * This fragment is responsible for delivering end game result to the player.
 * <p/>
 * ??.01.2016, Theis
 * - Added multiplayer information retrievel.
 *
 * @author rudz
 */
public class EndOfGameFragment extends Fragment {

    private static final String TAG = "EndGameFragment";

    @Nullable
    private ImageView imageViewResult, buttonNewGame, buttonMenu;

    private TextView textViewTop;
    private TextView textViewMiddle;
    private TextView textViewLower;

    private SaveGame endGame;

    private EndGameClickListener endGameClickListener;

    @Nullable
    private OnEndGameButtonClickListenerInterface onEndGameButtonClickListenerInterface;

    private GameSoundNotifier gameSoundNotifier;

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

    /**
     * As per Google's recommendation, an interface to communicate back to activity.
     * Instructing it what to do, mainly because the fragments should not be allowed to replace themselves.
     * There is little chance of potential strong references if it's done with this setup.
     */
    public interface OnEndGameButtonClickListenerInterface {
        void onEndGameButtonClicked(final boolean newGame);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnEndGameButtonClickListenerInterface) {
            onEndGameButtonClickListenerInterface = (OnEndGameButtonClickListenerInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEndGameButtonClickListenerInterface");
        }
        if (context instanceof GameSoundNotifier) {
            gameSoundNotifier = (GameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GameSoundNotifier");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.fragment_end_game, container, false);

        imageViewResult = (ImageView) root.findViewById(R.id.end_game_image_view);

        buttonNewGame = (ImageView) root.findViewById(R.id.end_game_button_new_game);
        buttonMenu = (ImageView) root.findViewById(R.id.end_game_button_main_menu);

        if (endGameClickListener == null) {
            endGameClickListener = new EndGameClickListener(this);
            Log.d(TAG, "ClickListener initiated");
        }

        if (buttonNewGame != null) {
            buttonNewGame.setOnClickListener(endGameClickListener);
        }
        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(endGameClickListener);
        }

        textViewTop = (AutoScaleTextView) root.findViewById(R.id.end_game_text_view_top);
        textViewMiddle = (AutoScaleTextView) root.findViewById(R.id.end_game_middle_status_text_view);
        textViewLower = (AutoScaleTextView) root.findViewById(R.id.end_game_lower_status_text_view);

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
            v.setOnKeyListener(new OnBackKeyListener());
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

        final CharSequence lowerText;
        CharSequence middleText = "";

        // WARNING .. NEEDS TO BE CLEANED UP!

        /* set the correct response depending on the game just played */
        if (!endGame.isMultiPlayer()) {
            if (endGame.getLogic().isGameLost()) {
                imageViewResult.setImageResource(R.drawable.reaper);
                textViewTop.setText(R.string.game_lost);
                lowerText = "Ordet var " + endGame.getLogic().getTheWord();
                middleText = "Dine gæt : " + endGame.getLogic().getUsedLetters();
            } else {
                imageViewResult.setImageResource(R.drawable.trophy);
                textViewTop.setText(R.string.game_won);
                middleText = "Dine gæt var : " + endGame.getLogic().getUsedLetters() + " og du gættede forkert " + endGame.getLogic().getNumWrongLetters() + " gange. tsktsk.";
                lowerText = "Ordet var " + endGame.getLogic().getTheWord();
            }
        } else {
            GameUtility.mpc.lc.updateLobby(endGame.getNames()[1], GameUtility.mpc.name, endGame.getLogic().getNumWrongLetters());
            final LobbyDTO dto = GameUtility.mpc.lc.lobbyList.get(endGame.getNames()[1]);
            boolean gameisDone = true;
            for (final LobbyPlayerStatus lps : dto.getPlayerList())
                if (!lps.getName().equals(GameUtility.mpc.name) && lps.getScore() == -1)
                    gameisDone = false;
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
                    imageViewResult.setImageResource(R.drawable.reaper);
                    textViewTop.setText("Du er blever henrettet af");
                    middleText = getWinner(dto);
                    lowerText = "Ordet var " + endGame.getLogic().getTheWord();
                } else {
                    imageViewResult.setImageResource(R.drawable.trophy);
                    textViewTop.setText("Du undslap galgen! - Triumf over ");
                    middleText = getOther(dto, GameUtility.mpc.name);
                    lowerText = "Du gættede ordet " + endGame.getLogic().getTheWord() + ", derved har du undgået at blive klynget op";
                }
            } else {
                if (endGame.getLogic().isGameLost()) {
                    imageViewResult.setImageResource(R.drawable.reaper);
                    textViewTop.setText("Du er blevet hængt");
                    middleText = "men din modstander kan også nå at blive det";
                    lowerText = "Ordet var " + endGame.getLogic().getTheWord();
                } else {
                    imageViewResult.setImageResource(R.drawable.trophy);
                    textViewTop.setText("Du undslap galgen!");
                    middleText = "Din modstander kan dog stadig nå at gøre det bedre.";
                    lowerText = "Du gættede ordet " + endGame.getLogic().getTheWord() + ", derved har du undgået at blive klynget op";
                }
            }
        }
        textViewMiddle.setText(middleText);
        textViewLower.setText(lowerText);
        YoYo.with(Techniques.ZoomInDown).duration(700).playOn(imageViewResult);
        YoYo.with(Techniques.ZoomInUp).duration(700).playOn(textViewTop);
        YoYo.with(Techniques.SlideInLeft).duration(700).playOn(textViewLower);
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

    private static String getOther(final LobbyDTO dto, final String name) {
        String s = "";
        for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
            if (!lps.getName().equals(name)) s += lps.getName() + " , ";
        }
        return s.substring(0, s.length() - 3);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        ((GameActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
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
        super.onDestroy();
        // something could be done here, but what? :-)
    }

    @Override
    public void onDetach() {
        imageViewResult = null;
        buttonMenu = null;
        buttonNewGame = null;
        Log.d(TAG, "Image objects destroyed.");
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
                endOfGameFragment.gameSoundNotifier.playGameSound(GameUtility.SFX_MENU_CLICK);
                YoYo.with(Techniques.ZoomOut).duration(300).playOn(endOfGameFragment.imageViewResult);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewTop);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewMiddle);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewLower);
                YoYo.with(Techniques.Pulse).duration(400).withListener(new ExitAnimatorHandler(endOfGameFragment, (ImageView) v)).playOn(v);
            }
        }
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
                endOfGameFragment.onEndGameButtonClickListenerInterface.onEndGameButtonClicked(clickedImageView == endOfGameFragment.buttonNewGame);
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