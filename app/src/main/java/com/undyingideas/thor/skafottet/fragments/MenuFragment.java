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

package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.undyingideas.thor.skafottet.game.SaveGame;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.interfaces.IGameSoundNotifier;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.observer.FireBaseLoginData;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.StarField;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.imageRefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.mpc;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_preferences;

/**
 * <p>
 * Created on 22-01-2016, 17:11.
 * Project : skafottet</p>
 *
 * @author rudz
 */
@SuppressWarnings("ConstantConditions")
public class MenuFragment extends Fragment implements FireBaseLoginData.FirebaseLoginResponse, PreferenceChangeListener {

    private static final int BUTTON_COUNT = 8;
    private ImageView title;
    private final ImageView[] buttons = new ImageView[BUTTON_COUNT];

    private static final int BUTTON_PLAY = 0;
    private static final int BUTTON_HIGHSCORE = 1;
    private static final int BUTTON_WORD_LISTS = 2;
    private static final int BUTTON_SETTINGS = 3;
    private static final int BUTTON_ABOUT = 4;
    private static final int BUTTON_HELP = 5;
    private static final int BUTTON_LOGIN_OUT = 6;
    private static final int BUTTON_QUIT = 7;

    /* star field stuff */
    private StarField sf;

    /* buttom text display fields */
    private static final int MSG_UPDATE_TEXT = 1;
    private static final String MSG_STRING = "m";
    private HTextView textView_buttom;
    private TextUpdateRunner textUpdateRunner; //  the runnable
    private TextUpdateHandler textUpdateHandler; // the handler

    /* sensor stuff */
    @Nullable
    private SensorManager sensorManager;
    @Nullable
    private Sensor sensor;
    private MenuSensorEventListener sensorListener;

    /* other fields */

    private int button_clicked; // not used atm
    private boolean click_status;
    private View.OnClickListener s_buttonListener;
    private static final String TAG = "MenuFragment";

    private
    @DrawableRes
    final int[] loginButtons = new int[2];


    /* observer data receiving classes */
    private FireBaseLoginData fireBaseLoginData;


    /* interfaces to control the activity's actions */
    @Nullable
    private IFragmentFlipper iFragmentFlipper;
    @Nullable
    private IGameSoundNotifier iGameSoundNotifier;

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu, container, false);

        if (s_buttonListener == null) {
            s_buttonListener = new MenuButtonClickHandler(this);
        }

        /* begin configuration for starfield and sensor */
        sf = (StarField) root.findViewById(R.id.sf);
        sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);

        loginButtons[0] = R.drawable.button_login;
        loginButtons[1] = R.drawable.button_logout;

        textView_buttom = (HTextView) root.findViewById(R.id.menu_buttom_text);
        textView_buttom.setAnimateType(HTextViewType.EVAPORATE);
        textView_buttom.setOnClickListener(new OnLoginClickListener(this));

        title = (ImageView) root.findViewById(R.id.menu_title);
        title.setClickable(true);
        title.setOnClickListener(s_buttonListener);

        buttons[BUTTON_PLAY] = (ImageView) root.findViewById(R.id.menu_button_play);
        buttons[BUTTON_HIGHSCORE] = (ImageView) root.findViewById(R.id.menu_button_highscore);

        buttons[BUTTON_WORD_LISTS] = (ImageView) root.findViewById(R.id.menu_button_word_lists);
        buttons[BUTTON_SETTINGS] = (ImageView) root.findViewById(R.id.menu_button_settings);
        buttons[BUTTON_ABOUT] = (ImageView) root.findViewById(R.id.menu_button_about);
        buttons[BUTTON_HELP] = (ImageView) root.findViewById(R.id.menu_button_help);
        buttons[BUTTON_LOGIN_OUT] = (ImageView) root.findViewById(R.id.menu_button_login_out);
        buttons[BUTTON_LOGIN_OUT].setTag(false);
        buttons[BUTTON_QUIT] = (ImageView) root.findViewById(R.id.menu_button_quit);

        /* configure callback observer interfaces data classes */
        fireBaseLoginData = new FireBaseLoginData(this);

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        registerSensor();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (sensor != null && sensorManager != null) sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        textUpdateRunner = new TextUpdateRunner(this); //  the runnable
        textUpdateHandler = new TextUpdateHandler(this);
        textUpdateHandler.post(textUpdateRunner);
        showAll();
        super.onResume();
    }

    @Override
    public void onPause() {
        textUpdateHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (sensor != null && sensorManager != null) sensorManager.unregisterListener(sensorListener, sensor);
        super.onStop();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentFlipper && context instanceof IGameSoundNotifier) {
            iFragmentFlipper = (IFragmentFlipper) context;
            iGameSoundNotifier = (IGameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IFragmentFlipper & IGameSoundNotifier");
        }
    }

    @Override
    public void onDetach() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(null);
            buttons[i] = null;
        }
        iFragmentFlipper = null;
        iGameSoundNotifier = null;
        super.onDetach();
    }

    private void setMenuButtonsClickable(final boolean value) {
        for (final ImageView iv : buttons) iv.setClickable(value);
    }

    private void registerSensor() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (sensor != null) {
            sensorListener = new MenuSensorEventListener(this);
            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
            Log.i(TAG, "TYPE_GRAVITY sensor registered");
        } else {
            Log.e(TAG, "TYPE_GRAVITY sensor NOT registered");
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor != null) {
                sensorListener = new MenuSensorEventListener(this);
                sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
                Log.i(TAG, "TYPE_ACCELEROMETER sensor registered");
            } else {
                sensorManager = null;
                Log.e(TAG, "TYPE_ACCELEROMETER sensor NOT registered");
            }
        }
    }

    /**
     * Re-displays the menu from scratch
     */
    private void showAll() {
        Log.d("login showall", String.valueOf(mpc.name == null));
        setLoginButton(GameUtility.getConnectionStatus());
        YoYo.with(Techniques.FadeIn).duration(1000).withListener(new EnterAnimatorHandler(this)).playOn(title);
        for (final ImageView button : buttons) {
            button.setClickable(true);
            button.setOnClickListener(s_buttonListener);
        }
        setLoginButton();
    }

    private void endMenu(final int gameMode, final ImageView clickedImageView) {
        if (click_status) {
            click_status = false;
            for (final ImageView iv : buttons) if (iv != clickedImageView) YoYo.with(Techniques.ZoomOut).duration(100).playOn(iv);
            YoYo.with(Techniques.Pulse).duration(500).withListener(new ExitAnimatorHandler(this, gameMode)).playOn(clickedImageView);
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
                if (mpc.name != null && saveGame.isMultiPlayer() && mpc.name.equals(saveGame.getPlayers()[0].getName())) {
                    startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : Multiplayer / Modstander : " + saveGame.getPlayers()[1].getName(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
                } else {
                    startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : Singleplayer / Gæt : " + saveGame.getLogic().getVisibleWord(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
                }
            }
        } catch (final NullPointerException npe) {
            // nothing happends here, its just for not adding the option to continue a game.
        } finally {
            startGameItems.add(new StartGameItem(Constant.MODE_SINGLE_PLAYER, "Nyt singleplayer", "Tilfældigt ord.", imageRefs[0]));
            if (mpc.name != null && GameUtility.getConnectionStatus() > -1) {
                // TODO : Add callback listener for possible multiplayer games waiting..

                if (mpc.lc.getFirstActiveGame() != null) {
                    startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER, "Næste multiplayer", "Kæmp imod", imageRefs[0]));
                }
                startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER_2, "Vælg multiplayer", "Jægeren er den jagtede", imageRefs[0]));
                startGameItems.add(new StartGameItem(Constant.MODE_MULTI_PLAYER_LOBBY, "Ny udfordring", "Udvælg dit offer", imageRefs[0]));
            }

            final StartGameAdapter adapter = new StartGameAdapter(getContext(), R.layout.new_game_list_row, startGameItems);
            final ListView listViewItems = new ListView(getContext());
            listViewItems.setAdapter(adapter);
            listViewItems.setOnItemClickListener(new OnStartGameItemClickListener(this));

            WindowLayout.setMd(new MaterialDialog.Builder(getActivity())
                    .customView(listViewItems, false)
                    .backgroundColor(Color.BLACK)
                    .cancelable(true)
                    .cancelListener(new NewGameCancelListener())
                    .title("Start spil")
                    .show());
        }
    }

    @SuppressWarnings("deprecation")
    private void dialogQuit() {
        new MaterialDialog.Builder(getContext())
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

    @Override
    public void onLoginResponse(final boolean result) {
        if (result) {
            WindowLayout.getLoadToast().success();
            GameUtility.me.setName(mpc.name);
            WindowLayout.showSnack("Du er nu logged ind som " + mpc.name, sf, true);
        } else {
            WindowLayout.getLoadToast().error();
            GameUtility.me.setName("Mig");
            WindowLayout.showSnack("Kunne ikke logge på.", sf, true);
        }
        buttons[BUTTON_LOGIN_OUT].setTag(result);
        setLoginButton(GameUtility.getConnectionStatus());
    }

    @Override
    public void preferenceChange(final PreferenceChangeEvent pce) {
        if (pce.getKey().equals(Constant.KEY_PREFS_MUSIC)) {
            Log.d("PrefsChange", pce.getNewValue());
        }
    }

    private static class MenuSensorEventListener extends WeakReferenceHolder<MenuFragment> implements android.hardware.SensorEventListener {

        public MenuSensorEventListener(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null && menuFragment.sf != null) {
                menuFragment.sf.setGravity(event.values[0], event.values[1]);
//                Log.d("GRAV", "Starfield gravity updated to x: " + event.values[0] + ", y: " + event.values[1]);
            }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) { }
    }

    @SuppressWarnings("AccessStaticViaInstance")
    static class QuitDialogCallback extends WeakReferenceHolder<MenuFragment> implements MaterialDialog.SingleButtonCallback {

        public QuitDialogCallback(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                if (which == DialogAction.POSITIVE) {
                    menuFragment.endMenu(Constant.MODE_FINISH, menuFragment.buttons[menuFragment.BUTTON_QUIT]);
                } else {
                    menuFragment.click_status = true;
                    menuFragment.setMenuButtonsClickable(true);
                }
            }
        }
    }

    private static class OnStartGameItemClickListener extends WeakReferenceHolder<MenuFragment> implements AdapterView.OnItemClickListener {

        private static final String TAG = "NewGame";

        public OnStartGameItemClickListener(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            // TODO : Call back to act to play the sound.
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                WindowLayout.getMd().dismiss();
                Log.d(TAG, "New game mode selected : " + view.getTag());
                menuFragment.endMenu((int) view.getTag(), menuFragment.buttons[BUTTON_PLAY]);
            }
        }
    }

    private static class MenuButtonClickHandler extends WeakReferenceHolder<MenuFragment> implements View.OnClickListener {

        public MenuButtonClickHandler(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onClick(final View v) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                boolean buttonStates = false;
                if (v instanceof ImageView) {
                    menuFragment.iGameSoundNotifier.playGameSound(GameUtility.SFX_MENU_CLICK);
                    menuFragment.setMenuButtonsClickable(false);

                    if (v == menuFragment.buttons[BUTTON_PLAY]) {

                        menuFragment.button_clicked = BUTTON_PLAY;
                        menuFragment.showNewGame();

                    } else if (v == menuFragment.buttons[BUTTON_LOGIN_OUT]) {

                        menuFragment.button_clicked = BUTTON_LOGIN_OUT;

                        if (GameUtility.getConnectionStatus() > -1) {
                            menuFragment.textView_buttom.callOnClick();
                        } else {
                            WindowLayout.showDialog(menuFragment.getContext(), "Fejl", "Ingen internetforbindelse tilstede.");
                        }

                        buttonStates = true;

                    } else if (v == menuFragment.buttons[BUTTON_HIGHSCORE]) {

                        if (GameUtility.getConnectionStatus() > -1) {
                            menuFragment.button_clicked = BUTTON_HIGHSCORE;
                            menuFragment.endMenu(Constant.MODE_HIGHSCORE, menuFragment.buttons[BUTTON_HIGHSCORE]);
                        } else {
                            buttonStates = true;
                            WindowLayout.showDialog(menuFragment.getContext(), "Fejl", "Ingen internetforbindelse tilstede.");
                        }

                    } else if (v == menuFragment.buttons[BUTTON_WORD_LISTS]) {

                        menuFragment.button_clicked = BUTTON_WORD_LISTS;
                        menuFragment.endMenu(Constant.MODE_WORD_LIST, menuFragment.buttons[BUTTON_WORD_LISTS]);

                    } else if (v == menuFragment.buttons[BUTTON_ABOUT]) {

                        menuFragment.button_clicked = BUTTON_ABOUT;
                        menuFragment.endMenu(Constant.MODE_ABOUT, menuFragment.buttons[BUTTON_ABOUT]);

                    } else if (v == menuFragment.buttons[BUTTON_HELP]) {

                        menuFragment.button_clicked = BUTTON_HELP;
                        menuFragment.endMenu(Constant.MODE_HELP, menuFragment.buttons[BUTTON_HELP]);

                    } else if (v == menuFragment.buttons[BUTTON_QUIT]) {

                        menuFragment.dialogQuit();
                        menuFragment.button_clicked = BUTTON_QUIT;

                    } else if (v == menuFragment.buttons[BUTTON_SETTINGS]) {

                        menuFragment.button_clicked = BUTTON_SETTINGS;
                        menuFragment.endMenu(Constant.MODE_SETTINGS, menuFragment.buttons[BUTTON_SETTINGS]);

//                        WindowLayout.showDialog(menuFragment.getContext(), "Hov!", "Denne funktion er ikke klar endnu.");
//                        buttonStates = true;

                    } else if (v == menuFragment.title) {
                        menuFragment.showAll();
                        buttonStates = true;
                    }

                    // we don't want to disable the buttons for the action that doesn't switch the fragment/act
                    if (buttonStates) {
                        menuFragment.setMenuButtonsClickable(true);
                    }
                }
            }
        }
    }

    private static class EnterAnimatorHandler extends WeakReferenceHolder<MenuFragment> implements Animator.AnimatorListener {

        public EnterAnimatorHandler(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                new AllFadeIn(menuFragment).run();
                menuFragment.click_status = true;
                menuFragment.setMenuButtonsClickable(true);
            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) { }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }

        private static class AllFadeIn extends WeakReferenceHolder<MenuFragment> implements Runnable {

            public AllFadeIn(final MenuFragment menuFragment) { super(menuFragment); }

            @Override
            public void run() {
                final MenuFragment menuFragment = weakReference.get();
                if (menuFragment != null) {
                    YoYo.with(Techniques.Pulse).duration(800).playOn(menuFragment.title);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuFragment.buttons[0]);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuFragment.buttons[1]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuFragment.buttons[2]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuFragment.buttons[3]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuFragment.buttons[4]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuFragment.buttons[5]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuFragment.buttons[6]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuFragment.buttons[7]);
                }
            }
        }
    }

    private static class ExitAnimatorHandler extends WeakReferenceHolder<MenuFragment> implements Animator.AnimatorListener {

        private final int gameMode;

        public ExitAnimatorHandler(final MenuFragment menuFragment, final int gameMode) {
            super(menuFragment);
            this.gameMode = gameMode;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
//            final MenuFragment menuFragment = weakReference.get();
//            if (menuFragment != null) {
//                if (menuFragment.button_clicked >= 0 && menuFragment.button_clicked < menuFragment.buttons.length) {
//                    menuFragment.buttons[menuFragment.button_clicked].setColorFilter(null);
//                }
//            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                if (gameMode != Constant.MODE_CONT_GAME) {
                    menuFragment.iFragmentFlipper.flipFragment(gameMode);
                } else if (gameMode == Constant.MODE_MULTI_PLAYER) {
                    menuFragment.iFragmentFlipper.flipFragment(gameMode, mpc.lc.getFirstActiveGame());
                } else {
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.KEY_SAVE_GAME, (SaveGame) s_preferences.getObject(Constant.KEY_SAVE_GAME, SaveGame.class));
                    menuFragment.iFragmentFlipper.flipFragment(gameMode, bundle);
                }
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private static class OnLoginClickListener extends WeakReferenceHolder<MenuFragment> implements View.OnClickListener {

        public OnLoginClickListener(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onClick(final View v) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                WindowLayout.setMd(new MaterialDialog.Builder(menuFragment.getActivity())
                        .customView(R.layout.login, false)
                        .cancelable(true)
                        .positiveText("Ok")
                        .negativeText(R.string.cancel)
                        .onPositive(new OnLoginClickResponse(menuFragment))
                        .onNegative(new QuitDialogCallback(menuFragment))
                        .title(R.string.login)
                        .show());
            }
        }
    }

    /**
     * Listener for adding list dialog !
     */
    private static class OnLoginClickResponse extends WeakReferenceHolder<MenuFragment> implements MaterialDialog.SingleButtonCallback {

        public OnLoginClickResponse(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        private static boolean isValid(@NonNull final String title, @NonNull final String url) {
            return !title.isEmpty() && !url.isEmpty();
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                if (which == DialogAction.POSITIVE) {
                    /* let's inflate the dialog view... */
                    final View dialogCustomView = dialog.getCustomView();
                    if (dialogCustomView != null) {
                        WindowLayout.getLoadToast().show();
                        final EditText editTextTitle = (EditText) dialogCustomView.findViewById(R.id.loginName);
                        final EditText editTextURL = (EditText) dialogCustomView.findViewById(R.id.loginPass);
                        final String user = editTextTitle.getText().toString().trim();
                        final String pass = editTextURL.getText().toString().trim();
                        final CheckBox check = (CheckBox) dialogCustomView.findViewById(R.id.createUserCheckBox);
                        final boolean isChecked = check.isChecked();

                        if (isValid(user, pass)) {
                            WindowLayout.getLoadToast().show();
                            if (isChecked) {
                                GameUtility.mpc.pc.createPlayer(user, pass, menuFragment.fireBaseLoginData);
                            } else {
                                GameUtility.mpc.login(user, pass, menuFragment.fireBaseLoginData);
                            }
                        } else {
                            WindowLayout.showSnack("Ugyldig information indtastet.", menuFragment.textView_buttom, true);
                        }
                    }
                } else {
//                    menuFragment.setLoginButton();
                    menuFragment.showAll();
                }
                dialog.dismiss();
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

    /* buttom text view configuration */
    private static class TextUpdateHandler extends Handler {

        private final WeakReference<MenuFragment> menuFragmentWeakReference;

        public TextUpdateHandler(final MenuFragment menuFragment) {
            menuFragmentWeakReference = new WeakReference<>(menuFragment);
        }

        @Override
        public void handleMessage(final Message msg) {
            if (msg != null && msg.what == MSG_UPDATE_TEXT) { // we got an updated text
                final MenuFragment menuFragment = menuFragmentWeakReference.get();
                if (menuFragment != null) {
//                    Log.d(TAG, "Got data : " + msg.getData().getString(MSG_STRING));
                    final String string = msg.getData().getString(MSG_STRING);
                    if (string != null) {
                        menuFragment.textView_buttom.animateText(string);
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

    private static class TextUpdateRunner extends WeakReferenceHolder<MenuFragment> implements Runnable {

        private final CharSequence NONE = "Ingen.";
        private final CharSequence NOTHING = "Intet.";

        private static final String INFO_GAME = "Spil: %s.";
        private static final String INFO_GUESS = "Dine gæt: %s.";
        private static final String INFO_LEFT = "Gæt tilbage: %d.";
        private static final String INFO_LOGGED_IN = "Logged ind: %s.";
        private static final String INFO_CONNECTION = "Forbindelse: %s.";
        private static final String INFO_BATTERY = "Batteriniveau: %s";

        private final Bundle bundle = new Bundle();
        private final ArrayDeque<String> info = new ArrayDeque<>(5);

        public TextUpdateRunner(final MenuFragment menuFragment) {
            super(menuFragment);
        }

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
            info.add(String.format(INFO_CONNECTION, GameUtility.getConnectionStatusName()));
//        try {
//            info.add(String.format(INFO_BATTERY, Integer.toString(batteryLevelRecieverData.getData().getLevel())));
//        } catch (final Exception e) {
//            info.add("Ukendt %");
//        }
        }

        @Override
        public void run() {
            final MenuFragment menuFragment = weakReference.get();
            if (menuFragment != null) {
                if (info.isEmpty()) {
                    updateMargueeScroller();
                    YoYo.with(Techniques.Flash).duration(2000).playOn(menuFragment.title);
                }
                bundle.clear();
                bundle.putString(MSG_STRING, info.pollFirst());
//                    Log.d(TAG, "Attempting to send : " + bundle.getString(MSG_STRING));
                final Message message = menuFragment.textUpdateHandler.obtainMessage(MSG_UPDATE_TEXT);
                message.setData(bundle);
                message.sendToTarget();
                menuFragment.textUpdateHandler.postDelayed(menuFragment.textUpdateRunner, 2000);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setLoginButton(final int connectionStatus) {
        /* make sure the right button is set for the login/logout status */
        if (buttons[BUTTON_LOGIN_OUT] != null) { // guard for when application first comes into foreground
            if (connectionStatus > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttons[BUTTON_LOGIN_OUT].setBackground(ContextCompat.getDrawable(getContext(), (boolean) buttons[BUTTON_LOGIN_OUT].getTag() ? loginButtons[1] : loginButtons[0]));
                } else {
                    buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable((boolean) buttons[BUTTON_LOGIN_OUT].getTag() ? loginButtons[1] : loginButtons[0]));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttons[BUTTON_LOGIN_OUT].setBackground(ContextCompat.getDrawable(getContext(), loginButtons[0]));
                } else {
                    buttons[BUTTON_LOGIN_OUT].setBackground(getResources().getDrawable(loginButtons[0]));
                }
                mpc.name = null;
            }
            YoYo.with(Techniques.Pulse).duration(300).playOn(buttons[BUTTON_LOGIN_OUT]);
        }
    }

    private void setLoginButton() {
        setLoginButton(GameUtility.getConnectionStatus());
    }

}