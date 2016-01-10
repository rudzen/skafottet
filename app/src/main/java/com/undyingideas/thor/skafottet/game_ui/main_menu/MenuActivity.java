/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.game_ui.main_menu;

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
import android.widget.ImageView;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.game_ui.GameActivity;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Very basic activity.
 *
 * @author rudz
 */
public class MenuActivity extends MenuActivityAbstract {

    private static final String FINISH = "finish";
    private static final int BACK_PRESSED_DELAY = 2000;

    private static final int BUTTON_COUNT = 7;
    private ImageView title;
    private final ImageView[] buttons = new ImageView[BUTTON_COUNT];
    private static final int TITLE = -1;

    private static final int BUTTON_PLAY = 0;
    private static final int BUTTON_HIGHSCORE = 1;
    private static final int BUTTON_WORD_LISTS = 2;
    private static final int BUTTON_SETTINGS = 3;
    private static final int BUTTON_ABOUT = 4;
    private static final int BUTTON_HELP = 5;
    private static final int BUTTON_QUIT = 6;


    private int button_clicked;

    private long backPressed;
    private boolean click_status;

    private MaterialDialog md;

    private int newGameID = -1;
    private int maxID; // quick hack for the dialog ID mess

    private View.OnClickListener s_buttonListener;

    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // contentView is set in super class..

        if (s_buttonListener == null) {
            s_buttonListener = new MenuButtonClickHandler(this);
        }

        title = (ImageView) findViewById(R.id.menu_title);
        title.setClickable(true);
        title.setOnClickListener(s_buttonListener);

        buttons[BUTTON_PLAY] = (ImageView) findViewById(R.id.menu_button_play);
        buttons[BUTTON_HIGHSCORE] = (ImageView) findViewById(R.id.menu_button_highscore);

        buttons[BUTTON_WORD_LISTS] = (ImageView) findViewById(R.id.menu_button_word_lists);
        buttons[BUTTON_SETTINGS] = (ImageView) findViewById(R.id.menu_button_settings);
        buttons[BUTTON_ABOUT] = (ImageView) findViewById(R.id.menu_button_about);
        buttons[BUTTON_HELP] = (ImageView) findViewById(R.id.menu_button_help);
        buttons[BUTTON_QUIT] = (ImageView) findViewById(R.id.menu_button_quit);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        WindowLayout.hideStatusBar(getWindow(), null);
        showAll();
    }

    @SuppressWarnings("AssignmentToNull")
    @Override
    protected void onDestroy() {
        for (int i = 0; i < buttons.length; i++) buttons[i] = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (backPressed + BACK_PRESSED_DELAY > System.currentTimeMillis()) {
            YoYo.with(Techniques.ZoomOut).duration(300).playOn(findViewById(R.id.menu_background));
            if (sf != null) sf.setRun(false);
            super.onBackPressed();
        } else WindowLayout.showSnack("Tryk tilbage igen for at smutte.", findViewById(R.id.menu_background), false);
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
        YoYo.with(Techniques.FadeIn).duration(300).withListener(new EnterAnimatorHandler(this)).playOn(findViewById(R.id.menu_background));
        for (final ImageView button : buttons) {
            button.setClickable(true);
            button.setOnClickListener(s_buttonListener);
        }
//        setMenuButtonsClickable(true);
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
        final Intent PlayerListActivity = new Intent(this, com.undyingideas.thor.skafottet.game_ui.hichscorecontent.PlayerListActivity.class);
        startActivity(PlayerListActivity);
    }

    @SuppressWarnings("unused")
    private void showAbout() {
        startActivity(new Intent(this, GameActivity.class).putExtra(Constant.KEY_MODE, Constant.MODE_ABOUT));
    }

    @SuppressWarnings("unused")
    private void showSettings() {
        notReady();
    }

    @SuppressWarnings("unused")
    private void showWordList() {
        notReady();
//        startActivity(new Intent(this, WordDetailsActivity.class));
    }

    @SuppressWarnings({"unused", "AccessStaticViaInstance"})
    private void showNewGame() {
        final ArrayList<StartGameItem> startGameItem = new ArrayList<>(3);
        maxID = 3;

        try {
            // If previous game is found, add it to list :-)
            GameUtility.s_prefereces.checkForNullKey(Constant.KEY_SAVE_GAME);
            final SaveGame saveGame = (SaveGame) GameUtility.s_prefereces.getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
            if (saveGame != null && !saveGame.getLogic().isGameOver()) {
                startGameItem.add(new StartGameItem(0, "Fortsæt sidste spil", "Type : " + (saveGame.isMultiPlayer() ? "Multi" : "Single") + "player / Gæt : " + saveGame.getLogic().getVisibleWord(), GameUtility.imageRefs[saveGame.getLogic().getNumWrongLetters()]));
                maxID++;
            }
        } catch (final NullPointerException npe) {
            // nothing happends here, it's just for not adding the option to continue a game.
        }

        startGameItem.add(new StartGameItem(1, "Nyt singleplayer", "Tilfældigt ord.", GameUtility.imageRefs[0]));
        startGameItem.add(new StartGameItem(2, getString(R.string.menu_new_multi_player_game), "Udfordring.", GameUtility.imageRefs[0]));
        startGameItem.add(new StartGameItem(3, getString(R.string.menu_new_multi_player_game), "Tværfaglig udfordring" , GameUtility.imageRefs[0]));

        final StartGameAdapter adapter = new StartGameAdapter(this, R.layout.new_game_list_row, startGameItem);
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

    @SuppressWarnings("unused")
    private void startNewGame() {
        final Intent intent = new Intent(this, GameActivity.class);
        if (maxID == 3) {
            newGameID++;
        }
        Log.d(TAG, "Game mode started : " + newGameID);
        intent.putExtra(Constant.KEY_MODE, newGameID);
        if (sf != null) sf.setRun(false);
        startActivity(intent);
    }

    private void callMethod(final String method_name) {
        if (FINISH.equals(method_name)) {
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

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WeakReference<Context> contextWeakReference = new WeakReference<>(view.getContext());
            final Context context = contextWeakReference.get();
            if (context != null) {
                Log.d("NG", String.valueOf(position));
                ((MenuActivity) context).md.dismiss();
                ((MenuActivity) context).newGameID = (int) id;
                ((MenuActivity) context).endMenu("startNewGame", ((MenuActivity) context).buttons[BUTTON_PLAY]);
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
                    // figure out some funky stuff here !!! :-)
                    menuActivity.showAll();
                    //menuActivity.button_clicked = TITLE;
                    buttonStates = true;
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
                YoYo.with(Techniques.FadeInDown).duration(800).playOn(menuActivity.title);
                YoYo.with(Techniques.BounceInUp).duration(900).playOn(menuActivity.buttons[0]);
                YoYo.with(Techniques.BounceInLeft).duration(900).playOn(menuActivity.buttons[1]);
                YoYo.with(Techniques.BounceInLeft).duration(800).playOn(menuActivity.buttons[2]);
                YoYo.with(Techniques.BounceInLeft).duration(800).playOn(menuActivity.buttons[3]);
                YoYo.with(Techniques.BounceInRight).duration(700).playOn(menuActivity.buttons[4]);
                YoYo.with(Techniques.BounceInRight).duration(700).playOn(menuActivity.buttons[5]);
                YoYo.with(Techniques.BounceInDown).duration(600).playOn(menuActivity.buttons[6]);
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

    private class NewGameCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(final DialogInterface dialog) {
            dialog.dismiss();
            setMenuButtonsClickable(true);
        }
    }

    private void notReady() {
        new MaterialDialog.Builder(this)
                .backgroundColor(Color.BLACK)
                .title("Ikke klar endnu")
                .content("Ikke implementeret!")
                .positiveText("Ok")
                .show();
        showAll();
    }

}
