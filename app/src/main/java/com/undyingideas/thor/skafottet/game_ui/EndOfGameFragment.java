package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.utility.Constant;

import java.lang.ref.WeakReference;

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

    @Nullable
    private OnEndGameButtonClickListenerInterface mListener;

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

        buttonNewGame.setOnClickListener(endGameClickListener);
        buttonMenu.setOnClickListener(endGameClickListener);

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
        endGame = gameData.getParcelable(Constant.KEY_SAVE_GAME);

        // if this is null, we are screwed...
        if (endGame == null) {
            throw new NullPointerException("SaveGame not functional.");
        }

        Log.d(TAG, "SaveGame was loaded OK.");

        if (endGame.getLogic().isGameLost()) {
            imageViewResult.setImageResource(R.drawable.reaper);
            textViewTop.setText("Du er død!");
        } else {
            imageViewResult.setImageResource(R.drawable.trophy);
            textViewTop.setText("Du undslap galgen!");
        }
        YoYo.with(Techniques.ZoomInDown).duration(700).playOn(imageViewResult);
        YoYo.with(Techniques.ZoomInDown).duration(700).playOn(textViewTop);
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


}