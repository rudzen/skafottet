package com.undyingideas.thor.skafottet;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.dialogs.YesNo;

import java.util.ArrayList;
import java.util.HashSet;

public class WordPicker extends Fragment implements YesNo.YesNoResultListener {
    SharedPreferences wordListGetter;
    ListView wordList;
    ArrayList<String> muligeOrd;
    TextView title;
    private boolean isHotseat = false;
    private String possibleWord;
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {

        View rot = i.inflate(R.layout.activity_ord_vaelger,container,false);
        wordListGetter = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        isHotseat = getIntent().getBooleanExtra("isHotSeat", false);

        muligeOrd = new ArrayList<>();
        Log.d("WordPicker", "cacheSize:" + wordListGetter.getStringSet("possibleWords",null).size() );
        muligeOrd.addAll(wordListGetter.getStringSet("possibleWords", null));

        //muligeOrd = getIntent().getStringArrayListExtra("muligeOrd");
        wordList = (ListView) rot.findViewById(R.id.ordListen);
        if(isHotseat){
            title = (TextView) rot.findViewById(R.id.WordPickerTitle);
            title.setText("Vælg et ord til din modstander!");
            wordList.setOnItemClickListener(new ListClickListener());
        }

        wordList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, muligeOrd));
        return rot;
    }

    @Override
    public void onDone(boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            Intent startGame = new Intent(getActivity(), HangmanButtonActivity.class);
            startGame.putExtra("isHotSeat", true);
            startGame.putExtra("wordToBeGuessed", possibleWord);
            startActivity(startGame);
//            finish();
        }
        else Log.d("wordPicer", "wordDenied");

    }

    private class ListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            possibleWord = muligeOrd.get(position);
            DialogFragment nf = YesNo.newInstance("Skal ordet være?", muligeOrd.get(position), "Ja", "Nej");

            Log.d("lol", "clicked");
            nf.show(getFragmentManager(), "newAcceptDialog");
        }
    }
}
