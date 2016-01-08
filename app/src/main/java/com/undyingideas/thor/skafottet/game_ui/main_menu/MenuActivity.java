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
import android.content.Intent;
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
import com.undyingideas.thor.skafottet.Instructions;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game_ui.MainActivity;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.NewGameAdapter;
import com.undyingideas.thor.skafottet.views.NewGameItem;

import java.lang.ref.WeakReference;

/**
 * Very basic activity.
 *
 * @author rudz
 */
public class MenuActivity extends MenuActivityAbstract {

    private static final int BUTTON_COUNT = 7;
    private static final String FINISH = "finish";
    public static final int BACK_PRESSED_DELAY = 2000;
    private final ImageView[] buttons = new ImageView[BUTTON_COUNT];
    private ImageView title;
    private static final int TITLE = -1;

    private static final int BUTTON_CONT_GAME = 0;
    private static final int BUTTON_NEW_GAME = 1;
    private static final int BUTTON_MULTIPLAYER = 2;
    private static final int BUTTON_WORD_LISTS = 3;
    private static final int BUTTON_SETTINGS = 4;
    private static final int BUTTON_HELP = 5;
    private static final int BUTTON_QUIT = 6;

    private int button_clicked;

    private long backPressed;
    private boolean click_status;

    private MaterialDialog md;

    private long newGameID = -1;

    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // contentView is set in super class..

        title = (ImageView) findViewById(R.id.menu_title);
        title.setClickable(true);
        title.setOnClickListener(new MenuButtonClickHandler(this, TITLE));

        buttons[BUTTON_CONT_GAME] = (ImageView) findViewById(R.id.menu_cont_game);
        buttons[BUTTON_NEW_GAME] = (ImageView) findViewById(R.id.menu_new_game);
        buttons[BUTTON_MULTIPLAYER] = (ImageView) findViewById(R.id.menu_multiplayer);
        buttons[BUTTON_WORD_LISTS] = (ImageView) findViewById(R.id.menu_word_lists);
        buttons[BUTTON_SETTINGS] = (ImageView) findViewById(R.id.menu_settings);
        buttons[BUTTON_HELP] = (ImageView) findViewById(R.id.menu_help);
        buttons[BUTTON_QUIT] = (ImageView) findViewById(R.id.menu_quit);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setClickable(true);
            //noinspection ObjectAllocationInLoop
            buttons[i].setOnClickListener(new MenuButtonClickHandler(this, i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowLayout.hideStatusBar(getWindow(), null);
        showAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (backPressed + BACK_PRESSED_DELAY > System.currentTimeMillis()) {
            YoYo.with(Techniques.ZoomOut).duration(300).playOn(findViewById(R.id.menu_background));
            sf.setRun(false);
            super.onBackPressed();
        } else WindowLayout.showSnack("Tryk tilbage igen for at smutte.", findViewById(R.id.menu_background), false);
        backPressed = System.currentTimeMillis();
    }

    private void setMenuButtonClickable(final int index, final boolean value) {
        int trueIndex = index;
        if (trueIndex < 0) trueIndex = 0;
        else if (trueIndex > buttons.length - 1) trueIndex = buttons.length - 1;
        buttons[trueIndex].setClickable(value);
    }

    private void setMenuButtonsClickable(final boolean value) {
        for (final ImageView iv : buttons) iv.setClickable(value);
    }

    private void showAll() {

        YoYo.with(Techniques.FadeIn).duration(300).withListener(new EnterAnimatorHandler(this)).playOn(findViewById(R.id.menu_background));
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
                if (menuActivity.buttons[BUTTON_CONT_GAME].getVisibility() == View.INVISIBLE) {
                    YoYo.with(Techniques.BounceInUp).duration(900).playOn(menuActivity.buttons[1]);
                } else {
                    YoYo.with(Techniques.BounceInUp).duration(900).playOn(menuActivity.buttons[0]);
                    YoYo.with(Techniques.BounceInLeft).duration(900).playOn(menuActivity.buttons[1]);
                }
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
        public void onAnimationStart(final Animator animation) { }

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

    private static class MenuButtonClickHandler implements View.OnClickListener {

        private final WeakReference<MenuActivity> menuActivityWeakReference;
        @SuppressWarnings("NonConstantFieldWithUpperCaseName")
        private final int BUTTON_VALUE;

        public MenuButtonClickHandler(final MenuActivity menuActivity, final int buttonValue) {
            menuActivityWeakReference = new WeakReference<>(menuActivity);
            BUTTON_VALUE = buttonValue;
        }

        @Override
        public void onClick(final View v) {
            final MenuActivity menuActivity = menuActivityWeakReference.get();
            if (menuActivity != null) {
                menuActivity.button_clicked = BUTTON_VALUE;
                if (BUTTON_VALUE == BUTTON_CONT_GAME || BUTTON_VALUE == BUTTON_WORD_LISTS) {
                    new MaterialDialog.Builder(menuActivity)
                            .content("Ikke implementeret!!!")
                            .cancelable(true)
                            .positiveText(R.string.dialog_yes)
                            .title("STOP DA PRESS!!!")
                            .show();
                } else if (BUTTON_VALUE == BUTTON_MULTIPLAYER) {
                    menuActivity.endMenu("showMultiplayer", menuActivity.buttons[BUTTON_VALUE]);
                } else if (BUTTON_VALUE == BUTTON_NEW_GAME) {
                    menuActivity.setMenuButtonsClickable(false);
                    menuActivity.callMethod("showNewGame");
                } else if (BUTTON_VALUE == BUTTON_HELP) {
                    menuActivity.endMenu("showHelp", menuActivity.buttons[BUTTON_VALUE]);
                } else if (BUTTON_VALUE == BUTTON_QUIT) {
                    menuActivity.setMenuButtonsClickable(false);
                    menuActivity.dialogQuit();
                } else if (BUTTON_VALUE == TITLE) {
                    // figure out some funky stuff here !!! :-)
                    menuActivity.showAll();
                } else if (BUTTON_VALUE == BUTTON_SETTINGS){
                   menuActivity.showHighScore();
                }
            }
        }
    }

    private void endMenu(final String method_name, final ImageView clickedImageView) {
        if (click_status) {
            click_status = false;
            for (final ImageView iv : buttons) if (iv != clickedImageView) YoYo.with(Techniques.FadeOut).duration(100).playOn(iv);
            YoYo.with(Techniques.RotateOut).duration(500).withListener(new ExitAnimatorHandler(this, method_name)).playOn(clickedImageView);
        }
    }

    @SuppressWarnings("unused")
    private void showMultiplayer() {
        startActivity(new Intent(this, MainActivity.class).putExtra(Constant.KEY_GAME_MODE, Constant.KEY_MULTI_PLAYER));
    }

    @SuppressWarnings("unused")
    private void showHelp() {
        sf.setRun(false);
        startActivity(new Intent(this, Instructions.class));

    }

    private void showHighScore(){
        final Intent PlayerListActivity = new Intent(this, com.undyingideas.thor.skafottet.game_ui.hichscorecontent.PlayerListActivity.class);
        startActivity(PlayerListActivity);
    }

    @SuppressWarnings("unused")
    private void showNewGame() {
        final NewGameItem[] newGameItems = new NewGameItem[3];

        newGameItems[0] = new NewGameItem(0, "Wuhuu..", "Bare start spillet mester", R.drawable.forkert6);
        newGameItems[1] = new NewGameItem(1, "Anden mulighed", "For hulvate dude!", R.drawable.forkert5);
        newGameItems[2] = new NewGameItem(1, "Nothing here!...", "Starter også bare spillet !", R.drawable.forkert4);

        final NewGameAdapter adapter = new NewGameAdapter(this, R.layout.new_game_list_row, newGameItems);
        final ListView listViewItems = new ListView(this);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new OnNewGameItemClickListener());

        md = new MaterialDialog.Builder(this)
                .customView(listViewItems, false)
                .title("New Game")
                .show();
    }

    @SuppressWarnings("unused")
    private void startNewGame() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constant.KEY_GAME_MODE, Constant.KEY_SINGLE_PLAYER);
        sf.setRun(false);
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

    private void dialogQuit() {
        new MaterialDialog.Builder(this)
                .content("Afslut svinet? KYYYYLING!?")
                .cancelable(true)
                .onAny(new QuitDialogCallback(this))
                .positiveText(R.string.dialog_yes)
                .negativeText(R.string.dialog_no)
                .title("Quitos los Gamos...")
                .show()
                ;
    }

    private static class OnNewGameItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final WeakReference<Context> contextWeakReference = new WeakReference<>(view.getContext());
            final Context context =  contextWeakReference.get();
            if (context != null) {
                Log.d("NG", String.valueOf(id));
                ((MenuActivity) context).md.dismiss();
                ((MenuActivity) context).newGameID = id;
                ((MenuActivity) context).endMenu("startNewGame", ((MenuActivity) context).buttons[BUTTON_NEW_GAME]);
                //((MenuActivity) context).startNewGame(id);
            }
        }
    }
}
