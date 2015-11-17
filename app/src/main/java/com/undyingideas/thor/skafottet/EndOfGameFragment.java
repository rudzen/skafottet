package com.undyingideas.thor.skafottet;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import java.util.zip.Inflater;

/**
 * Created by Thor on 17-11-2015.
 */
public class EndOfGameFragment extends Fragment {

    WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    Bundle gameData;
    String resultText;
    Button newGame, endGame;
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = i.inflate(R.layout.activity_end_of_game, container ,false);
        endImage = (ImageView) root.findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) root.findViewById(R.id.SpilresultaterWebView);

        Button endGameBtn = (Button) root.findViewById(R.id.afslutBtn);
        endGameBtn.setOnClickListener(new endGameListener());

        Button newGameBtn = (Button) root.findViewById(R.id.nytSplBtn);
        newGameBtn.setOnClickListener(new startGameListener());

        gameData = getArguments();

        displayResults(gameData);
        return root;

    }
    private void displayResults(Bundle spilData){

        if(true){//spilData.getBoolean("vundet")){
            endImage.setImageResource(R.mipmap.vundet);
//            resultText = "<html><body>Tilykke du har vundet <br> <br> Du gættede forkert <b> " + spilData.getInt("forsøg") + " gange</b>.</body></html>";
//            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);
        }
        else{
            endImage.setImageResource(R.mipmap.rip);
//            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + spilData.getString("ordet") + "</b>.</body></html>";
//            resultaterDisp.loadData(resultText,"text/html; charset=UTF-8", null);

        }
//        Log.d("endgame", "data: " + spilData.getString("spiller ") + " " + spilData.getString("forsøg") + " " + spilData.getBoolean("vundet"));
    }

    private class endGameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent newGame;
//        if(getIntent().getBooleanExtra("wasHotSeat", false)){
//            newGame = new Intent(this, WordPicker.class);
//            newGame.putExtra("isHotSeat", true);
//        } else{
//            newGame = new Intent(this, HangmanButtonActivity.class);
//            newGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
//        }
//        startActivity(newGame);
//        finish();

        }
    }

    private class startGameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}
