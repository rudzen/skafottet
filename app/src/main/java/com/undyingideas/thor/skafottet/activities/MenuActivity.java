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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.StartGameAdapter;
import com.undyingideas.thor.skafottet.adapters.StartGameItem;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetReciever;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetRecieverData;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.services.MusicPlay;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.SettingsDTO;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import net.steamcrafted.loadtoast.LoadToast;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.imageRefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.mpc;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_preferences;

/**
 * The main menu activity.<br>
 * Quite large, but contains only relevant code.<br>
 * The Class metric is high here, but this is just a quick hack to make the SoundPool work.<br>
 * The refactoring of this class is imminent.<br>
 * <li>It's TOO big.</li>
 * <li>Has too many responsibilities</li>
 * <li>Difficult to figure out what's going on where! (luckily search works!)</li>
 * <p>Animation support classes needs to be generalised</p>
 * <p>Private classes should use abstraction to save a lot of valuable space</p>
 * @author rudz
 */
@SuppressWarnings({"ClassTooDeepInInheritanceTree", "ClassWithTooManyMethods"})
public class MenuActivity extends MenuActivityAbstract implements
        InternetRecieverData.InternetRecieverInterface,
        SharedPreferences.OnSharedPreferenceChangeListener

{

    private static final String FINISH = "finish";
    private static final int BACK_PRESSED_DELAY = 2000;

    private static final int BUTTON_COUNT = 8;
    private ImageView title;
    private final ImageView[] buttons = new ImageView[BUTTON_COUNT];
    private HTextView textView_buttom;
    //    private static final String INFO_GAME = "Nuværende spil: %s";
//    private static final String INFO_GUESS = "Dine gæt: %s";
//    private static final String INFO_LEFT = "Gæt tilbage: %i";
//    private static final String INFO_LOGGED_IN = "Logged ind: %s";
//    private static final String INFO_CONNECTION = "Forbindelse: %s";
    private UpdateText updateText;

    private static final int BUTTON_PLAY = 0;
    private static final int BUTTON_HIGHSCORE = 1;
    private static final int BUTTON_WORD_LISTS = 2;
    private static final int BUTTON_SETTINGS = 3;
    private static final int BUTTON_ABOUT = 4;
    private static final int BUTTON_HELP = 5;
    private static final int BUTTON_LOGIN_OUT = 6;
    private static final int BUTTON_QUIT = 7;

    private int button_clicked;

    private long backPressed;
    private boolean click_status;

    private MaterialDialog md;

    private int newGameID = -1;

    private View.OnClickListener s_buttonListener;

    private static final String TAG = "MenuActivity";

    private final int[] loginButtons = new int[2];

    private InternetRecieverData connectionObserver;
    @SuppressWarnings("StaticVariableNamingConvention")
    private static LoadToast lt;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // contentView is set in super class..

        WindowLayout.hideStatusBar(getWindow(), null);

        initSound();
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        lt = new LoadToast(this);
        if (s_buttonListener == null) {
            s_buttonListener = new MenuButtonClickHandler(this);
        }

        loginButtons[0] = R.drawable.button_login;
        loginButtons[1] = R.drawable.button_logout;

        textView_buttom = (HTextView) findViewById(R.id.menu_buttom_text);
        textView_buttom.setAnimateType(HTextViewType.EVAPORATE);
        textView_buttom.setOnClickListener(new OnLoginClickListener(this));
        updateText = new UpdateText();

        title = (ImageView) findViewById(R.id.menu_title);
        title.setClickable(true);
        title.setOnClickListener(s_buttonListener);

        buttons[BUTTON_PLAY] = (ImageView) findViewById(R.id.menu_button_play);
        buttons[BUTTON_HIGHSCORE] = (ImageView) findViewById(R.id.menu_button_highscore);

        buttons[BUTTON_WORD_LISTS] = (ImageView) findViewById(R.id.menu_button_word_lists);
        buttons[BUTTON_SETTINGS] = (ImageView) findViewById(R.id.menu_button_settings);
        buttons[BUTTON_ABOUT] = (ImageView) findViewById(R.id.menu_button_about);
        buttons[BUTTON_HELP] = (ImageView) findViewById(R.id.menu_button_help);
        buttons[BUTTON_LOGIN_OUT] = (ImageView) findViewById(R.id.menu_button_login_out);
        buttons[BUTTON_QUIT] = (ImageView) findViewById(R.id.menu_button_quit);

        connectionObserver = new InternetRecieverData(this);
        InternetReciever.addObserver(connectionObserver);

    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        playSound(GameUtility.SFX_INTRO);
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateText = new UpdateText();
        menuHandler.post(updateText);
        showAll();
        if (connectionObserver == null) {
            connectionObserver = new InternetRecieverData(this);
        }
        InternetReciever.addObserver(connectionObserver);
        updateMargueeScroller();
    }

    @Override
    protected void onPause() {
        updateText = null;
        InternetReciever.removeObserver(connectionObserver);
        super.onPause();
    }

    @SuppressWarnings("AssignmentToNull")
    @Override
    protected void onDestroy() {
        for (int i = 0; i < buttons.length; i++) buttons[i] = null;
        stopService(MusicPlay.intent); // we want to stop the service here, as the acticity was destroyed
//        GameUtility.stopMusic(getApplicationContext());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (backPressed + BACK_PRESSED_DELAY > System.currentTimeMillis()) {
            YoYo.with(Techniques.ZoomOut).duration(300).playOn(findViewById(R.id.menu_background));
            if (sf != null) sf.setRun(false);
            super.onBackPressed();
        } else WindowLayout.showSnack("Tryk tilbage igen for at flygte i rædsel.", findViewById(R.id.menu_background), false);
        backPressed = System.currentTimeMillis();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }
    }

    private void setMenuButtonsClickable(final boolean value) {
        for (final ImageView iv : buttons) iv.setClickable(value);
    }

    private void showAll() {
        Log.d("login showall", String.valueOf(mpc.name == null));
        setLoginButton();
//        buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(loginButtons[mpc.name == null ? 0 : 1]));
        YoYo.with(Techniques.FadeIn).duration(1000).withListener(new EnterAnimatorHandler(this)).playOn(title);
        for (final ImageView button : buttons) {
            button.setClickable(true);
            button.setOnClickListener(s_buttonListener);
        }
    }

    private void endMenu(final String method_name, final ImageView clickedImageView) {
        if (click_status) {
            click_status = false;
            for (final ImageView iv : buttons) if (iv != clickedImageView) YoYo.with(Techniques.ZoomOut).duration(100).playOn(iv);
            YoYo.with(Techniques.Pulse).duration(500).withListener(new ExitAnimatorHandler(this, method_name)).playOn(clickedImageView);
        }
    }

    @SuppressWarnings("unused")
    private void showMultiplayer() {
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_MULTI_PLAYER));
    }

    @SuppressWarnings("unused")
    private void showHelp() {
//        if (sf != null) sf.setRun(false);
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_HELP));
    }

    @SuppressWarnings("unused")
    private void showHighScore() {
        if (GameUtility.connectionStatus > -1) {
            startActivity(new Intent(this, PlayerListActivity.class));
        } else {
            showDialog("Fejl", "Ingen internetforbindelse tilstede.");
        }
    }

    @SuppressWarnings("unused")
    private void showAbout() {
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_ABOUT));
    }

    @SuppressWarnings("unused")
    private void showSettings() {
//        startActivity(new Intent(this, PrefsActivity.class));
        showDialog("Hov!", "Denne funktion er ikke klar endnu.");
    }

    @SuppressWarnings("unused")
    private void showWordList() {
        startActivity(new Intent(this, WordListActivity.class));
    }

    @SuppressWarnings("unused")
    private void showLogin() {
        if (GameUtility.connectionStatus > -1) {
            textView_buttom.callOnClick();
        } else {
            showDialog("Fejl", "Ingen internetforbindelse tilstede.");
        }
    }

    @SuppressWarnings({"unused", "AccessStaticViaInstance"})
    private void showNewGame() {
        final ArrayList<StartGameItem> startGameItems = new ArrayList<>(3);

        StartGameItem startGameItem;

        try {
            // If previous game is found, add it to list :-)
            final SaveGame saveGame = (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
//            Log.d(TAG, saveGame.getLogic().toString());
            if (saveGame.getLogic() != null && !saveGame.getLogic().isGameOver()) {
                startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : " + (saveGame.isMultiPlayer() ? "Multi" : "Single") + "player / Gæt : " + saveGame.getLogic().getVisibleWord(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
            }
        } catch (final NullPointerException npe) {
            // nothing happends here, its just for not adding the option to continue a game.
        } finally {
            startGameItems.add(new StartGameItem(Constant.MODE_SINGLE_PLAYER, "Nyt singleplayer", "Tilfældigt ord.", imageRefs[0]));
            if (mpc.name != null && GameUtility.connectionStatus > -1) {
                startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER, "Næste multiplayer", "Kæmp imod", imageRefs[0]));
                startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER_2, "Vælg multiplayer", "Jægeren er den jagtede", imageRefs[0]));
                startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER_LOBBY, "Ny udfordring", "Udvælg dit offer", imageRefs[0]));
            }

            final StartGameAdapter adapter = new StartGameAdapter(this, R.layout.new_game_list_row, startGameItems);
            final ListView listViewItems = new ListView(this);
            listViewItems.setAdapter(adapter);
            listViewItems.setOnItemClickListener(new OnStartGameItemClickListener());

            md = new MaterialDialog.Builder(this)
                    .customView(listViewItems, false)
                    .backgroundColor(Color.BLACK)
                    .cancelable(true)
                    .cancelListener(new NewGameCancelListener())
                    .title("Start spil")
                    .show();
        }
    }

    @SuppressWarnings("unused")
    private void startNewGame() {
        if (newGameID != Constant.MODE_MULTI_PLAYER_LOGIN) {
            final Intent intent = new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, newGameID);
            if (newGameID == Constant.MODE_CONT_GAME) {
                intent.putExtra(Constant.KEY_SAVE_GAME, (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class));
            }
            if (sf != null) sf.setRun(false);
            startActivity(intent);
        } else {
            showLogin();
        }
    }

    private void callMethod(final String method_name) {
        if (FINISH.equals(method_name)) {
            ListFetcher.listHandler.post(ListFetcher.listSaver);
            finish();
        } else {
            try {
                getClass().getDeclaredMethod(method_name).invoke(this);
                //final Method method = getClass().getDeclaredMethod(method_name);
                //method.invoke(this);
            } catch (final Exception e) {
                // nothing here !! (should NEVER occour :! )
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void dialogQuit() {
        new MaterialDialog.Builder(this)
                .content("Ønsker du virkelig af forlade spillet?")
                .cancelable(true)
                .onAny(new QuitDialogCallback(this))
                .positiveText(R.string.dialog_yes)
                .negativeText(R.string.dialog_no)
                .title("Afslutningen er nær...")
                .backgroundColor(Color.BLACK)
                .contentColor(getResources().getColor(R.color.colorAccent))
                .buttonRippleColor(getResources().getColor(R.color.colorPrimaryDark))
                .show()
        ;
    }

    public void onFinishLoginDialog(final String title, final String pass) {
        if (title != null && pass != null) {
            buttons[BUTTON_LOGIN_OUT].setTag(true);
        }
        buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(mpc.name == null ? loginButtons[0] : loginButtons[1]));
//        updateMargueeScroller(NetworkHelper.getConnectivityStatus(getApplicationContext()));
        Log.d("login", "before show all");

        showAll();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (key.equals(Constant.KEY_PREFS_BLOOD) && sf != null) {
            SettingsDTO.PREFS_BLOOD = s_preferences.getBoolean(Constant.KEY_PREFS_BLOOD);
            sf.setRun(SettingsDTO.PREFS_BLOOD);
        }
//        if (key.equals(Constant.KEY_PREFS_MUSIC)) {
            SettingsDTO.PREFS_MUSIC = s_preferences.getBoolean(Constant.KEY_PREFS_MUSIC);
            if (SettingsDTO.PREFS_MUSIC) {
                if (!MusicPlay.isPlaying()) {
                    MusicPlay.intent = new Intent(getApplicationContext(), MusicPlay.class);
                    MusicPlay.intent.setAction("SKAFOTMUSIK");
                    startService(MusicPlay.intent);
                }
            } else {
                stopService(MusicPlay.intent);
            }
//        }

    }

    private static class WeakReferenceAbstraction {
        protected final WeakReference<MenuActivity> menuActivityWeakReference;

        public WeakReferenceAbstraction(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
        }
    }

    @SuppressWarnings("AccessStaticViaInstance")
    static class QuitDialogCallback extends WeakReferenceAbstraction implements MaterialDialog.SingleButtonCallback {

        public QuitDialogCallback(final MenuActivity menuActivity) {
            super(menuActivity);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                if (which == DialogAction.POSITIVE) {
                    menuActivity.endMenu(FINISH, menuActivity.buttons[menuActivity.BUTTON_QUIT]);
                } else {
                    menuActivity.click_status = true;
                    menuActivity.setMenuButtonsClickable(true);
                }
            }
        }
    }

    private static class OnStartGameItemClickListener implements AdapterView.OnItemClickListener {

        private static final String TAG = "NewGame";

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final Context context = view.getContext();
            if (context instanceof MenuActivity) {
                final MenuActivity menuActivity = (MenuActivity) context;
                menuActivity.playSound(GameUtility.SFX_MENU_CLICK);
                menuActivity.md.dismiss();
                Log.d(TAG, "New game mode selected : " + view.getTag());
                menuActivity.newGameID = (int) view.getTag();
                menuActivity.endMenu("startNewGame", menuActivity.buttons[BUTTON_PLAY]);
            }
        }
    }

    private static class MenuButtonClickHandler extends WeakReferenceAbstraction implements View.OnClickListener {

        @SuppressWarnings("NonConstantFieldWithUpperCaseName")

        public MenuButtonClickHandler(final MenuActivity menuActivity) {
            super(menuActivity);
        }

        @Override
        public void onClick(final View v) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                boolean buttonStates = false;
                if (v == menuActivity.buttons[BUTTON_PLAY]) {

                    menuActivity.callMethod("showNewGame");
                    menuActivity.button_clicked = BUTTON_PLAY;

                } else if (v == menuActivity.buttons[BUTTON_LOGIN_OUT]) {

                    menuActivity.button_clicked = BUTTON_LOGIN_OUT;
                    buttonStates = true; // or else we disable the buttons!
                    menuActivity.callMethod("showLogin"); // don't want to end menu with this one!

                } else if (v == menuActivity.buttons[BUTTON_HIGHSCORE]) {

                    menuActivity.endMenu("showHighScore", menuActivity.buttons[BUTTON_HIGHSCORE]);
                    menuActivity.button_clicked = BUTTON_HIGHSCORE;

                } else if (v == menuActivity.buttons[BUTTON_WORD_LISTS]) {

                    menuActivity.endMenu("showWordList", menuActivity.buttons[BUTTON_WORD_LISTS]);
                    menuActivity.button_clicked = BUTTON_WORD_LISTS;

                } else if (v == menuActivity.buttons[BUTTON_ABOUT]) {

                    menuActivity.endMenu("showAbout", menuActivity.buttons[BUTTON_ABOUT]);
                    menuActivity.button_clicked = BUTTON_ABOUT;

                } else if (v == menuActivity.buttons[BUTTON_HELP]) {

                    menuActivity.endMenu("showHelp", menuActivity.buttons[BUTTON_HELP]);
                    menuActivity.button_clicked = BUTTON_HELP;

                } else if (v == menuActivity.buttons[BUTTON_QUIT]) {

                    menuActivity.dialogQuit();
                    menuActivity.button_clicked = BUTTON_QUIT;

                } else if (v == menuActivity.buttons[BUTTON_SETTINGS]) {

                    menuActivity.endMenu("showSettings", menuActivity.buttons[BUTTON_SETTINGS]);
                    menuActivity.button_clicked = BUTTON_SETTINGS;

                } else if (v == menuActivity.title) {

                    menuActivity.showAll();
                    buttonStates = true;

                }
                if (menuActivity.button_clicked >= 0 && menuActivity.button_clicked < BUTTON_COUNT) {
                    menuActivity.playSound(GameUtility.SFX_MENU_CLICK);
                }
                menuActivity.setMenuButtonsClickable(buttonStates);
            }
        }
    }

    private static class EnterAnimatorHandler extends WeakReferenceAbstraction implements Animator.AnimatorListener {

        public EnterAnimatorHandler(final MenuActivity menuActivity) {
            super(menuActivity);
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                new AllFadeIn(menuActivity).run();
                menuActivity.click_status = true;
                menuActivity.setMenuButtonsClickable(true);
            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) { }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }

        private static class AllFadeIn extends WeakReferenceAbstraction implements Runnable {

            public AllFadeIn(final MenuActivity menuActivity) { super(menuActivity); }

            @Override
            public void run() {
                final MenuActivity menuActivity = menuActivityWeakReference.get();
                if (menuActivity != null) {
                    YoYo.with(Techniques.Pulse).duration(800).playOn(menuActivity.title);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuActivity.buttons[0]);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuActivity.buttons[1]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuActivity.buttons[2]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuActivity.buttons[3]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuActivity.buttons[4]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuActivity.buttons[5]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuActivity.buttons[6]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuActivity.buttons[7]);
                }
            }
        }
    }

    private static class ExitAnimatorHandler extends WeakReferenceAbstraction implements Animator.AnimatorListener {

        private final String methodToCall;

        public ExitAnimatorHandler(final MenuActivity menuActivity, final String methodToCall) {
            super(menuActivity);
            this.methodToCall = methodToCall;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                if (menuActivity.button_clicked >= 0 && menuActivity.button_clicked < menuActivity.buttons.length) {
                    menuActivity.buttons[menuActivity.button_clicked].setColorFilter(null);
                }
            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null && methodToCall != null) {
                menuActivity.callMethod(methodToCall);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private static class OnLoginClickListener extends WeakReferenceAbstraction implements View.OnClickListener {

        public OnLoginClickListener(final MenuActivity menuActivity) {
            super(menuActivity);
        }

        @Override
        public void onClick(final View v) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                if (menuActivity.sf != null) menuActivity.sf.setRun(false);
                menuActivity.md = new MaterialDialog.Builder(menuActivity)
                        .customView(R.layout.login, false)
                        .cancelable(true)
                        .positiveText("Ok")
                        .negativeText("Afbryd")
                        .onPositive(new OnLoginClickResponse(menuActivity))
                        .onNegative(new QuitDialogCallback(menuActivity))
                        .title("Login")
                        .show();
            }
        }
    }

    /**
     * Listener for adding list dialog !
     */
    private static class OnLoginClickResponse extends WeakReferenceAbstraction implements MaterialDialog.SingleButtonCallback {

        public OnLoginClickResponse(final MenuActivity menuActivity) {
            super(menuActivity);
        }

        private static boolean isValid(final Context context, @NonNull final String title, @NonNull final String url) {
            final boolean validStuff = !title.isEmpty() && !url.isEmpty();
            if (validStuff) return true;
            Toast.makeText(context, "Forkert indtastede informationer.", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                if (which == DialogAction.POSITIVE) {
                    /* let's inflate the dialog view... */
                    final View dialogCustomView = dialog.getCustomView();
                    if (dialogCustomView != null) {
                        final EditText editTextTitle = (EditText) dialogCustomView.findViewById(R.id.loginName);
                        final EditText editTextURL = (EditText) dialogCustomView.findViewById(R.id.loginPass);
                        final String user = editTextTitle.getText().toString().trim();
                        final String pass = editTextURL.getText().toString().trim();
                        final CheckBox check = (CheckBox) (dialogCustomView.findViewById(R.id.createUserCheckBox));
                        final boolean isChecked = check.isChecked();
                        Log.d("login", "isChecked " + isChecked);
                        Log.d("Login", user);
                        Log.d("Login", pass);
                        Log.d("Login", String.valueOf(isValid(menuActivity, user, pass)));

                        if (isValid(menuActivity, user, pass)) {
                            if (isChecked) {
                                GameUtility.mpc.pc.createPlayer(user, pass, menuActivity);
                                lt.show();
                            } else {
                                if (GameUtility.mpc.login(user, pass)) {
                                    Log.d("Login succes", " stuff");
                                } else {
                                    Log.d("Login failure", " stuff");
                                }
                                menuActivity.setLoginButton();
                            }
                            menuActivity.onFinishLoginDialog(user, pass);
                        }
                    }
                } else {
//                    menuActivity.setLoginButton();
                    menuActivity.callMethod("showAll");
                }
                dialog.dismiss();
                if (menuActivity.sf != null) {
                    menuActivity.sf.setRun(true);
                }
            }
        }
    }

    public static void postCreateResult(boolean b) {
        if (b) lt.success();
        else lt.error();
    }

    private class NewGameCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(final DialogInterface dialog) {
            dialog.dismiss();
            setMenuButtonsClickable(true);
        }
    }

    private void showDialog(final CharSequence title, final CharSequence content) {
        new MaterialDialog.Builder(this)
                .backgroundColor(Color.BLACK)
                .title(title)
                .content(content)
                .positiveText("Ok")
                .show();
        showAll();
    }

    /* buttom text view configuration */

    private int pos;
    private final ArrayDeque<String> info = new ArrayDeque<>(5);
    private final CharSequence NONE = "Ingen.";
    private final CharSequence NOTHING = "Intet.";

    private static final String INFO_GAME = "Spil: %s.";
    private static final String INFO_GUESS = "Dine gæt: %s.";
    private static final String INFO_LEFT = "Gæt tilbage: %d.";
    private static final String INFO_LOGGED_IN = "Logged ind: %s.";
    private static final String INFO_CONNECTION = "Forbindelse: %s.";
    private static final String INFO_BATTERY = "Batteriniveau: %s";

    private void updateMargueeScroller() {
        info.clear();
        // this is just a quick hack! but we need some basic info!
        final SaveGame sg;
        try {
            sg = (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
            if (sg.getLogic() != null && !sg.getLogic().isGameOver()) {
                info.add(String.format(INFO_GAME, "Igang"));
                info.add(String.format(INFO_GUESS, sg.getLogic().getUsedLetters().isEmpty() ? NONE : sg.getLogic().getUsedLetters()));
                info.add(String.format(INFO_LEFT, 7 - sg.getLogic().getNumWrongLetters()));
            } else {
                info.add(String.format(INFO_GAME, NOTHING));
            }
        } catch (final Exception e) {
            info.add(String.format(INFO_GAME, NOTHING));
        }

        try {
            info.add(String.format(INFO_LOGGED_IN, mpc != null && mpc.name != null ? mpc.name : "Nej"));
        } catch (final Exception e) {
            e.printStackTrace();
            info.add("Nej");
        }
        info.add(String.format(INFO_CONNECTION, GameUtility.connectionStatusName));
//        try {
//            info.add(String.format(INFO_BATTERY, Integer.toString(batteryLevelRecieverData.getData().getLevel())));
//        } catch (final Exception e) {
//            info.add("Ukendt %");
//        }
    }

    private class UpdateText implements Runnable {
        @Override
        public void run() {
            if (info.isEmpty()) {
                updateMargueeScroller();
                YoYo.with(Techniques.Flash).duration(2000).playOn(title);
//                YoYo.with(Techniques.RubberBand).duration(3000).playOn(sf);
            }
            textView_buttom.animateText(info.poll());
            menuHandler.postDelayed(updateText, 3000);
        }
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public void onInternetStatusChanged(final int connectionState) {
        GameUtility.connectionStatus = connectionState;
        if (connectionState > -1) {
            // there is internet
            setLoginButton();
        } else {
            // nope, no connection!
            mpc.name = null;
            setLoginButton();
        }
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public void onInternetStatusChanged(final String connectionState) {
        GameUtility.connectionStatusName = connectionState;
        // string version... could be displayed along the way..
        WindowLayout.showSnack(connectionState, sf, true);
    }

    @Override
    public void onBecameForeground() {
        if (sf != null) {
            sf.setRun(true);
        }
        mpc.name = null;
        setLoginButton();
        super.onBecameForeground();
    }

    @Override
    public void onBecameBackground() {
        if (sf != null) {
            sf.setRun(false);
        }
        super.onBecameBackground();
    }

    private void setLoginButton() {
        /* make sure the right button is set for the login/logout status */
        if (GameUtility.connectionStatus > -1) {
            if (buttons[BUTTON_LOGIN_OUT] != null) { // guard for when application first comes into foreground
                buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(mpc.name == null ? loginButtons[0] : loginButtons[1]));
            }
        } else {
            if (buttons[BUTTON_LOGIN_OUT] != null) {
                buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(loginButtons[0]));
                mpc.name = null;
            }
        }
        YoYo.with(Techniques.Pulse).duration(300).playOn(buttons[BUTTON_LOGIN_OUT]);
    }
}
