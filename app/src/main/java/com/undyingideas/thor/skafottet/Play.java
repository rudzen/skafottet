package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Play extends Activity {
    ImageView galgen;
    Galgelogik logik;
    Button ok;
    TextView usedLetters, ordet;
    EditText input;
    String gaet; //bruges til at holde det aktuelle gæt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //instatierer widgets
        ok = (Button) findViewById(R.id.InputBtn);
        usedLetters = (TextView) findViewById(R.id.usedLetters);
        ordet = (TextView) findViewById(R.id.synligtOrd);
        input = (EditText) findViewById(R.id.gaet);
        galgen = (ImageView) findViewById(R.id.galgen);
        logik = new Galgelogik();
        ordet.setText(logik.getSynligtOrd());

    }

    public void gaetClck(View view) {

      gaet = input.getText().toString();

        logik.gætBogstav(gaet);

        if(!logik.erSpilletSlut()){
            updateScreen();
        } else {
                Intent endgame = new Intent(Play.this, EndOfGame.class);

                endgame.putExtra("vundet", logik.erSpilletVundet());
                endgame.putExtra("forsøg", logik.getAntalForkerteBogstaver());
                endgame.putExtra("ordet", logik.getOrdet());
                endgame.putExtra("spiller", "Du");
                startActivity(endgame);
                Log.d("play", "finishing");
                finish();
        }
    }

    private void updateScreen(){
        ordet.setText(logik.getSynligtOrd());
        usedLetters.append(gaet);
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
    }
}
