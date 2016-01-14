package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.lang.ref.WeakReference;

import asia.ivity.android.marqueeview.MarqueeView;

// TODO : Add information about multiplayer game and what word that was played.

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 * This fragment is responsible for delivering end game result to the player.
 *
 * @author rudz
 */
public class EndOfGameFragment extends Fragment {

    private static final String TAG = "EndGameFragment";

    @Nullable
    private ImageView imageViewResult, buttonNewGame, buttonMenu;
    private ShimmerTextView textViewTop;

    private SaveGame endGame;

    @Nullable
    private Shimmer shimmerTop;
    private EndGameClickListener endGameClickListener;

    private MarqueeView marqueeViewLower;
    private TextView textViewLower;

    private Handler handler;
    private Runnable startMarquee;

    @Nullable
    private OnEndGameButtonClickListenerInterface mListener;

    /**
     * Constructs a new EndOfGameFragment.
     * @param saveGame The savegame from game which was just finished.
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
            mListener = (OnEndGameButtonClickListenerInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEndGameButtonClickListenerInterface");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.fragment_end_game, container, false);

        imageViewResult = (ImageView) root.findViewById(R.id.end_game_image_view);
        textViewTop = (ShimmerTextView) root.findViewById(R.id.end_game_text_view_top);

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

        marqueeViewLower = (MarqueeView) root.findViewById(R.id.end_game_lower_status_marquee_view);
        textViewLower = (TextView) root.findViewById(R.id.end_game_lower_status_text_view);

        marqueeViewLower.setPauseBetweenAnimations(500);
        marqueeViewLower.setSpeed(10);

        startMarquee = new StartMarquee();
        handler = new Handler();


        displayResults(getArguments());

        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        if (shimmerTop == null) {
            setShimmer();
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

        /* write an invalid save game to prefs */
        GameUtility.s_prefereces.putObject(Constant.KEY_SAVE_GAME, new SaveGame(null, false, null));



        final CharSequence lowerText;


        // WARNING .. NEEDS TO BE CLEANED UP!

        /* set the correct response depending on the game just played */
        if (!endGame.isMultiPlayer()) {
            if (endGame.getLogic().isGameLost()) {
                imageViewResult.setImageResource(R.drawable.reaper);
                lowerText = "Ordet var" + endGame.getLogic().getTheWord() + ". Dine gæt var : " + endGame.getLogic().getUsedLetters();
                textViewTop.setText(R.string.game_lost);
            } else {
                imageViewResult.setImageResource(R.drawable.trophy);
                textViewTop.setText(R.string.game_won);
                lowerText = "Ordet var" + endGame.getLogic().getTheWord() + ". Dine gæt var : " + endGame.getLogic().getUsedLetters() + " og du gættede forkert " + endGame.getLogic().getNumWrongLetters() + " gange. tsktsk.";
            }
        } else {
            GameUtility.mpc.lc.updateLobby(endGame.getNames()[1], GameUtility.mpc.name, endGame.getLogic().getNumWrongLetters());
            if (endGame.getLogic().isGameLost()) {
                imageViewResult.setImageResource(R.drawable.reaper);
                textViewTop.setText("Du er blever henrettet af " + endGame.getNames()[1]);
                lowerText = "Ordet du tabte udfordringen fra " + endGame.getNames()[1] + " var " + endGame.getLogic().getTheWord();
                for(final String s: endGame.getNames()) Log.d("Endgame", s);
            } else {
                imageViewResult.setImageResource(R.drawable.trophy);
                textViewTop.setText("Du undslap galgen! - Triumf over " + endGame.getNames()[1]);
                lowerText = "Du gættede ordet " + endGame.getLogic().getTheWord() + ", derved har du undgået at blive klynget op af " + endGame.getNames()[1];
            }
        }
        textViewLower.setText(lowerText);
        handler.postDelayed(startMarquee, 500);
        YoYo.with(Techniques.ZoomInDown).duration(700).playOn(imageViewResult);
        YoYo.with(Techniques.ZoomInUp).duration(700).playOn(textViewTop);
        YoYo.with(Techniques.SlideInLeft).duration(700).playOn(textViewLower);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shimmerTop == null) {
            setShimmer();
        }
        if (endGameClickListener == null) {
            endGameClickListener = new EndGameClickListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (shimmerTop != null) {
            shimmerTop.cancel();
            shimmerTop = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageViewResult = null;
        buttonMenu = null;
        buttonNewGame = null;
        Log.d(TAG, "Image objects destroyed.");
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

    private void setShimmer() {
        shimmerTop = new Shimmer();
        shimmerTop.setDuration(800);
        shimmerTop.setRepeatCount(0);
        shimmerTop.start(textViewTop);
        Log.d(TAG, "Shimmer Configured");
    }

    private static class EndGameClickListener implements View.OnClickListener {

        private final WeakReference<EndOfGameFragment> endOfGameFragmentWeakReference;

        public EndGameClickListener(final EndOfGameFragment endOfGameFragment) {
            endOfGameFragmentWeakReference = new WeakReference<>(endOfGameFragment);
        }

        @Override
        public void onClick(final View v) {
            final EndOfGameFragment endOfGameFragment = endOfGameFragmentWeakReference.get();
            if (endOfGameFragment != null) {
                YoYo.with(Techniques.ZoomOut).duration(300).playOn(endOfGameFragment.imageViewResult);
                YoYo.with(Techniques.ZoomOut).duration(400).playOn(endOfGameFragment.textViewTop);
                if (v == endOfGameFragment.buttonMenu) {
                    YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttonNewGame);
                } else {
                    YoYo.with(Techniques.FadeOut).duration(300).playOn(endOfGameFragment.buttonMenu);
                }
                YoYo.with(Techniques.RotateOut).duration(400).withListener(new ExitAnimatorHandler(endOfGameFragment, (ImageView) v)).playOn(v);
            }
        }
    }

    private static class ExitAnimatorHandler implements Animator.AnimatorListener {

        private final WeakReference<EndOfGameFragment> endOfGameFragmentWeakReference;
        private final ImageView clickedImageView;

        public ExitAnimatorHandler(final EndOfGameFragment endOfGameFragment, final ImageView clickedImageView) {
            endOfGameFragmentWeakReference = new WeakReference<>(endOfGameFragment);
            this.clickedImageView = clickedImageView;
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onAnimationEnd(final Animator animation) {
            final EndOfGameFragment endOfGameFragment = endOfGameFragmentWeakReference.get();
            if (endOfGameFragment != null) {
                endOfGameFragment.mListener.onEndGameButtonClicked(clickedImageView == endOfGameFragment.buttonNewGame);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private class StartMarquee implements Runnable {
        @Override
        public void run() {
            marqueeViewLower.startMarquee();
        }
    }

}