package com.undyingideas.thor.skafottet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Set;

public class LoadingScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        final SharedPreferences ord = PreferenceManager.getDefaultSharedPreferences(this);

        new LoadWords().execute();

        finish();

    }

    private class LoadWords extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<String> muligeOrd;
            try {
                muligeOrd = WordCollector.samlOrd();

                return muligeOrd;
            } catch (Exception e){
                return null;
            }
        }
        @Override
        protected void onPostExecute(Object muligeOrd) {

            Intent StartApp = new Intent(LoadingScreen.this, MainActivity.class);

            StartApp.putExtra("muligeOrd", (ArrayList<String>) muligeOrd);
            startActivity(StartApp);
        }
    }

}
