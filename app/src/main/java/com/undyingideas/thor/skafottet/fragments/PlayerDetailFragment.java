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

}
