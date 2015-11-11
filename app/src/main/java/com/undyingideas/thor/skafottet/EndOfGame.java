package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class EndOfGame extends Activity {

    WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    ImageView pokalen; //skal vise et vinder billede, eller et straffende taberbillede
    Bundle spilData;
    String resultatText;
    Button nytSpil, afslut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_game);

        pokalen = (ImageView) findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) findViewById(R.id.SpilresultaterWebView);

        spilData = getIntent().getExtras();
        //nytSpil = (Button) findViewById(R.id.nytSplBtn);
       // afslut = (Button) findViewById(R.id.afslutBtn);

        displayResults(spilData);




    }
    private void displayResults(Bundle spilData){

        if(spilData.getBoolean("vundet")){
            pokalen.setImageResource(R.mipmap.vundet);
            resultatText = "<html><body>Tilykke du har vundet <br> <br> Du gættede forkert <b> " + spilData.getInt("forsøg") + " gange</b> .</body></html>";
            resultaterDisp.loadData(resultatText,"text/html; charset=UTF-8", null);
        }
        else{
            pokalen.setImageResource(R.mipmap.rip);
            resultatText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + spilData.getString("ordet") + "</b> .</body></html>";
            resultaterDisp.loadData(resultatText,"text/html; charset=UTF-8", null);

        }
        Log.d("endgame", "data: " + spilData.getString("spiller ") + " " +  spilData.getString("forsøg")+ " " + spilData.getBoolean("vundet") );


    }

    public void nytSpilClck(View view) {
        Intent newGame = new Intent(this, HangmanButtonActivity.class);
        newGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));

        startActivity(newGame);
        finish();
    }

    public void afslutClck(View view) {
        //startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
