package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.dialogs.YesNo;
import com.undyingideas.thor.skafottet.utility.GameUtility;

public class MainActivity extends AppCompatActivity implements YesNo.YesNoResultListener {
    
    private static String s_possibleWord;

    public static String getS_possibleWord() {
        return s_possibleWord;
    }

    public static void setS_possibleWord(final String s_possibleWord) {
        MainActivity.s_possibleWord = s_possibleWord;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        if (savedInstanceState == null) {
            final Bundle bundle = new Bundle();
            bundle.putStringArrayList(GameUtility.KEY_MULIGE_ORD, getIntent().getStringArrayListExtra(GameUtility.KEY_MULIGE_ORD));

            final HangmanButtonFragment gameFragment = HangmanButtonFragment.newInstance(0, false);


//            final FragmentMenu_OLD fragment = new FragmentMenu_OLD();
//            final FragmentMenu_OLD fragment = new FragmentMenu_OLD();
//            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, gameFragment)// tom container i layout
                    .addToBackStack(null)
                    .commit();
        }

        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onDone(final boolean result) {
        if(result){
            Log.d("wordPicker", "WordAccepted");
            final HangmanButtonFragment startGame = new HangmanButtonFragment();
            final Bundle data = new Bundle();
            data.putBoolean(GameUtility.KEY_IS_HOT_SEAT, true);
            data.putString("theWord", s_possibleWord);
            startGame.setArguments(data);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, startGame)
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
