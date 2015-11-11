package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class WordPicker extends AppCompatActivity {

    ListView ordListe;
    ArrayList<String> muligeOrd;
    private boolean isHotseat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ord_vaelger);
        getIntent().getBooleanExtra("hotSeat", false);
        muligeOrd = getIntent().getStringArrayListExtra("muligeOrd");
        ordListe = (ListView) findViewById(R.id.ordListen);

        ordListe.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, muligeOrd));
    }
}
