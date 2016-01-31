package com.undyingideas.thor.skafottet.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.PlayerDetailActivity;
import com.undyingideas.thor.skafottet.activities.PlayerListActivity;
import com.undyingideas.thor.skafottet.support.firebase.Utils;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;

/**
 * A fragment representing a single Player detail screen.
 * This fragment is either contained in a {@link PlayerListActivity}
 * in two-pane mode (on tablets) or a {@link PlayerDetailActivity}
 * on handsets.
 */
public class PlayerDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private PlayerDTO mItem;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            final Bundle bundle = getArguments().getBundle(ARG_ITEM_ID);
            mItem = bundle.getParcelable(ARG_ITEM_ID);

            final Activity activity = getActivity();
            final CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.player_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.player_detail)).setText(Utils.decodeEmail(mItem.getEmail()));
        }

        return rootView;
    }

    /**
     * Creates a new fragment instance.
     * @param playerDTO The information to display.
     * @return The new fragment instance.
     */
    public static PlayerDetailFragment newInstance(final PlayerDTO playerDTO) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM_ID, playerDTO);
        return newInstance(args);
    }

    public static PlayerDetailFragment newInstance(final Bundle args) {
        final PlayerDetailFragment playerDetailFragment = new PlayerDetailFragment();
        playerDetailFragment.setArguments(args);
        return playerDetailFragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerDetailFragment() {
    }

}
