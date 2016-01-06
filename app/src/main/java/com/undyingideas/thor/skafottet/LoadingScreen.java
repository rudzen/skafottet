package com.undyingideas.thor.skafottet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.undyingideas.thor.skafottet.SupportClasses.WordCollector;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.FontsOverride;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.TinyDB;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.utility.GameUtility.s_prefereces;

/**
 * preliminary screen and process, that makes sure that the game is ready to start, by downloading
 * or fetching words from the cache
 */
public class LoadingScreen extends AppCompatActivity {

    private static final String TAG = "LoadingScreen";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
//        s_prefereces = PreferenceManager.getDefaultSharedPreferences(this);

        new LoadWords(this).execute();
    }

    private static class LoadWords extends AsyncTask<Void, Integer, ArrayList<String>> {

        private final WeakReference<LoadingScreen> loadingScreenWeakReference;

        public LoadWords(final LoadingScreen loadingScreen) {
            loadingScreenWeakReference = new WeakReference<>(loadingScreen);
        }

        @Override
        protected ArrayList<String> doInBackground(final Void... params) {
            final LoadingScreen loadingScreen = loadingScreenWeakReference.get();
            if (loadingScreen != null) {
                /* This is the first code executed, thus some configuration of the application takes place.. */
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "DEFAULT", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "MONOSPACE", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "SERIF", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "SANS_SERIF", Constant.DEF_FONT);
                //noinspection AssignmentToStaticFieldFromInstanceMethod
                s_prefereces = new TinyDB(loadingScreen.getApplicationContext());

                /* begin loading wordlist */
                final ArrayList<String> muligeOrd = new ArrayList<>();
                final HashSet<String> data = new HashSet<>();
                try {
                    muligeOrd.addAll(WordCollector.samlOrd());
                    Collections.sort(muligeOrd);
                    data.addAll(muligeOrd);
                    Log.d(TAG, "length:" + data.size());
                    s_prefereces.putObject(Constant.KEY_PREF_POSSIBLE_WORDS, data);
                } catch (final Exception e) {
                    //noinspection unchecked
                    muligeOrd.addAll((HashSet<String>) s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
                    if (muligeOrd.size() <= 1) muligeOrd.add(0, "hej"); // LOLZ
                }
                Log.d(TAG, "dataLength:" + muligeOrd.size());
                return muligeOrd;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> possibleWords) {
            super.onPostExecute(possibleWords);
            final LoadingScreen loadingScreen = loadingScreenWeakReference.get();
            if (possibleWords != null && loadingScreen != null) { // med seler og livrem
                final Intent StartApp = new Intent(loadingScreen, MenuActivity.class);
                StartApp.putExtra(GameUtility.KEY_MULIGE_ORD, possibleWords);
                StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
                loadingScreen.startActivity(StartApp);
                try {
                    TinyDB.checkForNullKey(Constant.KEY_PREF_POSSIBLE_WORDS);
                    Log.d("cache", "contains " + s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class)); //checkForNullKey(); s_prefereces.contains("possibleWords"));
                } catch (final NullPointerException e) {
                    // nada here atm.
                }
            }
        }
    }

}
