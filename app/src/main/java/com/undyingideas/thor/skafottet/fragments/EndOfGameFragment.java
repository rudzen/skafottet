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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.ScoreAdapter;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.DrawableHelper;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
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

    @Nullable
    private ImageView imageViewResult;

    private final RelativeLayout[] buttons = new RelativeLayout[3];
    private final AutoScaleTextView[] button_text = new AutoScaleTextView[3];

    private AutoScaleTextView textViewTop;
    private AutoScaleTextView textViewMiddle;
    private AutoScaleTextView textViewLower;

    private SaveGame endGame;

    private EndGameClickListener endGameClickListener;

    @Nullable
    private IGameSoundNotifier iGameSoundNotifier;

    @Nullable
    private IFragmentFlipper iFragmentFlipper;

    private ScoreAdapter scoreAdapter;

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

        buttons[0] = (RelativeLayout) root.findViewById(R.id.end_game_button_new_game);
        buttons[0].setClickable(false);
        buttons[0].setOnClickListener(endGameClickListener);
        buttons[0].setVisibility(View.INVISIBLE);

        buttons[1] = (RelativeLayout) root.findViewById(R.id.end_game_button_main_menu);
        buttons[1].setClickable(false);
        buttons[1].setOnClickListener(endGameClickListener);
        buttons[1].setVisibility(View.INVISIBLE);

        buttons[2] = (RelativeLayout) root.findViewById(R.id.end_game_button_highscore);
        buttons[2].setClickable(false);
        buttons[2].setOnClickListener(endGameClickListener);
        buttons[2].setVisibility(View.INVISIBLE);


        button_text[0] = (AutoScaleTextView) root.findViewById(R.id.end_game_button_new_game_text);
        button_text[1] = (AutoScaleTextView) root.findViewById(R.id.end_game_button_main_menu_text);
        button_text[2] = (AutoScaleTextView) root.findViewById(R.id.end_game_button_highscore_text);

        DrawableHelper.setButtonColors(buttons, button_text);

        displayResults(getArguments());

        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
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
        buttons[0] = null;
        buttons[1] = null;
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

        private final static String WORD_WAS = "Ordet var : ";

        @Override
        public void run() {
            /* set the correct response depending on the game just played */
            final Bundle results = new Bundle();

            if (!endGame.isMultiPlayer()) {
                if (endGame.getLogic().isGameLost()) {
                    results.putInt(MSG_KEY_IMG, R.drawable.reaper);
                    results.putString(MSG_KEY_UPPER, getString(R.string.game_lost));
                    results.putString(MSG_KEY_MIDDLE, "Dine gæt : " + endGame.getLogic().getUsedLetters());
                    results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord() + " / Point : " + endGame.getPlayers()[0].getPtsString());
                } else {
                    results.putInt(MSG_KEY_IMG, R.drawable.trophy);
                    results.putString(MSG_KEY_UPPER, getString(R.string.game_won));
                    if (endGame.getLogic().getNumWrongLetters() == 0) {
                        results.putString(MSG_KEY_MIDDLE, "Dine gæt var : " + endGame.getLogic().getUsedLetters() + ", og du havde ingen fejl!");
                    } else {
                        results.putString(MSG_KEY_MIDDLE, "Dine gæt var : " + endGame.getLogic().getUsedLetters() + " og du gættede forkert " + endGame.getLogic().getNumWrongLetters() + " gange. tsktsk.");
                    }
                    results.putString(MSG_KEY_LOWER, WORD_WAS + endGame.getLogic().getTheWord() + " / Point : " + endGame.getPlayers()[0].getPtsString());
                }
            } else {
                GameUtility.mpc.lc.updateLobby(endGame.getPlayers()[1].getName(), GameUtility.mpc.name, endGame.getLogic().getNumWrongLetters());
                final LobbyDTO dto = GameUtility.mpc.lc.lobbyList.get(endGame.getPlayers()[1].getName());
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

    private void showHighscores() {
        scoreAdapter = new ScoreAdapter(getContext(), R.layout.highscore_list_row, GameUtility.highscoreManager.getScores());
        final ListView listViewItems = new ListView(getActivity().getApplicationContext());
        listViewItems.setAdapter(scoreAdapter);

        WindowLayout.setMd(new MaterialDialog.Builder(getActivity())
                .customView(listViewItems, false)
                .backgroundColor(Color.BLACK)
                .positiveText("Ok")
                .autoDismiss(true)
                .title("De bedste i din verden!"));
        WindowLayout.getMd().show();
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
        final StringBuilder sb = new StringBuilder(16);
        for (final LobbyPlayerStatus lps : dto.getPlayerList()) {
            if (!lps.getName().equals(name)) sb.append(lps.getName()).append(" , ");
        }
        return sb.substring(0, sb.length() - 3);
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
                endOfGameFragment.iGameSoundNotifier.playGameSound(GameUtility.SFX_MENU_CLICK);
                if (v != endOfGameFragment.buttons[2]) { // not the highscore button!
                    v.setClickable(false);
                    if (v == endOfGameFragment.buttons[1]) {
                        endOfGameFragment.buttons[0].setClickable(false);
                        YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttons[0]);
                    } else {
                        endOfGameFragment.buttons[1].setClickable(false);
                        YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttons[1]);
                    }
                    YoYo.with(Techniques.ZoomOut).duration(300).playOn(endOfGameFragment.imageViewResult);
                    YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewTop);
                    YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewMiddle);
                    YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewLower);
                    YoYo.with(Techniques.Pulse).duration(400).withListener(new ExitAnimatorHandler(endOfGameFragment, (RelativeLayout) v)).playOn(v);
                } else {
                    endOfGameFragment.showHighscores();
                }
            }
        }
    }

    private static class EnterAnimatorHandler extends WeakReferenceHolder<EndOfGameFragment> implements Animator.AnimatorListener,  AdapterView.OnItemClickListener {

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
                YoYo.with(Techniques.BounceInRight).duration(1000).playOn(endOfGameFragment.buttons[1]);
                endOfGameFragment.buttons[1].setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInLeft).duration(1000).playOn(endOfGameFragment.buttons[0]);
                endOfGameFragment.buttons[0].setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceIn).duration(1000).playOn(endOfGameFragment.buttons[2]);
                endOfGameFragment.buttons[2].setVisibility(View.VISIBLE);

                if (!endOfGameFragment.endGame.isMultiPlayer()) {
                    if (GameUtility.highscoreManager.checkScore(endOfGameFragment.endGame.getPlayers()[0].getPts()) > -1) {
                        final int highscorePosition = GameUtility.highscoreManager.addScore(endOfGameFragment.endGame.getLogic().getTheWord(), endOfGameFragment.endGame.getPlayers()[0]);
                        Log.d("Highscore", "Tilføjet til position # : " + highscorePosition);
                        GameUtility.highscoreManager.saveHighScore();
                        endOfGameFragment.textViewLower.setText(endOfGameFragment.textViewLower.getText().toString() + "\nDu har indtaget position nummer " + Integer.toString(highscorePosition + 1));
                    } else {
                        Log.d("Highscore", "Spiller " + endOfGameFragment.endGame.getPlayers()[0].getName() + " sux.");
                    }
                    Log.d("Highscore", GameUtility.highscoreManager.getHighscoreString());
                    endOfGameFragment.showHighscores();
                }


            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    private static class ExitAnimatorHandler extends WeakReferenceHolder<EndOfGameFragment> implements Animator.AnimatorListener {

        private final RelativeLayout clickedView;

        public ExitAnimatorHandler(final EndOfGameFragment endOfGameFragment, final RelativeLayout clickedView) {
            super(endOfGameFragment);
            this.clickedView = clickedView;
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onAnimationEnd(final Animator animation) {
            final EndOfGameFragment endOfGameFragment = weakReference.get();
            if (endOfGameFragment != null) {
                endOfGameFragment.iFragmentFlipper.flipFragment(clickedView == endOfGameFragment.buttons[0] ? Constant.MODE_SINGLE_PLAYER : Constant.MODE_MENU);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }
}