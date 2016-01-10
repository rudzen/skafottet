/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.game_ui.wordlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.utility.GameUtility;

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
    private boolean mDuelPane;

    // Currently selected item in the ListView
    private int mCurCheckPosition;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArrayAdapter<String> lvAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, GameUtility.s_wordList.getWordTitleList());

        setListAdapter(lvAdapter);

        final View detailsFrame = getActivity().findViewById(R.id.word_details);

        mDuelPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        mCurCheckPosition = savedInstanceState != null ? savedInstanceState.getInt("curChoice", 0) : GameUtility.s_wordList.getIndexByTitle(GameUtility.s_wordList.getCurrentWordListTitle());

        if (mDuelPane) {
            // CHOICE_MODE_SINGLE allows one item in the ListView to be selected at a time
            // CHOICE_MODE_MULTIPLE allows multiple
            // CHOICE_MODE_NONE is the default and the item won't be highlighted in this case'
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        showDetails(position);
    }

    private void showDetails(final int index) {

        /* update last selected */
        mCurCheckPosition = index;

        /* if in landscape */
        if (mDuelPane) {

            /* highlight the selected item */
            getListView().setItemChecked(index, true);

            WordDetailsFragment details = (WordDetailsFragment) getFragmentManager().findFragmentById(R.id.word_details);

            if (details == null || details.getShownIndex() != index) {
                details = WordDetailsFragment.newInstance(index);
                final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.word_details, details);
                ft.commit();
            }

        } else {
            final Intent intent = new Intent();
            intent.setClass(getActivity(), WordDetailsActivity.class);
            intent.putExtra(WordDetailsFragment.KEY_INDEX, index);
            startActivity(intent);
//            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }
}
