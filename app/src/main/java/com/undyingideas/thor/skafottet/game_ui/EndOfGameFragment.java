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
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
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

    private ImageView imageViewResult;
    private TextView textViewTop;
    private ShimmerButton buttonNewGame, buttonMenu;
    private Shimmer shimmerNewGame, shimmerMenu;

    private SaveGame endGame;

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
        textViewTop = (TextView) root.findViewById(R.id.end_game_text_view_top);

        buttonNewGame = (ShimmerButton) root.findViewById(R.id.end_game_button_new_game);
        buttonMenu = (ShimmerButton) root.findViewById(R.id.end_game_button_menu);

        displayResults(getArguments());

        return root;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        shimmerMenu = new Shimmer();
        shimmerNewGame = new Shimmer();

        shimmerMenu.setStartDelay(300);
        shimmerMenu.setDuration(400);
        shimmerNewGame.setDuration(400);

        shimmerMenu.start(buttonMenu);
        shimmerNewGame.start(buttonNewGame);

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
                Log.d(TAG, "going to start Screen");
                //noinspection ConstantConditions
                endOfGameFragment.mListener.onEndGameButtonClicked(false);
            }
        }
    }

}