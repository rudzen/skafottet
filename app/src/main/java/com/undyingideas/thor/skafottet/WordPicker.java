package com.undyingideas.thor.skafottet;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.undyingideas.thor.skafottet.dialogs.YesNo;

import java.util.ArrayList;

public class WordPicker extends AppCompatActivity implements YesNo.YesNoResultListener {

    ListView wordList;
    ArrayList<String> muligeOrd;
    private boolean isHotseat = false;
    private String possibleWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ord_vaelger);
        isHotseat = getIntent().getBooleanExtra("hotSeat", false);
        muligeOrd = getIntent().getStringArrayListExtra("muligeOrd");
        wordList = (ListView) findViewById(R.id.ordListen);
        if(isHotseat)wordList.setOnItemClickListener(new ListClickListener());

        wordList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, muligeOrd));
    }

    @Override
    public void onDone(boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            Intent startGame = new Intent(this, Play.class);
            startGame.putExtra("wordToBeGuessed", possibleWord);
            startActivity(startGame);
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
