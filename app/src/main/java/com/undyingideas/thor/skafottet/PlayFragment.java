package com.undyingideas.thor.skafottet;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thor on 17-11-2015.
 */
public class PlayFragment extends AbstractPlayFragment {

    ImageView galgen;
    Galgelogik logik;
    ArrayList<String> possibleWords;
    private boolean isHotSeat;
    Button ok;
    TextView usedLetters, ordet, status;
    EditText input;
    String theGuess; //bruges til at holde det aktuelle gæt
    SharedPreferences data;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = i.inflate(R.layout.activity_play, container, false);


        //instantierer widgets
        ok = (Button) root.findViewById(R.id.InputBtn);
        ok.setOnClickListener(new OkClick());
        usedLetters = (TextView) root.findViewById(R.id.usedLetters);
        ordet = (TextView) root.findViewById(R.id.synligtOrd);
        input = (EditText) root.findViewById(R.id.gaet);
        galgen = (ImageView) root.findViewById(R.id.galgen);
        possibleWords = new ArrayList<>();
        CheckGameType();

        ordet.setText(logik.getSynligtOrd());
        status = (TextView) root.findViewById(R.id.statusText);
        return root;
    }



    private void guess(String guess){

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

    private void StartEndgame() {
        Intent endgame = new Intent(getActivity(), EndOfGame.class);
        EndOfGameFragment fragment = new EndOfGameFragment();

        getFragmentManager().beginTransaction().replace(R.id.fragmentindhold, fragment).commit();
//        endgame.putExtra("vundet", logik.erSpilletVundet());
//        endgame.putExtra("forsøg", logik.getAntalForkerteBogstaver());
//        endgame.putExtra("ordet", logik.getOrdet());
//        endgame.putExtra("spiller", "Du");
//        ///endgame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
//        Log.d("Play", "StartEndgame: isHotseat " + isHotSeat);
//        endgame.putExtra("wasHotSeat", isHotSeat);
//        startActivity(endgame);
        Log.d("play", "finishing");

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

    private class OkClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
                logik.logStatus();
                guess(input.getText().toString());

        }
    }
}



