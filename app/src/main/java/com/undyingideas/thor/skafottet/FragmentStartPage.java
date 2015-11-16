package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentStartPage extends Fragment {

    private Button StartBtn, instructionBtn, preferenceBtn;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        View rot = i.inflate(R.layout.activity_main,container,false);


        findViewById(R.id.btnMultiplayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MultiplayerActivity.class)
                        .putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd")));
            }
        });

        return rot;

    }

    /**
     * knap til at starte 1p spil
     * @param v
     */
    public void startClck(View v){
        Intent startGame = new Intent(getActivity(), HangmanButtonActivity.class);
        startGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        //Intent startGame = new Intent(MainActivity.this, LoadingScreen.class);

        startActivity(startGame);
    }

    /**
     * Knap til instillings aktivitet
     * @param v
     */
    public void preferenceClck(View v){
        startActivity(new Intent(getActivity(), Preferences.class));
    }

    /**
     * knap til instruktions aktivitet
     * @param view
     */
    public void instructionClck(View view) {
        Intent Instillinger = new Intent(getActivity(), Instructions.class);
        startActivity(Instillinger);
    }

    public void getWordsClck(View v){
        Intent wordPicker = new Intent(getActivity(), WordPicker.class);
        wordPicker.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        startActivity(wordPicker);
//        WordPicker fragment = new WordPicker();
//        getFragmentManager().beginTransaction()
//                .replace(R.id.fragmentContentView, fragment)
//                .addToBackStack(null)
//                .commit();
    }
}
