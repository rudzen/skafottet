package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.undyingideas.thor.skafottet.SupportClasses.WordCollector;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.FontsOverride;
import com.undyingideas.thor.skafottet.utility.TinyDB;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.utility.GameUtility.prefs;

/**
 * preliminary screen and process, that makes sure that the game is ready to start, by downloading
 * or fetching words from the cache
 */
public class LoadingScreen extends Activity {
    //private SharedPreferences prefs;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        new LoadWords(this).execute();
    }

    private static class LoadWords extends AsyncTask<Void, Integer, ArrayList<String>> {

        private final WeakReference<LoadingScreen> loadingScreenWeakReference;

        public LoadWords(final LoadingScreen loadingScreen) {
            loadingScreenWeakReference = new WeakReference<>(loadingScreen);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            final LoadingScreen loadingScreen = loadingScreenWeakReference.get();
            if (loadingScreen != null) {
                /* This is the first code executed, thus some configuration of the application takes place.. */
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "DEFAULT", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "MONOSPACE", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "SERIF", Constant.DEF_FONT);
                FontsOverride.setDefaultFont(loadingScreen.getApplicationContext(), "SANS_SERIF", Constant.DEF_FONT);
                //noinspection AssignmentToStaticFieldFromInstanceMethod
                prefs = new TinyDB(loadingScreen.getApplicationContext());

                /* begin loading wordlist */
                final ArrayList<String> muligeOrd = new ArrayList<>();
                final HashSet<String> data = new HashSet<>();
                try {
                    muligeOrd.addAll(WordCollector.samlOrd());
                    data.addAll(muligeOrd);
                    Log.d("LoadingScreen", "length:" + data.size());
                    prefs.putObject("possibleWords", data);
                    //prefs.edit().putStringSet("possibleWords", data).apply();//creates a cache for later use
                } catch (final Exception e) {
                    muligeOrd.addAll((HashSet<String>) prefs.getObject("possibleWords", HashSet.class)); // prefs.getStringSet("possibleWords", null));
                    if (muligeOrd.size() <= 1) muligeOrd.add(0, "hej"); // LOLZ
                }
                Log.d("LoadingScreen", "dataLength:" + muligeOrd.size());
                return muligeOrd;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> possibleWords) {
            super.onPostExecute(possibleWords);
            LoadingScreen loadingScreen = loadingScreenWeakReference.get();
            if (possibleWords != null && loadingScreen != null) { // med seler og livrem
                final Intent StartApp = new Intent(loadingScreen, MenuActivity.class);
                StartApp.putExtra("muligeOrd", possibleWords);
                StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
                loadingScreen.startActivity(StartApp);
                try {
                    prefs.checkForNullKey("possibleWords");
                    Log.d("cache", "contains " + prefs.getObject("possibleWords", HashSet.class)); //checkForNullKey(); prefs.contains("possibleWords"));
                } catch (NullPointerException e) {

                }
            }
        }

        //        @Override
//        protected Object doInBackground(final Object[] params) {
//            ArrayList<String> muligeOrd = new ArrayList<>();
//            final HashSet<String> data = new HashSet<>();
//            try {
//                muligeOrd = WordCollector.samlOrd();
//                data.addAll(muligeOrd);
//                Log.d("LoadingScreen", "length:" + data.size());
//                prefs.edit().putStringSet("possibleWords", data).apply();//creates a cache for later use
//
//            } catch (final Exception e){
//                muligeOrd.addAll(prefs.getStringSet("possibleWords", null));
//                if(muligeOrd.size() <= 1) muligeOrd.add(0, "hej");
//
//            }
//            Log.d("LoadingScreen", "dataLength:" + muligeOrd.size());
//            return muligeOrd;
//}
//
//        @Override
//        protected void onPostExecute(final Object possibleWords) {
//
//            final Intent StartApp = new Intent(LoadingScreen.this, MenuActivity.class);
////            final Intent StartApp = new Intent(LoadingScreen.this, FragmentMainActivity.class);
//
//            StartApp.putExtra("muligeOrd", (ArrayList<String>) possibleWords);
//            StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
//            startActivity(StartApp);
//            Log.d("cache", "contains " + prefs.contains("possibleWords"));
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            return null;
//        }
    }

}
