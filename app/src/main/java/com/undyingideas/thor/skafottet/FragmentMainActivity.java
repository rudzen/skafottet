package com.undyingideas.thor.skafottet;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.undyingideas.thor.skafottet.dialogs.YesNo;

public class FragmentMainActivity extends AppCompatActivity implements YesNo.YesNoResultListener {


    protected static String possibleWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);



        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putStringArrayList("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));

            FragmentStartPage fragment = new FragmentStartPage();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentindhold, fragment)// tom container i layout
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void onDone(boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            PlayFragment startGame = new PlayFragment();
            Bundle data = new Bundle();
            data.putBoolean("isHotSeat", true);
            data.putString("theWord", possibleWord);
            startGame.setArguments(data);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, startGame)
                        .addToBackStack(null)
                            .commit();


//            Intent startGame = new Intent(this, HangmanButtonActivity.class);
//            startGame.putExtra("isHotSeat", true);
//            startGame.putExtra("wordToBeGuessed", possibleWord);
//            startActivity(startGame);
        }
        else Log.d("wordPicer", "wordDenied");
    }
}
