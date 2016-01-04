package com.undyingideas.thor.skafottet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 * @author Thor
 */
public class EndOfGameFragment extends Fragment {

    private WebView resultaterDisp; //skal bruges til at vise spillets resultater, og om det er vundet etc.
    private ImageView endImage; //skal vise et vinder/taber billede, eller et straffende taberbillede
    private Bundle gameData;
    String resultText;
    Button newGame, endGame;

    @Override
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View root = i.inflate(R.layout.activity_end_of_game, container, false);
        endImage = (ImageView) root.findViewById(R.id.PokalBillede);
        resultaterDisp = (WebView) root.findViewById(R.id.SpilresultaterWebView);

        final Button endGameBtn = (Button) root.findViewById(R.id.afslutBtn);
        endGameBtn.setOnClickListener(new endGameListener());

        final Button newGameBtn = (Button) root.findViewById(R.id.nytSplBtn);
        newGameBtn.setOnClickListener(new startGameListener());

        gameData = getArguments();

        displayResults(gameData);
        return root;

    }

    private void displayResults(final Bundle gameData) {

        if (gameData.getBoolean("vundet")) {//checkes if the game is won
            endImage.setImageResource(R.drawable.vundet);
            resultText = "<html><body>Tilykke du har vundet <br> <br> Du gættede forkert <b> " + gameData.getInt("forsøg") + " gange</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        } else {// or lost
            endImage.setImageResource(R.drawable.rip);
            resultText = "<html><body>Du har tabt <br> <br> Ordet du ledte efter var <b> " + gameData.getString("ordet") + "</b>.</body></html>";
            resultaterDisp.loadData(resultText, "text/html; charset=UTF-8", null);
        }
        Log.d("endgame", "data: " + gameData.getString("spiller ") + " " + gameData.getString("forsøg") + " " + gameData.getBoolean("vundet"));
    }

    private class endGameListener implements View.OnClickListener {//for afslut spil clicked

        @Override
        public void onClick(final View v) {
            Log.d("endgame", "going to start Screen");
            final FragmentStartPage newGame = new FragmentStartPage();
            getFragmentManager().beginTransaction().replace(R.id.fragmentindhold, newGame).commit();
        }
    }

    private class startGameListener implements View.OnClickListener { //for newgame Clicked
        Bundle gameData = new Bundle();
        @Override
        public void onClick(final View v) {

            if (getArguments().getBoolean("wasHotSeat", false)){//starting new multiyPlayer game by going to wordPicker
                gameData.putBoolean("isHotSeat", true);
                final WordPicker newMultiPGame = new WordPicker();
                newMultiPGame.setArguments(gameData);
                getFragmentManager().beginTransaction().replace(R.id.fragmentindhold, newMultiPGame).commit();
            }

            else{//starting new singleplayergame
                gameData.putBoolean("isHotSeat", false);
                final HangmanButtonFragment newGame = new HangmanButtonFragment();
                newGame.setArguments(gameData);
                getFragmentManager().beginTransaction().replace(R.id.fragmentindhold, newGame).commit();
            }
        }
    }

    @Override
    public void onStart(){//this actually removes the keyboard
        super.onStart();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}