package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * TODO : FLAGGED FOR RE-CODING SOON!
 * WordPicker Fragment.<br>
 * <p>
 *     Supports live update of current wordlist (requires preference setting to allow this!).<br>
 * </p>
 */
public class WordPickerFragment extends Fragment {

    private static final String TAG = "WordPickerFragment";
    private static final String KEY_LAST_INDEX = "lidx";
    private static final String KEY_MULTI_PLAYER = "mp";
    private static final String KEY_WORD_LIST = "wl";

    private ArrayList<String> wordList;

    private boolean isMultiPlayer;
    private int index;

    private ArrayAdapter adapter;

    private Runnable updater;

    public static WordPickerFragment newInstance(final int lastIndex, final boolean multiplayer, final ArrayList<String> wordList) {
        final WordPickerFragment wordPickerFragment = new WordPickerFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_LAST_INDEX, lastIndex);
        args.putBoolean(KEY_MULTI_PLAYER, multiplayer);
        args.putStringArrayList(KEY_WORD_LIST, wordList);
        wordPickerFragment.setArguments(args);
        return wordPickerFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rot = inflater.inflate(R.layout.activity_ord_vaelger, container, false);

        wordList = new ArrayList<>();

        boolean listAltered = false;

        if (getArguments() != null) {
            index = getArguments().getInt(KEY_LAST_INDEX);
            isMultiPlayer = getArguments().getBoolean(KEY_MULTI_PLAYER, false);
            listAltered = wordList.addAll(getArguments().getStringArrayList(KEY_WORD_LIST));
        }

        if (!listAltered) {
            // read the default list!
            //noinspection unchecked
            wordList.addAll((HashSet<String>) GameUtility.s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
        }

        Log.d(TAG, "isMultiPlayer : " + isMultiPlayer);
        Log.d(TAG, "Word List count : " + wordList.size());

        final ListView wordListView = (ListView) rot.findViewById(R.id.ordListen);
        if (isMultiPlayer) {
            final TextView listTitle = (TextView) rot.findViewById(R.id.WordPickerTitle);
            listTitle.setText("Vælg et ord til din modstander!");
            wordListView.setOnItemClickListener(new ListClickListener(this));
        }

        // TODO : Make new awesome layout and custom adapter :-)
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, wordList);
        wordListView.setAdapter(adapter);
        wordListView.setSelection(index);

        return rot;
    }

    private static class ListClickListener implements AdapterView.OnItemClickListener {

        private final WeakReference<WordPickerFragment> wordPickerWeakReference;

        public ListClickListener(final WordPickerFragment wordPickerFragment) {
            wordPickerWeakReference = new WeakReference<>(wordPickerFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WordPickerFragment wordPickerFragment = wordPickerWeakReference.get();
            if (wordPickerFragment != null) {
                // TODO : Rudz, recode
                GameActivity.setS_possibleWord(wordPickerFragment.wordList.get(position));
//                final DialogFragment nf = YesNo.newInstance("Skal ordet være?", wordPickerFragment.wordList.get(position), "Ja", "Nej");
//                Log.d("lol", "clicked");
//                nf.show(wordPickerFragment.getFragmentManager(), "newAcceptDialog");
            }
        }
    }

    /**
     * Responsible for updating the adapter when the word list is altered online.
     */
    private class WordListUpdater implements Runnable {

        @Override
        public void run() {
            // TODO : Get stuff from FireBase classes
            adapter.notifyDataSetChanged();
        }
    }


}
