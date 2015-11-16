package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button StartBtn, instructionBtn, preferenceBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnMultiplayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MultiplayerActivity.class)
                        .putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd")));
            }
        });

    }

    /**
     * knap til at starte 1p spil
     * @param v
     */
    public void startClck(View v){
        Intent startGame = new Intent(MainActivity.this, HangmanButtonActivity.class);
        startGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        //Intent startGame = new Intent(MainActivity.this, LoadingScreen.class);

        startActivity(startGame);
    }

    /**
     * Knap til instillings aktivitet
     * @param v
     */
    public void preferenceClck(View v){
        startActivity(new Intent(MainActivity.this, Preferences.class));
    }

    /**
     * knap til instruktions aktivitet
     * @param view
     */
    public void instructionClck(View view) {
        Intent Instillinger = new Intent(MainActivity.this, Instructions.class);
        startActivity(Instillinger);
    }

    public void getWordsClck(View v){
//        Intent wordPicker = new Intent(MainActivity.this, WordPicker.class);
//        wordPicker.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
//        startActivity(wordPicker);
        WordPicker fragment = new WordPicker();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContentView, fragment)
                .addToBackStack(null)
                .commit();
    }
}
