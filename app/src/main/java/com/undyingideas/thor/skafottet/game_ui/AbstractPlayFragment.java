package com.undyingideas.thor.skafottet.game_ui;

/**
 * Created on 09-11-2015, 08:39.
 * Project : skafottet
 *
 * @author Thor
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.Galgelogik;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.views.HangedView;

import java.util.ArrayList;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.utility.GameUtility.s_prefereces;

/**
 * Holds the shared gamelogik/flow, in order to allow different layouts to be utilized
 *
 */
public class AbstractPlayFragment extends Fragment {

    protected Galgelogik logik;
    ArrayList<String> possibleWords = new ArrayList<>();
    private boolean isHotSeat;
    private String theGuess; //bruges til at holde det aktuelle gæt
    HangedView galgen;

    Button ok;
    TextView usedLetters, ordet, status;
    EditText input;

    void guess(final String guess, final boolean isMultiButtonInterface) {
        final Boolean isMultiBtn = isMultiButtonInterface;

        theGuess = guess;
        if (theGuess.length() > 1) {
            theGuess = theGuess.substring(0, 1);
            logik.gætBogstav(theGuess);
            status.setText("Brug kun ét bogstav, resten vil blive ignoreret");
        } else {
            if (!isMultiBtn) status.setText("");
            logik.gætBogstav(theGuess);
        }

        if (!logik.erSpilletSlut()) {
            if (!isMultiBtn) updateScreen();
            else updateScreen(false, false); // because its the Hangman with mulityple buttons an no input fields
        } else {
            StartEndgame();
        }
    }

    void guess(final String guess) {
        guess(guess, true);
    }

    private void updateScreen(final boolean hasUsedLettersStat, final boolean hasInputTxtField) {
        ordet.setText(logik.getSynligtOrd());
        if (hasUsedLettersStat) usedLetters.append(theGuess);
        if (!logik.erSidsteBogstavKorrekt()) {
            final int wrongs = logik.getAntalForkerteBogstaver();
            galgen.setState(wrongs);
            YoYo.with(Techniques.Landing).duration(100).playOn(galgen);
//            if (wrongs == 1) {
//                galgen.setImageResource(R.drawable.forkert1);
//            } else if (wrongs == 2) {
//                galgen.setImageResource(R.drawable.forkert2);
//            } else if (wrongs == 3) {
//                galgen.setImageResource(R.drawable.forkert3);
//            } else if (wrongs == 4) {
//                galgen.setImageResource(R.drawable.forkert4);
//            } else if (wrongs == 5) {
//                galgen.setImageResource(R.drawable.forkert5);
//            } else if (wrongs == 6) {
//                galgen.setImageResource(R.drawable.forkert6);
//            }
        }
        if (hasInputTxtField) input.setText(null);
    }

    private void updateScreen() {
        updateScreen(true, true);
    }

    void CheckGameType() {//checks gametype, to se whether its a newgame by some extra info from activating classes, but that would require more refactoring
        isHotSeat = getArguments().getBoolean(GameUtility.KEY_IS_HOT_SEAT, false);
        Log.d("Play", "CheckGameType: isHotseat " + isHotSeat);
        if (isHotSeat) {
            possibleWords.add(getArguments().getString("theWord"));
        } else if (logik != null && logik.erSpilletSlut()) {
            logik.nulstil();
        } else {// for det tilfældes skyld at der er tale om et nyt spil
            possibleWords.addAll((HashSet<String>) s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
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
