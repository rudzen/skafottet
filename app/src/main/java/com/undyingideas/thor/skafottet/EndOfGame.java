package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class EndOfGame extends Activity {

    WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    Bundle gameData;
    String resultText;
    Button newGame, endGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_game);

        endImage = (ImageView) findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) findViewById(R.id.SpilresultaterWebView);

        gameData = getIntent().getExtras();

        displayResults(gameData);

    }
    private void displayResults(Bundle spilData){

        if(spilData.getBoolean("vundet")){
            endImage.setImageResource(R.mipmap.vundet);
            resultText = "<html><body>Tilykke du har vundet <br> <br> Du gættede forkert <b> " + spilData.getInt("forsøg") + " gange</b>.</body></html>";
            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);
        }
        else{
            endImage.setImageResource(R.mipmap.rip);
            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + spilData.getString("ordet") + "</b>.</body></html>";
            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);

        }
        Log.d("endgame", "data: " + spilData.getString("spiller ") + " " + spilData.getString("forsøg") + " " + spilData.getBoolean("vundet"));


    }

    public void newGameClck(View view) {
        Intent newGame;
        if(getIntent().getBooleanExtra("wasHotSeat", false)){
            newGame = new Intent(this, WordPicker.class);
            newGame.putExtra("isHotSeat", true);
        } else{
            newGame = new Intent(this, HangmanButtonActivity.class);
            newGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        }
        startActivity(newGame);
        finish();
    }

    public void endGameClck(View view) {
        //startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
