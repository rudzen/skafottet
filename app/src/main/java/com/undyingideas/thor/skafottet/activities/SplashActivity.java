/*
 * Copyright 2016 Rudy Alex Kohn
 *
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.Firebase;
import com.nineoldandroids.animation.Animator;
import com.splunk.mint.Mint;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.services.MusicPlay;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.firebase.controller.WordListController;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.FontUtils;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.NetworkHelper;
import com.undyingideas.thor.skafottet.support.utility.TinyDB;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.firebase;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_preferences;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_wordController;

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

    private Animation animation;
    private RelativeLayout logo;
    private TextView title1, title2;
    private Handler loadHandler;
    private static final int MSG_LOAD_COMPLETE = 1;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Mint key! */
        Mint.initAndStartSession(this, "6adabf91");

        setContentView(R.layout.activity_splash);

        loadHandler = new LoadHandler(this);
        loadHandler.post(new LoadConfig());

        logo = (RelativeLayout) findViewById(R.id.splash_center_circle);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.step_number_fader));
        title2 = (TextView) findViewById(R.id.splash_text_left);
        title1 = (TextView) findViewById(R.id.splash_text_right);

        if (savedInstanceState == null) flyIn();
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
        animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation);
        title1.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.pro_animation);
        title2.startAnimation(animation);
    }

    private void endSplash() {
        logo.setAnimation(null);
        // just use YoYo for the nice animation :-)
        YoYo.with(Techniques.Pulse).duration(700).withListener(new SplashEndAnimatorListener(this)).playOn(logo);

        animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation_back);
        title1.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.pro_animation_back);
        title2.startAnimation(animation);
    }

    private class LoadConfig implements Runnable {

        @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
        @Override
        public void run() {
            /* This is the first code executed, thus some configuration of the application takes place.. */

            // advarsel, dette er en test-zone.. alt hvad der reelt foregår her er udsat for underlige fremgangsmåder!..

            if (s_preferences == null) {
                s_preferences = new TinyDB(getApplicationContext());
            }

            MusicPlay.intent = new Intent(getApplicationContext(), MusicPlay.class);
            MusicPlay.intent.setAction("SKAFOTMUSIK");
            startService(MusicPlay.intent);

//            if (SettingsDTO.PREFS_MUSIC) {
//                startService(MusicPlay.intent);
//            }


            GameUtility.connectionStatus = NetworkHelper.getConnectivityStatus(getApplicationContext());
            GameUtility.connectionStatusName = NetworkHelper.getConnectivityStatusStringFromStatus(GameUtility.connectionStatus);

            FontUtils.setDefaultFont(getApplicationContext(), "DEFAULT", Constant.FONT_BOLD);
            FontUtils.setDefaultFont(getApplicationContext(), "MONOSPACE", Constant.FONT_BOLD);
            FontUtils.setDefaultFont(getApplicationContext(), "SERIF", Constant.FONT_LIGHT);
            FontUtils.setDefaultFont(getApplicationContext(), "SANS_SERIF", Constant.FONT_BOLD);



            // only for testing stuff!!!!
//            s_preferences.clear();
//            Log.d(TAG, "Wordlist deleted : " + ListFetcher.deleteList(getApplicationContext()));
            ListFetcher.listHandler = new Handler();
            ListFetcher.listSaver = new ListFetcher.ListSaver(getApplicationContext());

            /* begin loading wordlist
             * 1) Check if any existing word list exist already
             * 2) If so, load the list and continue to next list loading.
             * 3) If not, reset the list to default build in list.
            */
            s_wordController = ListFetcher.loadWordList(getBaseContext());
            if (s_wordController == null || s_wordController.getCurrentList() == null) {
                s_wordController = new WordController(getResources().getStringArray(R.array.lande));
                ListFetcher.listHandler.post(ListFetcher.listSaver);
//                 ListFetcher.saveWordLists(s_wordController, getApplicationContext());
            }

            try {
//                TinyDB.checkForNullKey(Constant.KEY_WORDS_FIREBASE);
                WordListController.wordList = (HashMap<String, WordItem>) s_preferences.getObject(Constant.KEY_WORDS_FIREBASE, HashMap.class);
            } catch (final Exception e) {
                Log.d(TAG, "Unable to load any remote cached word lists from preferences, log in to update.");
                if (!s_wordController.isLocal()) {
                    s_wordController.setIsLocal(true);
                    s_wordController.setIndexLocale(0);
                }
            }

            Log.d(TAG, String.valueOf(s_wordController.isLocal()));

//            s_wordController.setIndexLocale(s_preferences.getInt(Constant.KEY_WORDS_LIST_LOCAL_INDEX, 0));
//            s_wordController.setIsLocal(s_preferences.getBoolean(Constant.KEY_WORDS_IS_LIST_LOCAL));
//            s_wordController.setIndexRemote(s_preferences.getString(Constant.KEY_WORDS_LIST_FIREBASE_KEY));

            s_wordController.setIsLocal(true);

            Firebase.setAndroidContext(getApplicationContext());
            GameUtility.firebase = new Firebase(Constant.HANGMANDTU_FIREBASEIO);
            firebase.keepSynced(true);
//            GameUtility.firebase.keepSynced(false);
            GameUtility.mpc = new MultiplayerController(GameUtility.firebase);


            Log.d(TAG, s_wordController.toString());

            final Message message = loadHandler.obtainMessage(MSG_LOAD_COMPLETE);
            message.sendToTarget();
        }
    }

    private static class LoadHandler extends Handler {

        private final WeakReference<SplashActivity> splashActivityWeakReference;

        public LoadHandler(final SplashActivity splashActivity) {
            splashActivityWeakReference = new WeakReference<>(splashActivity);
        }

        @SuppressWarnings("AnonymousInnerClass")
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.what == MSG_LOAD_COMPLETE) {
                final SplashActivity splashActivity = splashActivityWeakReference.get();
                if (splashActivity != null) {
                    splashActivity.loadHandler.postDelayed(new EndSplash(splashActivity), 1000);
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
     * Class to execute code when a specific animation ends.
     */
    private static class SplashEndAnimatorListener implements Animator.AnimatorListener {

        private final WeakReference<SplashActivity> splashActivityWeakReference;

        public SplashEndAnimatorListener(final SplashActivity splashActivity) {
            splashActivityWeakReference = new WeakReference<>(splashActivity);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final SplashActivity splashActivity = splashActivityWeakReference.get();
            if (splashActivity != null) {
                final Intent intent = new Intent(splashActivity, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

