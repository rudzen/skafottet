package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.TinyDB;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.utility.WordCollector;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_prefereces;

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

        @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
        @Override
        protected ArrayList<String> doInBackground(final Void... params) {
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (loadingActivity != null) {
                Firebase.setAndroidContext(loadingActivity.getApplicationContext());
                GameUtility.fb = new Firebase(Constant.HANGMANDTU_FIREBASEIO);
                GameUtility.mpc = new MultiplayerController(GameUtility.fb);

                /* This is the first code executed, thus some configuration of the application takes place.. */
                setDefaultFont(loadingActivity.getApplicationContext(), "DEFAULT", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "MONOSPACE", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SERIF", Constant.DEF_FONT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SANS_SERIF", Constant.DEF_FONT);

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
                s_prefereces.putListString(GameUtility.KEY_MULIGE_ORD, possibleWords);
                final Intent intent = new Intent(loadingActivity, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                loadingActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                loadingActivity.startActivity(intent);
                try {
                    TinyDB.checkForNullKey(Constant.KEY_PREF_POSSIBLE_WORDS);
                    Log.d("cache", "contains " + s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
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
            WindowLayout.setScreenDimension(this);
            WindowLayout.setImmersiveMode(getWindow());
        }

    }
}
