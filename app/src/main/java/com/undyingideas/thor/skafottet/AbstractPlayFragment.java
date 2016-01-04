package com.undyingideas.thor.skafottet;

/**
 * Created on 09-11-2015, 08:39.
 * Project : skafottet
 * @author Thor
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Holds the shared gamelogik/flow, in order to allow different layouts to be utilized
 *
 */
public class AbstractPlayFragment extends Fragment {

    protected Galgelogik logik;
    ArrayList<String> possibleWords;
    private boolean isHotSeat;
    private String theGuess; //bruges til at holde det aktuelle gæt
    private SharedPreferences data;
    ImageView galgen;

    Button ok;
    TextView usedLetters, ordet, status;
    EditText input;

    void guess(final String guess, final boolean isMultiButtonInterface){
        final Boolean isMultiBtn = isMultiButtonInterface;

        theGuess = guess;
        if (theGuess.length() > 1){
            theGuess = theGuess.substring(0, 1);
            logik.gætBogstav(theGuess);
            status.setText("Brug kun ét bogstav, resten vil blive ignoreret");
        } else {
            if(!isMultiBtn) status.setText("");
            logik.gætBogstav(theGuess);
        }

        if(!logik.erSpilletSlut()){
            if (!isMultiBtn)updateScreen();
            else updateScreen(false, false); // because its the Hangman with mulityple buttons an no input fields
        } else {
            StartEndgame();
        }
    }
    void guess(final String guess){
       guess(guess, true);
    }


    private void updateScreen(final boolean hasUsedLettersStat, final boolean hasInputTxtField){
        ordet.setText(logik.getSynligtOrd());
        if(hasUsedLettersStat) usedLetters.append(theGuess);
        if(!logik.erSidsteBogstavKorrekt()){
            final int wrongs = logik.getAntalForkerteBogstaver();
            switch (wrongs){
                case 1:
                    galgen.setImageResource(R.drawable.forkert1);
                    break;
                case 2:
                    galgen.setImageResource(R.drawable.forkert2);
                    break;
                case 3:
                    galgen.setImageResource(R.drawable.forkert3);
                    break;
                case 4:
                    galgen.setImageResource(R.drawable.forkert4);
                    break;
                case 5:
                    galgen.setImageResource(R.drawable.forkert5);
                    break;
                case 6:
                    galgen.setImageResource(R.drawable.forkert6);
                    break;
            }
        }
        if(hasInputTxtField)input.setText("");
    }

    private void updateScreen(){
       updateScreen(true, true);
    }
    void CheckGameType(){//checks gametype, to se whether its a newgame by some extra info from activating classes, but that would require more refactoring
        isHotSeat = getArguments().getBoolean("isHotSeat", false);
        Log.d("Play", "CheckGameType: isHotseat " + isHotSeat);
        if(isHotSeat){
            possibleWords.add(getArguments().getString("theWord"));
        }
        else if(logik != null && logik.erSpilletSlut()){
            logik.nulstil();
        } else{// for det tilfældes skyld at der er tale om et nyt spil
            data = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final ArrayList<String> candidateLlist = new ArrayList<>();
            candidateLlist.addAll(data.getStringSet("possibleWords", null));
            possibleWords = candidateLlist; //


        }
        logik = new Galgelogik(possibleWords);
    }
    private void StartEndgame() {// gathers need data for starting up the endgame Fragment
        final EndOfGameFragment fragment = new EndOfGameFragment();
        final Bundle endgame = new Bundle();
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
