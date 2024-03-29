/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.services.MusicPlay;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.Utils;
import com.undyingideas.thor.skafottet.support.firebase.controller.WordListController;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.highscore.local.HighscoreManager;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.FontUtils;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.NetworkHelper;
import com.undyingideas.thor.skafottet.support.utility.SettingsDTO;
import com.undyingideas.thor.skafottet.support.utility.TinyDB;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getConnectionStatus;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getFirebase;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getHighscoreManager;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getMe;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getPrefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getSettings;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getWordController;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setConnectionStatus;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setConnectionStatusName;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setFirebase;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setHighscoreManager;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setMusicPLayIntent;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setPrefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setSettings;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.setWordController;

/**
 * Created on 07-12-2015, 11:07.
 * Project : gunshow1
 * <p>
 * Simple and effective splash screen :)
 * </p>
 * Modified for Skafottet.
 *
 * @author rudz
 */
public class SplashActivity extends AppCompatActivity {

    private Animation mAnimation;
    private RelativeLayout mLogo;
    private TextView mTitleRight, mTitleLeft;
    private Handler mLoadHandler;
    private static final int MSG_LOAD_COMPLETE = 1;
    private static final int MSG_ANON_AUTH_COMPLETE = 2;
    private static final int MSG_USER_AUTH_COMPLETE = 3;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Mint key! */
//        Mint.initAndStartSession(this, "6adabf91");

//        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.setAndroidContext(getApplicationContext());

        setContentView(R.layout.activity_splash);

        mLoadHandler = new LoadHandler(this, Looper.getMainLooper());
        new Thread(new LoadConfig()).start();

        mLogo = (RelativeLayout) findViewById(R.id.splash_center_circle);
        mLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.step_number_fader));
        mTitleLeft = (TextView) findViewById(R.id.splash_text_left);
        mTitleRight = (TextView) findViewById(R.id.splash_text_right);

        if (savedInstanceState == null) {
            flyIn();
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
            final Display display = getWindowManager().getDefaultDisplay();
            display.getSize(WindowLayout.screenDimension);
        }
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    private void flyIn() {
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation);
        mTitleRight.startAnimation(mAnimation);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.pro_animation);
        mTitleLeft.startAnimation(mAnimation);
    }

    private void endSplash() {
        mLogo.setAnimation(null);
        // just use YoYo for the nice mAnimation :-)
        YoYo.with(Techniques.Pulse).duration(700).withListener(new SplashEndAnimatorListener(this)).playOn(mLogo);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation_back);
        mTitleRight.startAnimation(mAnimation);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.pro_animation_back);
        mTitleLeft.startAnimation(mAnimation);
    }

    private class LoadConfig implements Runnable {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            /* This is the first code executed, thus some configuration of the application takes place.. */

            // advarsel, dette er en test-zone.. alt hvad der reelt foregår her er udsat for underlige fremgangsmåder!..

            setPrefs(new TinyDB(getApplicationContext()));
//            getPrefs().clear();

            /* set the highscore manager */
            //HighscoreManager.deleteHighScore(getApplicationContext());
            setHighscoreManager(new HighscoreManager());
            getHighscoreManager().loadScoreFile(getApplicationContext());

            loadSettings();

            /* set the intent for the background music */
            setMusicPLayIntent(new Intent(getApplicationContext(), MusicPlay.class));

            setConnectionStatus(NetworkHelper.getConnectivityStatus(getApplicationContext()));
            setConnectionStatusName(NetworkHelper.getConnectivityStatusStringFromStatus(getConnectionStatus()));

            setFonts();

            // only for testing stuff!!!!
//            Log.d(TAG, "Wordlist deleted : " + ListFetcher.deleteList(getApplicationContext()));
            ListFetcher.setListHandler(new Handler(Looper.getMainLooper()));
            ListFetcher.setListSaver(new ListFetcher.ListSaver(getApplicationContext()));

            /* begin loading wordlist
             * 1) Check if any existing word list exist already
             * 2) If so, load the list and continue to next list loading.
             * 3) If not, reset the list to default build in list.
            */
            setWordController(ListFetcher.loadWordList(getBaseContext()));
            if (getWordController() == null || getWordController().getCurrentList() == null) {
                setWordController(new WordController(getResources().getStringArray(R.array.lande)));
                ListFetcher.getListHandler().post(ListFetcher.getListSaver());
//                 ListFetcher.saveWordLists(wordController, getApplicationContext());
            }

            try {
//                TinyDB.checkForNullKey(Constant.KEY_WORDS_FIREBASE);
                WordListController.setWordList((HashMap<String, WordItem>) getPrefs().getObject(Constant.KEY_WORDS_FIREBASE, Map.class));
            } catch (final Exception e) {
                Log.d(TAG, "Unable to load any remote cached word lists from preferences, log in to update.");
                if (!getWordController().isLocal()) {
                    getWordController().setIsLocal(true);
                    getWordController().setIndexLocale(0);
                }
            }

            Log.d(TAG, String.valueOf(getWordController().isLocal()));

//            wordController.setIndexLocale(prefs.getInt(Constant.KEY_WORDS_LIST_LOCAL_INDEX, 0));
//            wordController.setIsLocal(prefs.getBoolean(Constant.KEY_WORDS_IS_LIST_LOCAL));
//            wordController.setIndexRemote(prefs.getString(Constant.KEY_WORDS_LIST_FIREBASE_KEY));

            getWordController().setIsLocal(true);


            setFirebase(new Firebase(Constant.FIREBASE_URL));

            final String lastPw = getPrefs().getString(Constant.PASSWORD_LAST, null);
            final String lastUser = getPrefs().getString(Constant.USER_LAST, null);

            if (lastPw != null && lastUser != null) {
                Log.d(TAG, "Loggin in with u/p : " + lastUser + '/' + lastPw);
                getFirebase().authWithPassword(lastUser, lastPw, new StartupAuthResultHandler(true));
            } else {
                final Message message = mLoadHandler.obtainMessage(MSG_LOAD_COMPLETE);
                message.sendToTarget();
            }
        }

        private void loadSettings() {
            setSettings(new SettingsDTO());
            getSettings().prefsMusic = getPrefs().getBoolean(Constant.KEY_PREFS_MUSIC, true);
            getSettings().prefsSfx = getPrefs().getBoolean(Constant.KEY_PREFS_SFX, true);
            getSettings().prefsBlood = getPrefs().getBoolean(Constant.KEY_PREFS_BLOOD, true);
            getSettings().prefsHeptic = getPrefs().getBoolean(Constant.KEY_PREFS_HEPTIC, true);
            getSettings().keepLogin = getPrefs().getBoolean(Constant.KEY_PREFS_KEEP_LOGIN, false);
            getSettings().prefsColour = getPrefs().getInt(Constant.KEY_PREFS_COLOUR, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? ContextCompat.getColor(getApplicationContext(), R.color.colorAccent) : getResources().getColor(R.color.colorAccent));
            getSettings().setContrastColor();

            /* keep the preferences as we don't know if the user actually ran the app for the first time. */
            getPrefs().putBoolean(Constant.KEY_PREFS_BLOOD, getSettings().prefsBlood);
            getPrefs().putBoolean(Constant.KEY_PREFS_MUSIC, getSettings().prefsMusic);
            getPrefs().putBoolean(Constant.KEY_PREFS_SFX, getSettings().prefsSfx);
            getPrefs().putBoolean(Constant.KEY_PREFS_HEPTIC, getSettings().prefsHeptic);
            getPrefs().putInt(Constant.KEY_PREFS_COLOUR, getSettings().prefsColour);
        }

        private void setFonts() {
            FontUtils.setDefaultFont(getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
            FontUtils.setDefaultFont(getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
            FontUtils.setDefaultFont(getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
            FontUtils.setDefaultFont(getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);
        }


        private class StartupAuthResultHandler implements Firebase.AuthResultHandler {

            final boolean withPassword;

            public StartupAuthResultHandler(final boolean withPassword) {
                this.withPassword = withPassword;
            }

            @Override
            public void onAuthenticated(final AuthData authData) {
                if (authData != null) {
                    Log.d(TAG, (withPassword ? "Logged in with password as : " : "Logged in without password as : ") + authData.getUid());
                    setAuthenticatedUserPasswordProvider(authData);
                } else {
                    final Message message = mLoadHandler.obtainMessage(MSG_LOAD_COMPLETE);
                    message.sendToTarget();
                }
            }

            @Override
            public void onAuthenticationError(final FirebaseError firebaseError) {
                Log.d(TAG, (withPassword ? "Failed to log in with password : " : "Failed to Log in without password : ") + firebaseError.getMessage());
                getMe().setHasLoggedInWithPassword(false);
                final Message message = mLoadHandler.obtainMessage(MSG_LOAD_COMPLETE);
                message.sendToTarget();
            }

            private void setAuthenticatedUserPasswordProvider(final AuthData authData) {
                final String unprocessedEmail = authData.getProviderData().get(Constant.FIREBASE_PROPERTY_EMAIL).toString().toLowerCase();
                final String mEncodedEmail = Utils.encodeEmail(unprocessedEmail);
                final Firebase userRef = new Firebase(Constant.FIREBASE_URL_USERS).child(mEncodedEmail);

                /**
                 * Check if current user has logged in at least once
                 */
                userRef.addListenerForSingleValueEvent(new AppStartLoginValueEventListener());

            }

            private class AppStartLoginValueEventListener implements ValueEventListener {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Message message;
                    GameUtility.setMe(dataSnapshot.getValue(PlayerDTO.class));
                    message = mLoadHandler.obtainMessage(GameUtility.getMe() != null ? MSG_USER_AUTH_COMPLETE : MSG_LOAD_COMPLETE);
                    message.sendToTarget();
                }

                @Override
                public void onCancelled(final FirebaseError firebaseError) {
                    Log.e(TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
                    final Message message = mLoadHandler.obtainMessage(MSG_LOAD_COMPLETE);
                    message.sendToTarget();
                }
            }
        }
    }

    private static class LoadHandler extends Handler {

        private final WeakReference<SplashActivity> splashActivityWeakReference;

        public LoadHandler(final SplashActivity splashActivity, final Looper looper) {
            super(looper);
            splashActivityWeakReference = new WeakReference<>(splashActivity);
        }

        @SuppressWarnings("AnonymousInnerClass")
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (msg.what == MSG_LOAD_COMPLETE) {
                    /* no connection was availble, so we just proceed with singleplayer mode. */
                    GameUtility.setIsLoggedIn(false);
                    GameUtility.setMe(new PlayerDTO(GameUtility.getPrefs().getString(Constant.KEY_PREFS_PLAYER_NAME, "Mig")));


//                } else if (msg.what == MSG_ANON_AUTH_COMPLETE) {
//                    /* user has read access to firebase (not able to play multiplayer) */
//                    getSettings().auth_status = SettingsDTO.AUTH_ANON;
                } else if (msg.what == MSG_USER_AUTH_COMPLETE) {
                    /* user has access to the full features of the game */
                    GameUtility.setIsLoggedIn(true);
                }
                final SplashActivity splashActivity = splashActivityWeakReference.get();
                if (splashActivity != null) {
                    splashActivity.mLoadHandler.postDelayed(new EndSplash(splashActivity), 1000);
                }
            }
        }

        private static class EndSplash implements Runnable {
            private final SplashActivity splashActivity;

            public EndSplash(final SplashActivity splashActivity) {this.splashActivity = splashActivity;}

            @Override
            public void run() {
                splashActivity.endSplash();
            }
        }
    }

    /**
     * Class to execute code when a specific mAnimation ends.
     */
    private static class SplashEndAnimatorListener extends WeakReferenceHolder<SplashActivity> implements Animator.AnimatorListener {

        public SplashEndAnimatorListener(final SplashActivity splashActivity) {
            super(splashActivity);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final SplashActivity splashActivity = mWeakReference.get();
            if (splashActivity != null) {
                final Intent intent = new Intent(splashActivity, GameActivity.class);
                final Bundle bundle;
                bundle = new Bundle();
                bundle.putInt(Constant.KEY_MODE, Constant.MODE_MENU);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.KEY_MODE, bundle);
                splashActivity.startActivity(intent);
                splashActivity.finish();
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

}

