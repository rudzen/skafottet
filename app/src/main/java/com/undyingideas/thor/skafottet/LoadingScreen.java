package com.undyingideas.thor.skafottet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return WordCollector.samlOrd();
            }

            @Override
            protected void onPostExecute(Object muligeOrd) {
                Intent StartApp = new Intent(LoadingScreen.this, MainActivity.class);
                StartApp.putExtra("muligeOrd", (ArrayList<String>) muligeOrd);
                startActivity(StartApp);
            }
        }.execute();


    }
}
