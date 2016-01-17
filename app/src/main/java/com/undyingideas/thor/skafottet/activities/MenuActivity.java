/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.undyingideas.thor.skafottet.support.audio.music.MusicPlay;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.NetworkHelper;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.imageRefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.mpc;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_preferences;

/**
 * The main menu activity.
 * Quite large, but contains only relevant code.
 *
 * @author rudz
 */
public class MenuActivity extends MenuActivityAbstract implements
        InternetRecieverData.InternetRecieverInterface,
        PreferenceChangeListener
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // contentView is set in super class..

        initSound();

        if (s_buttonListener == null) {
            s_buttonListener = new MenuButtonClickHandler(this);
        }

        loginButtons[0] = R.drawable.button_login;
        loginButtons[1] = R.drawable.button_logout;

        textView_buttom = (HTextView) findViewById(R.id.menu_buttom_text);
        textView_buttom.setAnimateType(HTextViewType.EVAPORATE);
        textView_buttom.setOnClickListener(new OnLoginClickListener(this));
        updateText = new UpdateText();

        onInternetStatusChanged(NetworkHelper.getConnectivityStatus(getApplicationContext()));

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
        onInternetStatusChanged(NetworkHelper.getConnectivityStatus(getApplicationContext()));
        InternetReciever.addObserver(connectionObserver);
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
        stopService(MusicPlay.intent);
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
        buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(loginButtons[mpc.name == null ? 0 : 1]));
        YoYo.with(Techniques.FadeIn).duration(1000).withListener(new EnterAnimatorHandler(this)).playOn(title);
        for (final ImageView button : buttons) {
            button.setClickable(true);
            button.setOnClickListener(s_buttonListener);
        }
    }

    private void endMenu(final String method_name, final ImageView clickedImageView) {
        if (click_status) {
            clickedImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            click_status = false;
            for (final ImageView iv : buttons) if (iv != clickedImageView) YoYo.with(Techniques.FadeOut).duration(100).playOn(iv);
            YoYo.with(Techniques.RotateOut).duration(500).withListener(new ExitAnimatorHandler(this, method_name)).playOn(clickedImageView);
        }
    }

    @SuppressWarnings("unused")
    private void showMultiplayer() {
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_MULTI_PLAYER));
    }

    @SuppressWarnings("unused")
    private void showHelp() {
        if (sf != null) sf.setRun(false);
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_HELP));
    }

    @SuppressWarnings("unused")
    private void showHighScore() {
        final Intent PlayerListActivity = new Intent(this, com.undyingideas.thor.skafottet.activities.PlayerListActivity.class);
        startActivity(PlayerListActivity);
    }

    @SuppressWarnings("unused")
    private void showAbout() {
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_ABOUT));
    }

    @SuppressWarnings("unused")
    private void showSettings() {
        showDialog("Hov!", "Denne funktion mangler stadig.");
    }

    @SuppressWarnings("unused")
    private void showWordList() {
        startActivity(new Intent(this, WordListActivity.class));
    }

    @SuppressWarnings("unused")
    private void showLogin() {
        if (NetworkHelper.getConnectivityStatus(getApplicationContext()) > -1) {
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
            s_preferences.checkForNullKey(Constant.KEY_SAVE_GAME);
            final SaveGame saveGame = (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
            Log.d(TAG, saveGame.toString());
            if (saveGame.getLogic() != null && !saveGame.getLogic().isGameOver()) {
                startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : " + (saveGame.isMultiPlayer() ? "Multi" : "Single") + "player / Gæt : " + saveGame.getLogic().getVisibleWord(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
            }
        } catch (final NullPointerException npe) {
            // nothing happends here, its just for not adding the option to continue a game.
        } finally {
            startGameItems.add(new StartGameItem(Constant.MODE_SINGLE_PLAYER, "Nyt singleplayer", "Tilfældigt ord.", imageRefs[0]));
            if (mpc.name != null && NetworkHelper.getConnectivityStatus(getApplicationContext()) > -1) {
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
            if (sf != null) sf.setRun(false);
            startActivity(intent);
        } else {
            showLogin();
        }
    }

    private void callMethod(final String method_name) {
        if (FINISH.equals(method_name)) {
            ListFetcher.listHandler.post(ListFetcher.listSaver);
            overridePendingTransition(0, 0);
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
        showAll();
    }

    @Override
    public void preferenceChange(final PreferenceChangeEvent pce) {
        // do nothing for now.
    }

    @SuppressWarnings("AccessStaticViaInstance")
    static class QuitDialogCallback implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<MenuActivity> menuActivityWeakReference;

        public QuitDialogCallback(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
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

    private static class MenuButtonClickHandler implements View.OnClickListener {

        private final WeakReference<MenuActivity> menuActivityWeakReference;

        @SuppressWarnings("NonConstantFieldWithUpperCaseName")

        public MenuButtonClickHandler(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
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

    private static class EnterAnimatorHandler implements Animator.AnimatorListener {

        private final WeakReference<MenuActivity> menuActivityWeakReference;

        public EnterAnimatorHandler(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Pulse).duration(800).playOn(menuActivity.title);
                        YoYo.with(Techniques.RotateInDownLeft).duration(900).playOn(menuActivity.buttons[0]);
                        YoYo.with(Techniques.RotateIn).duration(900).playOn(menuActivity.buttons[1]);
                        YoYo.with(Techniques.RotateInUpRight).duration(800).playOn(menuActivity.buttons[2]);
                        YoYo.with(Techniques.RotateInUpRight).duration(800).playOn(menuActivity.buttons[3]);
                        YoYo.with(Techniques.RotateInUpLeft).duration(700).playOn(menuActivity.buttons[4]);
                        YoYo.with(Techniques.RotateInUpLeft).duration(700).playOn(menuActivity.buttons[5]);
                        YoYo.with(Techniques.RotateInDownRight).duration(600).playOn(menuActivity.buttons[6]);
                        YoYo.with(Techniques.RotateInDownRight).duration(600).playOn(menuActivity.buttons[7]);

                    }
                }.run();
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
    }

    private static class ExitAnimatorHandler implements Animator.AnimatorListener {

        private final WeakReference<MenuActivity> menuActivityWeakReference;
        private final String methodToCall;

        public ExitAnimatorHandler(final MenuActivity menuActivity, final String methodToCall) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
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

    private static class OnLoginClickListener implements View.OnClickListener {

        private final WeakReference<MenuActivity> menuActivityWeakReference;

        public OnLoginClickListener(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
        }

        @Override
        public void onClick(final View v) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
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
    private static class OnLoginClickResponse implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<MenuActivity> menuActivityWeakReference;

        public OnLoginClickResponse(final MenuActivity menuActivity) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
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
                        Log.d("Login", user);
                        Log.d("Login", pass);
                        Log.d("Login", String.valueOf(isValid(menuActivity, user, pass)));

                        if (isValid(menuActivity, user, pass)) {
                            menuActivity.onFinishLoginDialog(user, pass);
                            if (GameUtility.mpc.login(user, pass)) {
                                Log.d("Login succes", " stuff");
                            } else {
                                Log.d("Login failure", " stuff");
                            }
                        }

                    }
                } else {
                    menuActivity.buttons[BUTTON_LOGIN_OUT].setBackground(menuActivity.getResources().getDrawable(mpc.name == null ? menuActivity.loginButtons[0] : menuActivity.loginButtons[1]));
                    menuActivity.callMethod("showAll");
                }
                dialog.dismiss();
                menuActivity.onWindowFocusChanged(true);
            }
        }
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

    private static final String INFO_GAME = "Nuværende spil: %s";
    private static final String INFO_GUESS = "Dine gæt: %s";
    private static final String INFO_LEFT = "Gæt tilbage: %d";
    private static final String INFO_LOGGED_IN = "Logged ind: %s";
    private static final String INFO_CONNECTION = "Forbindelse: %s";
    private static final String INFO_BATTERY = "Batteriniveau : %s";

    private void updateMargueeScroller() {
        info.clear();
        // this is just a quick hack! but we need some basic info!
        final SaveGame sg;
        try {
            sg = (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
            if (sg.getLogic() != null) {
                info.add(String.format(INFO_GAME, sg.getLogic().getVisibleWord()));
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
        info.add(String.format(INFO_CONNECTION, NetworkHelper.getConnectivityStatusString(getApplicationContext())));
        info.add(String.format(INFO_BATTERY, batteryLevelRecieverData.getData() != null ? Integer.toString(batteryLevelRecieverData.getData().getLevel()) : "Ukendt"));
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


    @Override
    public void onInternetStatusChanged(final int connectionState) {
//        updateMargueeScroller(connectionState);
        if (connectionState > -1) {
            // we have a connection now ! enable the firebase controller

        } else {
            // kill the firebase controller

        }

    }

    @Override
    public void onInternetStatusChanged(final String connectionState) {
        // string version... could be displayed along the way..
        WindowLayout.showSnack(connectionState, sf, true);
    }

}
