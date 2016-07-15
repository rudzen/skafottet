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

package com.undyingideas.thor.skafottet.support.utility;

import com.undyingideas.thor.skafottet.BuildConfig;

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
    public static final String KEY_PREFS_HEPTIC = "kphep";

    public static final String KEY_PREFS_COLOUR = "kpcol";

    public static final String KEY_PREFS_PLAYER_NAME = "kpplyname";

    public static final String KEY_PREFS_KEEP_LOGIN = "kploginlast";

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
    public static final int MODE_SETTINGS = 11;
    public static final int MODE_LOGIN = 12;
    public static final int MODE_CREATE_ACCOUNT = 13;
    public static final int MODE_LOGOUT = 14;

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



    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_SHOPPING_LIST_ITEMS = "shoppingListItems";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_LISTS = "userLists";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_LISTS_SHARED_WITH = "sharedWith";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_OWNER_MAPPINGS = "ownerMappings";



    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_BOUGHT = "bought";
    public static final String FIREBASE_PROPERTY_BOUGHT_BY = "boughtBy";
    public static final String FIREBASE_PROPERTY_LIST_NAME = "listName";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_ITEM_NAME = "itemName";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USERS_SHOPPING = "usersShopping";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_SHOPPING_LIST_ITEMS = FIREBASE_URL + '/' + FIREBASE_LOCATION_SHOPPING_LIST_ITEMS;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + '/' + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_USER_LISTS = FIREBASE_URL + '/' + FIREBASE_LOCATION_USER_LISTS;
    public static final String FIREBASE_URL_USER_FRIENDS = FIREBASE_URL + '/' + FIREBASE_LOCATION_USER_FRIENDS;
    public static final String FIREBASE_URL_LISTS_SHARED_WITH = FIREBASE_URL + '/' + FIREBASE_LOCATION_LISTS_SHARED_WITH;


    /* Constants for Firebase login */
    public static final String PASSWORD_PROVIDER = "pw";
    public static final String GOOGLE_PROVIDER = "gog";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
    public static final String PASSWORD_LAST = "last_password";
    public static final String USER_LAST = "user_last";

    /* Constants for bundles, extras and shared preferences keys */
    public static final String KEY_LIST_NAME = "LIST_NAME";
    public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
    public static final String KEY_LIST_ID = "LIST_ID";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_LIST_ITEM_NAME = "ITEM_NAME";
    public static final String KEY_LIST_ITEM_ID = "LIST_ITEM_ID";
    public static final String KEY_PROVIDER = "PROVIDER";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_LIST_OWNER = "LIST_OWNER";
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";
    public static final String KEY_PREF_SORT_ORDER_LISTS = "PERF_SORT_ORDER_LISTS";
    public static final String KEY_SHARED_WITH_USERS = "SHARED_WITH_USERS";




}
