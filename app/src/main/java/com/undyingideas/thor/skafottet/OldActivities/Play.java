package com.undyingideas.thor.skafottet.OldActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.Galgelogik;
import com.undyingideas.thor.skafottet.R;

import java.util.ArrayList;

public class Play extends Activity {
    ImageView galgen;
    Galgelogik logik;
    ArrayList<String> possibleWords;
    private boolean isHotSeat;
    Button ok;
    TextView usedLetters, ordet, status;
    EditText input;
    String theGuess; //bruges til at holde det aktuelle gæt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //instantierer widgets
        ok = (Button) findViewById(R.id.InputBtn);
        usedLetters = (TextView) findViewById(R.id.usedLetters);
        ordet = (TextView) findViewById(R.id.synligtOrd);
        input = (EditText) findViewById(R.id.gaet);
        galgen = (ImageView) findViewById(R.id.galgen);
        possibleWords = new ArrayList<>();
        CheckGameType();

        ordet.setText(logik.getSynligtOrd());
        status = (TextView) findViewById(R.id.statusText);
    }

    public void gaetClck(View view) {
        logik.logStatus();
        guess(input.getText().toString());
    }

    private void guess(String guess){

        theGuess = guess;
        if (theGuess.length() > 1){
            theGuess = theGuess.substring(0, 1);
            logik.gætBogstav(theGuess);
            status.setText("Brug kun et bogstav, resten vil blive ignoreret");
        } else {
            status.setText("");
            logik.gætBogstav(theGuess);
        }

        if(!logik.erSpilletSlut()){
            updateScreen();
        } else {
            StartEndgame();
        }
    }

    private void StartEndgame() {
        Intent endgame = new Intent(Play.this, EndOfGame.class);

        endgame.putExtra("vundet", logik.erSpilletVundet());
        endgame.putExtra("forsøg", logik.getAntalForkerteBogstaver());
        endgame.putExtra("ordet", logik.getOrdet());
        endgame.putExtra("spiller", "Du");
        endgame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        Log.d("Play", "StartEndgame: isHotseat " + isHotSeat);
        endgame.putExtra("wasHotSeat", isHotSeat);
        startActivity(endgame);
        Log.d("play", "finishing");
        finish();
    }

    private void updateScreen(){
        ordet.setText(logik.getSynligtOrd());
        usedLetters.append(theGuess);
        if(!logik.erSidsteBogstavKorrekt()){
            int wrongs = logik.getAntalForkerteBogstaver();

            switch (wrongs){
                case 1:
                    galgen.setImageResource(R.mipmap.forkert1);
                    break;
                case 2:
                    galgen.setImageResource(R.mipmap.forkert2);
                    break;
                case 3:
                    galgen.setImageResource(R.mipmap.forkert3);
                    break;
                case 4:
                    galgen.setImageResource(R.mipmap.forkert4);
                    break;
                case 5:
                    galgen.setImageResource(R.mipmap.forkert5);
                    break;
                case 6:
                    galgen.setImageResource(R.mipmap.forkert6);
                    break;
            }
        }
        input.setText("");
    }
    private void CheckGameType(){// could check gametype by some extra info from activating classes, but that would require more refactoring
        isHotSeat = getIntent().getBooleanExtra("isHotSeat", false);
        Log.d("Play", "CheckGameType: isHotseat " + isHotSeat);
        if(logik != null && logik.erSpilletSlut()){
            logik.nulstil();
        } else{
            ArrayList<String> candidateLlist = getIntent().getStringArrayListExtra("muligeOrd");
            if(candidateLlist != null) possibleWords = candidateLlist; //
            else possibleWords.add(getIntent().getStringExtra("wordToBeGuessed"));

            logik = new Galgelogik(possibleWords);
        }
    }
}
