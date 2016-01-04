package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.undyingideas.thor.skafottet.dialogs.YesNo;

public class FragmentMainActivity extends AppCompatActivity implements YesNo.YesNoResultListener {


    private static String possibleWord;

    public static String getPossibleWord() {
        return possibleWord;
    }

    public static void setPossibleWord(String possibleWord) {
        FragmentMainActivity.possibleWord = possibleWord;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);



        if (savedInstanceState == null) {
            final Bundle b = new Bundle();
            b.putStringArrayList("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));

            final FragmentStartPage fragment = new FragmentStartPage();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentindhold, fragment)// tom container i layout
                    .addToBackStack(null)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onDone(final boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            final HangmanButtonFragment startGame = new HangmanButtonFragment();
            final Bundle data = new Bundle();
            data.putBoolean("isHotSeat", true);
            data.putString("theWord", getPossibleWord());
            startGame.setArguments(data);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentindhold, startGame)
                        .addToBackStack(null)
                            .commit();
        }
        else Log.d("wordPicer", "wordDenied");
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //onBackPressed();
            Log.d("main", "button pressed");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        final int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
