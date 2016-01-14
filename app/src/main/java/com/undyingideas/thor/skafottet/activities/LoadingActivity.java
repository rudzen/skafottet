package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.TinyDB;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_prefereces;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_wordController;

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

    @SuppressWarnings("AccessStaticViaInstance")
    private static class LoadWords extends AsyncTask<Void, Integer, Boolean> {

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

        @SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod", "unchecked", "OverlyLongMethod"})
        @Override
        protected Boolean doInBackground(final Void... params) {
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (loadingActivity != null) {
                Firebase.setAndroidContext(loadingActivity.getApplicationContext());
                GameUtility.firebase = new Firebase(Constant.HANGMANDTU_FIREBASEIO);
                GameUtility.mpc = new MultiplayerController(GameUtility.firebase);

                /* This is the first code executed, thus some configuration of the application takes place.. */
                setDefaultFont(loadingActivity.getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
                setDefaultFont(loadingActivity.getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
                setDefaultFont(loadingActivity.getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);

                if (s_prefereces == null) {
                    s_prefereces = new TinyDB(loadingActivity.getApplicationContext());
                }

                s_prefereces.clear();

                /* begin loading wordlist
                * 1) Check if any existing word list exist in preferences.
                * 2) If so, load the list and continue to next list loading.
                * 3) If not, reset the list to default build in list.
                */

                s_wordController = ListFetcher.loadWordList(loadingActivity.getApplicationContext());
                if (s_wordController == null) {
                    s_wordController = new WordController(loadingActivity.getResources().getStringArray(R.array.countries));
                    ListFetcher.saveWordLists(s_wordController, loadingActivity.getApplicationContext());
                }

                Log.d(TAG, String.valueOf(s_wordController.isLocal()));

                s_wordController.setCurrentLocalList(s_prefereces.getInt(Constant.KEY_WORDS_LIST_LOCAL_INDEX, 0));
                s_wordController.setIsLocal(s_prefereces.getBoolean(Constant.KEY_WORDS_IS_LIST_LOCAL));
                s_wordController.setIndexRemote(s_prefereces.getString(Constant.KEY_WORDS_LIST_FIREBASE_KEY));

                Log.d(TAG, s_wordController.toString());
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (loadingActivity != null) {
                final Intent intent = new Intent(loadingActivity, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                loadingActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                loadingActivity.startActivity(intent);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setScreenDimension(this);
            WindowLayout.setImmersiveMode(getWindow());
        }
    }

    private static void setScreenDimension(final AppCompatActivity appCompatActivity) {
        final Display display = appCompatActivity.getWindowManager().getDefaultDisplay();
        display.getSize(WindowLayout.screenDimension);
    }
}
