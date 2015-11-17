package com.undyingideas.thor.skafottet.OldActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.Galgelogik;
import com.undyingideas.thor.skafottet.R;

import java.util.ArrayList;

public class HangmanButtonActivity extends Activity {

    private Galgelogik game;
    private ImageView imageView;
    private TextView ordet;
    private ArrayList<Button> listOfButtons = new ArrayList<Button>();
    private String theGuess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.galge);

        if (getIntent().getExtras().getBoolean("isHotSeat"))
            game = new Galgelogik(getIntent().getExtras().getString("wordToBeGuessed"));
        else
            game = new Galgelogik(getIntent().getExtras().getStringArrayList("muligeOrd"));
        //game.nulstil();
        ordet = (TextView) findViewById(R.id.visibleText);
        ordet.setText(game.getSynligtOrd());
        resetButtons();
    }

    public void buttonOnClick(View v){
        Button button = (Button )v;
        ((Button) v).setVisibility(View.INVISIBLE);

        listOfButtons.add((Button) v);
        String letter = ((Button) v).getText().toString();
        guess(letter);
    }
    public void resetButtons(){
        for(int i=0; i<listOfButtons.size();i++){
            Button button;
            button = listOfButtons.get(i);
            button.setVisibility(View.VISIBLE);
        }
    }

    private void guess(String guess){

        theGuess = guess;
        game.gætBogstav(theGuess);

        if(!game.erSpilletSlut()){
            updateScreen();
        } else {
            Intent endgame = new Intent(HangmanButtonActivity.this, EndOfGame.class);

            endgame.putExtra("vundet", game.erSpilletVundet());
            endgame.putExtra("forsøg", game.getAntalForkerteBogstaver());
            endgame.putExtra("ordet", game.getOrdet());
            endgame.putExtra("spiller", "Du");
            endgame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
            startActivity(endgame);
            Log.d("play", "finishing");
            finish();
        }
    }

    private void updateScreen(){
        ordet.setText(game.getSynligtOrd());
        //usedLetters.append(theGuess);
        if(!game.erSidsteBogstavKorrekt()){
            int wrongs = game.getAntalForkerteBogstaver();

            switch (wrongs){
                case 1:
                   imageView.setImageResource(R.mipmap.forkert1);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.forkert2);
                    break;
                case 3:
                    imageView.setImageResource(R.mipmap.forkert3);
                    break;
                case 4:
                    imageView.setImageResource(R.mipmap.forkert4);
                    break;
                case 5:
                    imageView.setImageResource(R.mipmap.forkert5);
                    break;
                case 6:
                    imageView.setImageResource(R.mipmap.forkert6);
                    break;
                default:
                    break;
            }
        }
    }

//    public void testForFails(){
//
//        if(game.erSpilletTabt()||game.erSpilletTabt()) {
//            game.nulstil();
//            Toast toast = new Toast(getApplicationContext());
//            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
//            Toast.makeText(HangmanButtonActivity.this, "Spillet er slut", Toast.LENGTH_LONG).show();
//            resetButtons();
//        }
//
//        if (game.getAntalForkerteBogstaver()==1){
//            imageView.setImageResource(R.mipmap.forkert1);
//        }
//        else if (game.getAntalForkerteBogstaver()==2){
//            imageView.setImageResource(R.mipmap.forkert2);
//        }
//        else if (game.getAntalForkerteBogstaver()==3){
//            imageView.setImageResource(R.mipmap.forkert3);
//        }
//        else if (game.getAntalForkerteBogstaver()==4){
//            imageView.setImageResource(R.mipmap.forkert4);
//        }
//        else if (game.getAntalForkerteBogstaver()==5){
//            imageView.setImageResource(R.mipmap.forkert5);
//        }
//        else if (game.getAntalForkerteBogstaver()==6){
//            imageView.setImageResource(R.mipmap.forkert6);
//        }
//        else if (game.getAntalForkerteBogstaver()==0) {
//            imageView.setImageResource(R.mipmap.galge);
//        }
//
//        ordet.setText(game.getSynligtOrd());
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
