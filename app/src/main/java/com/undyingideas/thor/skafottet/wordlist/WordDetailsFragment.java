/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.wordlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Fragment for showing the word details. With self-contained static onItemClickListener.
 * @author rudz
 */
public class WordDetailsFragment extends Fragment {

    /* create insance of the fragment with the correct data in bundle */
    public static WordDetailsFragment newInstance(final int index) {
        final WordDetailsFragment f = new WordDetailsFragment();
        final Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    // LayoutInflator puts the Fragment on the screen
    // ViewGroup is the view you want to attach the Fragment to
    // Bundle stores key value pairs so that data can be saved

    /**
     * Creates wordlistfragment view.
     * @param inflater The inflater
     * @param container The container (should not be used!)
     * @param savedInstanceState Unused
     * @return Listview containing the words
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        /* configure adapter */
        final ArrayAdapter detailsAdapter = new HangmanWordsAdapter(getActivity(), Game.wl.getListByIndex(getShownIndex()));

        Log.d("WDFrag", "Index    : " + Integer.toString(getShownIndex()));
        Log.d("WDFrag", "ListSize : " + Integer.toString(Game.wl.getListByIndex(getShownIndex()).size()));

        /* set up list view */
        final ListView lv = new ListView(getActivity());

        lv.setOnItemClickListener(new ListClickListener(this));
        lv.setAdapter(detailsAdapter);

        /* set the padding to ** 4 ** dpi, based on the current device's dm */
        /* based on Derek Banas code here ! */
        //int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getActivity().getResources().getDisplayMetrics());
        //lv.setPadding(padding, padding, padding, padding);

        return lv;
    }

    /**
     * Static click listener with weak this reference for guarding against unwanted things :-)
     */
    private static class ListClickListener implements AdapterView.OnItemClickListener {

        private final WeakReference<WordDetailsFragment> wordDetailsFragmentWeakReference;

        public ListClickListener(final WordDetailsFragment wordDetailsFragment) {
            wordDetailsFragmentWeakReference = new WeakReference<>(wordDetailsFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WordDetailsFragment wordDetailsFragment = wordDetailsFragmentWeakReference.get();
            if (wordDetailsFragment != null) {
                Toast.makeText(wordDetailsFragment.getActivity(), String.valueOf(parent.getItemAtPosition(position)), Toast.LENGTH_SHORT).show();
            }
        }
    }
}