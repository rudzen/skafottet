package com.undyingideas.thor.skafottet;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    .add(R.id.fragmentindhold, startGame)
                        .addToBackStack(null)
                            .commit();
        }
        else Log.d("wordPicer", "wordDenied");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //onBackPressed();
            Log.d("main", "button pressed");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
