package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import com.undyingideas.thor.skafottet.SupportClasses.WordCollector;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * preliminary screen and process, that makes sure that the game is ready to start, by downloading
 * or fetching words from the cache
 */
public class LoadingScreen extends Activity {
     private SharedPreferences prefs;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        new LoadWords().execute();
    }

    private class LoadWords extends AsyncTask{
        @Override
        protected Object doInBackground(final Object[] params) {
            ArrayList<String> muligeOrd = new ArrayList<>();
            final HashSet<String> data = new HashSet<>();
            try {
                muligeOrd = WordCollector.samlOrd();
                data.addAll(muligeOrd);
                Log.d("LoadingScreen", "length:" + data.size());
                prefs.edit().putStringSet("possibleWords", data).apply();//creates a cache for later use

            } catch (final Exception e){
                muligeOrd.addAll(prefs.getStringSet("possibleWords", null));
                if(muligeOrd.size() <= 1) muligeOrd.add(0, "hej");

            }
            Log.d("LoadingScreen", "dataLength:" + muligeOrd.size());
            return muligeOrd;
}

        @Override
        protected void onPostExecute(final Object possibleWords) {

            final Intent StartApp = new Intent(LoadingScreen.this, FragmentMainActivity.class);

            StartApp.putExtra("muligeOrd", (ArrayList<String>) possibleWords);
            StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
            startActivity(StartApp);
            Log.d("cache", "contains " + prefs.contains("possibleWords"));
        }
    }

}
