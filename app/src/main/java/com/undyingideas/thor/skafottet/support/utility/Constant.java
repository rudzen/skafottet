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

/**
 * Created on 04-01-2016, 20:12.
 * Project : skafottet
 *
 * @author rudz
 */
public final class Constant {

    public static final String FONT_BOLD = "fonts/Roboto-Bold.ttf";
    public static final String FONT_LIGHT = "fonts/Roboto-Light.ttf";
    public static final String FONT_SERIF = "fonts/deathrattlebb_reg.ttf";

    /* preferences keys */
    public static final String KEY_WORDS_IS_LIST_LOCAL = "kwll";
    public static final String KEY_WORDS_LIST_FIREBASE_KEY = "kwlfbk";
    public static final String KEY_WORDS_LIST_LOCAL_INDEX = "kwlli";
    public static final String KEY_WORDS_LOCAL = "lcw";
    public static final String KEY_WORDS_FIREBASE = "fbw";

    public static final String KEY_PREFS_MUSIC = "kpmusic";
    public static final String KEY_PREFS_SFX   = "kpsfx";
    public static final String KEY_PREFS_BLOOD = "kpblood";

    /* instance keys */

    /* game mode keys */
    public static final String KEY_MODE = "mode";

    public static final int MODE_FINISH = -100;
    public static final int MODE_MENU = -1;
    public static final int MODE_CONT_GAME = 0;
    public static final int MODE_SINGLE_PLAYER = 1;
    public static final int MODE_MULTI_PLAYER = 2;
    public static final int MODE_MULTI_PLAYER_2 = 3;
    public static final int MODE_MULTI_PLAYER_LOBBY = 4;
    public static final int MODE_MULTI_PLAYER_LOGIN = 5;
    public static final int MODE_MULTI_PLAYER_WORD_SELECT = 6;
    public static final int MODE_ABOUT = 8;
    public static final int MODE_HELP = 9;
    public static final int MODE_END_GAME = 10;


    /* to deliver as true back pressed */
    public static final int MODE_BACK_PRESSED = 231;


    /* activities, going to be replaced */
    public static final int MODE_WORD_LIST = 7;

    /* perhaps one day this will become a fragment */
    public static final int MODE_HIGHSCORE = 101;

    public static final String KEY_GAME_LOGIC = "log";
    public static final String KEY_PREF_HELP = "hlp";

    /* firebase constants */
    public static final String FIREBASE_MULTI_PLAYER = "MultiPlayer";
    public static final String HANGMANDTU_FIREBASEIO = "https://hangmandtu.firebaseio.com/";


    /* save game keys */
    public static final String KEY_SAVE_GAME = "svg";



}
