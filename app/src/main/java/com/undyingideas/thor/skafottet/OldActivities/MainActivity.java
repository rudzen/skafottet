package com.undyingideas.thor.skafottet.OldActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.undyingideas.thor.skafottet.Instructions;
import com.undyingideas.thor.skafottet.Preferences;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.WordPicker;

// TODO : Fix memory leaks
public class MainActivity extends Activity {

    private Button StartBtn, instructionBtn, preferenceBtn;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnMultiplayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(MainActivity.this, MultiplayerActivity.class)
                        .putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd")));
            }
        });

    }

    /**
     * knap til at starte 1p spil
     * @param v
     */
    public void startClck(final View v){
        final Intent startGame = new Intent(this, HangmanButtonActivity.class);
        startGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));

        //Intent startGame = new Intent(MainActivity.this, LoadingScreen.class);

        startActivity(startGame);
    }

    /**
     * Knap til instillings aktivitet
     * @param v
     */
    public void preferenceClck(final View v){
        startActivity(new Intent(this, Preferences.class));
    }

    /**
     * knap til instruktions aktivitet
     * @param view
     */
    public void instructionClck(final View view) {
        final Intent Instillinger = new Intent(this, Instructions.class);
        startActivity(Instillinger);
    }

    public void getWordsClck(final View v){
        final Intent wordPicker = new Intent(this, WordPicker.class);
        wordPicker.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        startActivity(wordPicker);
//        WordPicker fragment = new WordPicker();
//        getFragmentManager().beginTransaction()
//                .replace(R.id.fragmentContentView, fragment)
//                .addToBackStack(null)
//                .commit();
    }
}
