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

package com.undyingideas.thor.skafottet.support.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.highscore.local.HighscoreManager;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;

/**
 * Created on 04-01-2016, 18:47.
 * Project : skafottet
 *
 * @author rudz
 */
public final class GameUtility {

    public static Firebase firebase;

    public static TinyDB prefs;

    public static WordController wordController;

    public static SettingsDTO settings;

    private static PlayerDTO me;

    private static HighscoreManager highscoreManager;

    private static int connectionStatus;
    private static String connectionStatusName;

    private static boolean isLoggedIn;

    public static final int SFX_GUESS_WRONG = 0;
    public static final int SFX_GUESS_RIGHT = 1;
    public static final int SFX_INTRO = 2;
    public static final int SFX_MENU_CLICK = 3;
    public static final int SFX_WON = 4;
    public static final int SFX_LOST = 5;
    public static final int SFX_CHALLENGE = 6;

    public static @DrawableRes
    final int[] imageRefs = new int[8];

    private static Intent musicPLayIntent = new Intent();

    static {
        imageRefs[0] = R.drawable.terror0;
        imageRefs[1] = R.drawable.terror1;
        imageRefs[2] = R.drawable.terror2;
        imageRefs[3] = R.drawable.terror3;
        imageRefs[4] = R.drawable.terror4;
        imageRefs[5] = R.drawable.terror5;
        imageRefs[6] = R.drawable.terror6;
        imageRefs[7] = R.drawable.terror7;
    }

    public static Bitmap invert(final Context context, final int id) {
        final Bitmap bm = BitmapFactory.decodeResource(context.getResources(), imageRefs[id]);
        final Bitmap ret = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        final Canvas canvas = new Canvas(ret);

        final float[] m_Invert = {
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
        };

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final ColorMatrix cm = new ColorMatrix(m_Invert);

//        paint.setDither(true);
//        paint.setAntiAlias(true);

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bm, 0, 0, paint);

        return ret;
    }

    public static void writeNullGame() { getPrefs().remove(Constant.KEY_SAVE_GAME); }

    public static int getConnectionStatus() { return connectionStatus; }

    public static void setConnectionStatus(final int connectionStatus) {
        GameUtility.connectionStatus = connectionStatus;
    }

    public static String getConnectionStatusName() { return connectionStatusName; }

    public static void setConnectionStatusName(final String connectionStatusName) { GameUtility.connectionStatusName = connectionStatusName; }

    public static boolean isLoggedIn() { return isLoggedIn; }

    public static void setIsLoggedIn(final boolean isLoggedIn) { GameUtility.isLoggedIn = isLoggedIn; }

    public static Firebase getFirebase() {
        return firebase;
    }

    public static void setFirebase(Firebase firebase) {
        GameUtility.firebase = firebase;
    }

    public static PlayerDTO getMe() {
        return me;
    }

    public static void setMe(PlayerDTO me) {
        GameUtility.me = me;
    }

    public static TinyDB getPrefs() {
        return prefs;
    }

    public static void setPrefs(TinyDB prefs) {
        GameUtility.prefs = prefs;
    }

    public static SettingsDTO getSettings() {
        return settings;
    }

    public static void setSettings(SettingsDTO settings) {
        GameUtility.settings = settings;
    }

    public static HighscoreManager getHighscoreManager() {
        return highscoreManager;
    }

    public static void setHighscoreManager(HighscoreManager highscoreManager) {
        GameUtility.highscoreManager = highscoreManager;
    }

    public static Intent getMusicPLayIntent() {
        return musicPLayIntent;
    }

    public static void setMusicPLayIntent(Intent musicPLayIntent) {
        GameUtility.musicPLayIntent = musicPLayIntent;
    }

    public static WordController getWordController() {
        return wordController;
    }

    public static void setWordController(WordController wordController) {
        GameUtility.wordController = wordController;
    }
}
