package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.SupportClasses.WordCollector;
import com.undyingideas.thor.skafottet.game_ui.main_menu.MenuActivity;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.TinyDB;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.utility.GameUtility.s_prefereces;

/**
 * preliminary screen and process, that makes sure that the game is ready to start, by downloading
 * or fetching words from the cache
 */
public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
//        s_prefereces = PreferenceManager.getDefaultSharedPreferences(this);

        new LoadWords(this).execute();
    }

    private static class LoadWords extends AsyncTask<Void, Integer, ArrayList<String>> {

        private final WeakReference<LoadingActivity> loadingScreenWeakReference;

        public LoadWords(final LoadingActivity loadingActivity) {
            loadingScreenWeakReference = new WeakReference<>(loadingActivity);
        }

        private static void setDefaultFont(final Context context, final String staticTypefaceFieldName, final String fontAssetName) {
            replaceFont(staticTypefaceFieldName, Typeface.createFromAsset(context.getAssets(), fontAssetName));
        }

        private static void replaceFont(final String staticTypefaceFieldName, final Typeface newTypeface) {
            try {
                final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(final Void... params) {
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (loadingActivity != null) {
                /* This is the first code executed, thus some configuration of the application takes place.. */
                WindowLayout.setScreenDimension(loadingActivity);


                setDefaultFont(loadingActivity.getApplicationContext(), "DEFAULT", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "MONOSPACE", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SERIF", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SANS_SERIF", Constant.DEF_FONT);
                //noinspection AssignmentToStaticFieldFromInstanceMethod
                s_prefereces = new TinyDB(loadingActivity.getApplicationContext());

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
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (possibleWords != null && loadingActivity != null) { // med seler og livrem
                final Intent StartApp = new Intent(loadingActivity, MenuActivity.class);
                s_prefereces.putListString(GameUtility.KEY_MULIGE_ORD, possibleWords);
//                StartApp.putExtra(GameUtility.KEY_MULIGE_ORD, possibleWords);
                StartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// Fjerner loadscreen fra stacken
                loadingActivity.startActivity(StartApp);
                try {
                    TinyDB.checkForNullKey(Constant.KEY_PREF_POSSIBLE_WORDS);
                    Log.d("cache", "contains " + s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class)); //checkForNullKey(); s_prefereces.contains("possibleWords"));
                } catch (final NullPointerException e) {
                    // nada here atm.
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }

    }

}
