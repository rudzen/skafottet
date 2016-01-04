/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.wordlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.utility.Game;

/* needs to be heavily altered with stuff */
/**
 * Shows the title fragment as a ListView.
 * When a ListView item is currently selected the WordDetailsFragment will be put in the FrameLayout
 * if the mode is Landscape, else the WordDetailsActivity is created.
 * This is based on the Android.Developer example.
 * @author rudz
 */
public class WordTitlesFragment extends ListFragment {

    // True or False depending on if we are in horizontal or duel pane mode
    boolean mDuelPane;

    // Currently selected item in the ListView
    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        ArrayAdapter<String> lvAdapter = new WordListAdapter(getActivity(), Game.wl);
        final ArrayAdapter<String> lvAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, Game.wl.getWordTitleList());

        // Connect the ListView to our data
        setListAdapter(lvAdapter);

        // Check if the FrameLayout with the id details exists
        final View detailsFrame = getActivity().findViewById(R.id.word_details);

        // Set mDuelPane based on whether you are in the horizontal layout
        // Check if the detailsFrame exists and if it is visible
        mDuelPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        // If the screen is rotated onSaveInstanceState() below will store the // hero most recently selected. Get the value attached to curChoice and // store it in mCurCheckPosition
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        } else {
            // Else read the wordlist current list.
            mCurCheckPosition = Game.wl.getIndexByTitle(Game.wl.getCurrentWordListTitle());
        }

        if (mDuelPane) {
            // CHOICE_MODE_SINGLE allows one item in the ListView to be selected at a time
            // CHOICE_MODE_MULTIPLE allows multiple
            // CHOICE_MODE_NONE is the default and the item won't be highlighted in this case'
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            // Send the item selected to showDetails so the right item info is shown
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    // When a list item is clicked we want to change the info
    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        showDetails(position);
    }

    // Shows the data
    void showDetails(int index) {

        /* update last selected */
        mCurCheckPosition = index;

        /* if in landscape */
        if (mDuelPane) {

            /* highlight the selected item */
            getListView().setItemChecked(index, true);

            // Create an object that represents the current FrameLayout that we will put the data in
            WordDetailsFragment details = (WordDetailsFragment) getFragmentManager().findFragmentById(R.id.word_details);

            // When a DetailsFragment is created by calling newInstance the index for the data
            // it is supposed to show is passed to it. If that index hasn't been assigned we must
            // assign it in the if block
            if (details == null || details.getShownIndex() != index) {

                // Make the details fragment and give it the currently selected hero index
                details = WordDetailsFragment.newInstance(index);

                // Start Fragment transactions
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                // Replace any other Fragment with our new Details Fragment with the right data
                ft.replace(R.id.word_details, details);

                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                //ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            final Intent intent = new Intent();
            intent.setClass(getActivity(), WordDetailsActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
//            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }
}
