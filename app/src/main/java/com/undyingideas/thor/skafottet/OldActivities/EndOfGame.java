package com.undyingideas.thor.skafottet.OldActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.WordPicker;

public class EndOfGame extends Activity {

    private WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    private ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    private Bundle gameData;
    private String resultText;
    Button newGame, endGame;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_game);

        endImage = (ImageView) findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) findViewById(R.id.SpilresultaterWebView);

        gameData = getIntent().getExtras();

        displayResults(gameData);

    }
    private void displayResults(final Bundle spilData){

        if(spilData.getBoolean("vundet")){
            endImage.setImageResource(R.drawable.vundet);
            resultText = "<html><body>Tilykke du har vundet <br> <br> Du gættede forkert <b> " + spilData.getInt("forsøg") + " gange</b>.</body></html>";
            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);
        }
        else{
            endImage.setImageResource(R.drawable.rip);
            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + spilData.getString("ordet") + "</b>.</body></html>";
            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);

        }
        Log.d("endgame", "data: " + spilData.getString("spiller ") + " " + spilData.getString("forsøg") + " " + spilData.getBoolean("vundet"));
    }

    public void newGameClck(final View view) {
        final Intent intent;
        if(getIntent().getBooleanExtra("wasHotSeat", false)){
            intent = new Intent(this, WordPicker.class);
            intent.putExtra("isHotSeat", true);
        } else{
            intent = new Intent(this, HangmanButtonActivity.class);
            intent.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        }
        startActivity(intent);
        finish();
    }

    public void endGameClck(final View view) {
        //startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
