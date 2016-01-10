package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 *
 * @author Thor
 */
public class EndOfGameFragment extends Fragment {

    private static final String TAG = "EndGameFragment";

    private ImageView imageViewResult, buttonNewGame, buttonMenu;
    private ShimmerTextView textViewTop;

    private SaveGame endGame;

    private Shimmer shimmerTop;
    private static EndGameClickListener endGameClickListener;

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
        }

        buttonNewGame.setOnClickListener(endGameClickListener);
        buttonMenu.setOnClickListener(endGameClickListener);

        displayResults(getArguments());

        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        shimmerTop = new Shimmer();
        shimmerTop.setDuration(800);
        shimmerTop.setRepeatCount(0);
//        shimmerTop.setStartDelay(500);
        shimmerTop.start(textViewTop);
    }

    private void displayResults(final Bundle gameData) throws IllegalArgumentException {
        endGame = gameData.getParcelable(Constant.KEY_SAVE_GAME);

        // if this is null, we are screwed...
        if (endGame == null) {
            throw new IllegalArgumentException("SaveGame not functional.");
        }

        if (endGame.getLogic().isGameOver()) {
            imageViewResult.setImageResource(R.drawable.reaper);
            textViewTop.setText("Du er død!");
        } else {
            imageViewResult.setImageResource(R.drawable.trophy);
            textViewTop.setText("Du undslap galgen!");
        }
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