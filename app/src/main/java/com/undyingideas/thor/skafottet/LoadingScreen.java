package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * preliminary screen and process, that makes sure that the game is ready to start, by downloading
 * or fetching words from the cache
 */
public class LoadingScreen extends Activity {
     SharedPreferences ord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        ord = PreferenceManager.getDefaultSharedPreferences(this);
        new LoadWords().execute();
    }

    private class LoadWords extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<String> muligeOrd = new ArrayList<>();
            HashSet<String> data = new HashSet<>();
            try {
                muligeOrd = WordCollector.samlOrd();
                data.addAll(muligeOrd);
                ord.edit().putStringSet("possibleWords", data).apply();//creates a cache for later use
                return muligeOrd;
            } catch (Exception e){
                muligeOrd.addAll(ord.getStringSet("possibleWords", new HashSet<String>()));
                return muligeOrd;
            }
        }

        @Override
        protected void onPostExecute(Object possibleWords) {

            Intent StartApp = new Intent(LoadingScreen.this, MainActivity.class);

            StartApp.putExtra("muligeOrd", (ArrayList<String>) possibleWords);
            StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
            startActivity(StartApp);
            Log.d("cache", "contains " + ord.contains("possibleWords"));
        }
    }

}
