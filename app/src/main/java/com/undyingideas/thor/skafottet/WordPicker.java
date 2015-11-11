package com.undyingideas.thor.skafottet;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.undyingideas.thor.skafottet.dialogs.YesNo;

import java.util.ArrayList;
import java.util.HashSet;

public class WordPicker extends AppCompatActivity implements YesNo.YesNoResultListener {
    SharedPreferences wordListGetter;
    ListView wordList;
    ArrayList<String> muligeOrd;
    private boolean isHotseat = false;
    private String possibleWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ord_vaelger);
        wordListGetter = PreferenceManager.getDefaultSharedPreferences(this);
        isHotseat = getIntent().getBooleanExtra("isHotSeat", false);
        muligeOrd = new ArrayList<>();
        Log.d("WordPicker", "cacheSize:" + wordListGetter.getStringSet("possibleWords",null).size() );
        muligeOrd.addAll(wordListGetter.getStringSet("possibleWords", null));

        //muligeOrd = getIntent().getStringArrayListExtra("muligeOrd");
        wordList = (ListView) findViewById(R.id.ordListen);
        if(isHotseat)wordList.setOnItemClickListener(new ListClickListener());

        wordList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, muligeOrd));
    }

    @Override
    public void onDone(boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            Intent startGame = new Intent(this, Play.class);
            startGame.putExtra("isHotSeat", true);
            startGame.putExtra("wordToBeGuessed", possibleWord);
            startActivity(startGame);
            finish();
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
