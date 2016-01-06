package com.undyingideas.thor.skafottet;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.dialogs.YesNo;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class WordPicker extends Fragment {
    private static final String TAG = WordPicker.class.getSimpleName();
    private ArrayList<String> muligeOrd;
    private boolean isHotseat;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rot = inflater.inflate(R.layout.activity_ord_vaelger, container, false);
        if (getArguments() != null) {
            isHotseat = getArguments().getBoolean(GameUtility.KEY_IS_HOT_SEAT, false);
        }
        Log.d(TAG, "isHotSeat:" + isHotseat);
        muligeOrd = new ArrayList<>();
        Log.d(TAG, "cacheSize:" + GameUtility.s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
        muligeOrd.addAll((HashSet<String>) GameUtility.s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));

        final ListView wordList = (ListView) rot.findViewById(R.id.ordListen);
        if (isHotseat) {
            final TextView title = (TextView) rot.findViewById(R.id.WordPickerTitle);
            title.setText("Vælg et ord til din modstander!");
            wordList.setOnItemClickListener(new ListClickListener(this));
        }

        final ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, muligeOrd);
        wordList.setAdapter(adapter);
        return rot;
    }

    private static class ListClickListener implements AdapterView.OnItemClickListener {

        private final WeakReference<WordPicker> wordPickerWeakReference;

        public ListClickListener(final WordPicker wordPicker) {
            wordPickerWeakReference = new WeakReference<>(wordPicker);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WordPicker wordPicker = wordPickerWeakReference.get();
            if (wordPicker != null) {
                FragmentMainActivity.setS_possibleWord(wordPicker.muligeOrd.get(position));
                final DialogFragment nf = YesNo.newInstance("Skal ordet være?", wordPicker.muligeOrd.get(position), "Ja", "Nej");
                Log.d("lol", "clicked");
                nf.show(wordPicker.getFragmentManager(), "newAcceptDialog");
            }
        }
    }
}
