package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HangmanButtonActivity extends Activity {

    private Galgelogik game;;
    private TextView visible;
    private ArrayList<Button> listOfButtons = new ArrayList<Button>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_button);
        game.nulstil();
        visible = (TextView) findViewById(R.id.visibleText);
        testForFails();
    }

    public void buttonOnClick(View v){
        // do something

        Button button= (Button )v;
        ((Button) v).setVisibility(View.INVISIBLE);

        listOfButtons.add((Button) v);
        String letter = ((Button) v).getText().toString();
        game.gætBogstav(letter);
        testForFails();
    }
    public void resetButtons(){
        for(int i=0; i<listOfButtons.size();i++){
            Button button;
            button = listOfButtons.get(i);
            button.setVisibility(View.VISIBLE);
        }
    }

    public void testForFails(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if(game.erSpilletTabt()||game.erSpilletTabt()) {
            game.nulstil();
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            Toast.makeText(HangmanButtonActivity.this, "Spillet er slut", Toast.LENGTH_LONG).show();
            resetButtons();
        }

        if (game.getAntalForkerteBogstaver()==1){
            imageView.setImageResource(R.mipmap.forkert1);
        }
        else if (game.getAntalForkerteBogstaver()==2){
            imageView.setImageResource(R.mipmap.forkert2);
        }
        else if (game.getAntalForkerteBogstaver()==3){
            imageView.setImageResource(R.mipmap.forkert3);
        }
        else if (game.getAntalForkerteBogstaver()==4){
            imageView.setImageResource(R.mipmap.forkert4);
        }
        else if (game.getAntalForkerteBogstaver()==5){
            imageView.setImageResource(R.mipmap.forkert5);
        }
        else if (game.getAntalForkerteBogstaver()==6){
            imageView.setImageResource(R.mipmap.forkert6);
        }
        else if (game.getAntalForkerteBogstaver()==0) {
            imageView.setImageResource(R.mipmap.galge);
        }

        visible.setText(game.getSynligtOrd());
    }

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
