package com.undyingideas.thor.skafottet;

/**
 * Created by Thor on 09-11-2015.
 */

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Holds the shared gamelogik/flow, in order to allow different layouts to be utilized
 *
 */
public class AbstractPlayFragment extends Fragment{

    Galgelogik logik;
    ArrayList<String> possibleWords;
    private boolean isHotSeat;
    String theGuess; //bruges til at holde det aktuelle gæt
    SharedPreferences data;
    ImageView galgen;

    Button ok;
    TextView usedLetters, ordet, status;
    EditText input;

    protected void guess(String guess){

        theGuess = guess;
        if (theGuess.length() > 1){
            theGuess = theGuess.substring(0, 1);
            logik.gætBogstav(theGuess);
            status.setText("Brug kun ét bogstav, resten vil blive ignoreret");
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
    protected void CheckGameType(){//checks gametype, to se whether its a newgame by some extra info from activating classes, but that would require more refactoring
        isHotSeat = getArguments().getBoolean("isHotSeat", false);
        Log.d("Play", "CheckGameType: isHotseat " + isHotSeat);
        if(isHotSeat){
            possibleWords.add(getArguments().getString("theWord"));
        }
        else if(logik != null && logik.erSpilletSlut()){
            logik.nulstil();
        } else{// for det tilfældes skyld at der er tale om et nyt spil
            data = PreferenceManager.getDefaultSharedPreferences(getActivity());
            ArrayList<String> candidateLlist = new ArrayList<>();
            candidateLlist.addAll(data.getStringSet("possibleWords", null));
            if(candidateLlist != null) possibleWords = candidateLlist; //
            else possibleWords.add("default");


        }
        logik = new Galgelogik(possibleWords);
    }
    private void StartEndgame() {// gathers need data for starting up the endgame Fragment
        EndOfGameFragment fragment = new EndOfGameFragment();
        Bundle endgame = new Bundle();
        endgame.putBoolean("vundet", logik.erSpilletVundet());
        endgame.putInt("forsøg", logik.getAntalForkerteBogstaver());
        endgame.putString("ordet", logik.getOrdet());
        endgame.putString("spiller", "Du");
        endgame.putBoolean("wasHotSeat", isHotSeat);
        fragment.setArguments(endgame);

        getFragmentManager().beginTransaction().replace(R.id.fragmentindhold, fragment).commit();

        Log.d("play", "finishing");

    }



}
