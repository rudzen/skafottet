package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.Firebase;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.TinyDB;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;

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
        ListFetcher.listSaver = new ListFetcher.ListSaver(getApplicationContext());
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

        @Override
        protected void onPreExecute() {
        }

        @SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod", "unchecked", "OverlyLongMethod"})
        @Override
        protected Boolean doInBackground(final Void... params) {
            final LoadingActivity loadingActivity = loadingScreenWeakReference.get();
            if (loadingActivity != null) {


                /* This is the first code executed, thus some configuration of the application takes place.. */
                setDefaultFont(loadingActivity.getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
                setDefaultFont(loadingActivity.getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
                setDefaultFont(loadingActivity.getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
                setDefaultFont(loadingActivity.getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);

                if (s_prefereces == null) {
                    s_prefereces = new TinyDB(loadingActivity.getApplicationContext());
                }

                // only for testing stuff!!!!
//                s_prefereces.clear();

                /* begin loading wordlist
                * 1) Check if any existing word list exist already
                * 2) If so, load the list and continue to next list loading.
                * 3) If not, reset the list to default build in list.
                */
                s_wordController = ListFetcher.loadWordList(loadingActivity.getApplicationContext());
                if (s_wordController == null || s_wordController.getCurrentList() == null) {
                    s_wordController = new WordController(loadingActivity.getResources().getStringArray(R.array.countries));
                    ListFetcher.listHandler.post(ListFetcher.listSaver);
//                    ListFetcher.saveWordLists(s_wordController, loadingActivity.getApplicationContext());
                }



                try {
                    s_prefereces.checkForNullValue(Constant.KEY_WORDS_FIREBASE);
                    WordListController.wordList = (HashMap<String, WordItem>) s_prefereces.getObject(Constant.KEY_WORDS_FIREBASE, HashMap.class);
                } catch (final Exception e) {
                    Log.d(TAG, "Unable to load any remote cached word lists from preferences, log in to update.");
                    if (!s_wordController.isLocal()) {
                        s_wordController.setIsLocal(true);
                        s_wordController.setIndexLocale(0);
                    }
                }

                Log.d(TAG, String.valueOf(s_wordController.isLocal()));

                s_wordController.setIndexLocale(s_prefereces.getInt(Constant.KEY_WORDS_LIST_LOCAL_INDEX, 0));
                s_wordController.setIsLocal(s_prefereces.getBoolean(Constant.KEY_WORDS_IS_LIST_LOCAL));
                s_wordController.setIndexRemote(s_prefereces.getString(Constant.KEY_WORDS_LIST_FIREBASE_KEY));

                s_wordController.setIsLocal(true);

                Firebase.setAndroidContext(loadingActivity.getApplicationContext());
                GameUtility.firebase = new Firebase(Constant.HANGMANDTU_FIREBASEIO);
                GameUtility.mpc = new MultiplayerController(GameUtility.firebase);

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


                if (s_wordController.isLocal() && WordListController.wordList == null) {
                    s_wordController.setIsLocal(true);
                }
                final Intent intent = new Intent(loadingActivity, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                YoYo.with(Techniques.RollOut).duration(300).playOn(loadingActivity.findViewById(R.id.loading_screen_text));
                YoYo.with(Techniques.RotateOut).duration(900).withListener(new EndAnimatorListener(loadingActivity)).playOn(loadingActivity.findViewById(R.id.loading_screen_image));
//                loadingActivity.overridePendingTransition(anim., android.R.anim.fade_out);
                loadingActivity.startActivity(intent);
            }
        }

        private static class EndAnimatorListener implements Animator.AnimatorListener {

            private final WeakReference<LoadingActivity> loadingActivityWeakReference;

            public EndAnimatorListener(final LoadingActivity loadingActivity) {
                loadingActivityWeakReference = new WeakReference<>(loadingActivity);
            }

            @Override
            public void onAnimationStart(final Animator animation) { }

            @Override
            public void onAnimationEnd(final Animator animation) {
                final LoadingActivity loadingActivity = loadingActivityWeakReference.get();
                if (loadingActivity != null) {
                    loadingActivity.finish();
                }
            }

            @Override
            public void onAnimationCancel(final Animator animation) { }

            @Override
            public void onAnimationRepeat(final Animator animation) { }
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
