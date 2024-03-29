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

package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.DrawableHelper;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.AutoScaleTextView;
import com.undyingideas.thor.skafottet.views.StarField;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.SFX_MENU_CLICK;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getConnectionStatus;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getConnectionStatusName;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getMe;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getPrefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getSettings;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.imageRefs;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.settings;

/**
 * <p>
 * Created on 22-01-2016, 17:11.
 * Project : skafottet</p>
 *
 * @author rudz
 */
@SuppressWarnings("ConstantConditions")
public class MenuFragment extends Fragment implements PreferenceChangeListener {

    private static final int BUTTON_COUNT = 8;
    private ImageView title;
    private final RelativeLayout[] mButtons = new RelativeLayout[BUTTON_COUNT];
    private final AutoScaleTextView[] mButtons_text = new AutoScaleTextView[BUTTON_COUNT];

    private static final int BUTTON_PLAY = 0;
    private static final int BUTTON_HIGHSCORE = 1;
    private static final int BUTTON_WORD_LISTS = 2;
    private static final int BUTTON_SETTINGS = 3;
    private static final int BUTTON_ABOUT = 4;
    private static final int BUTTON_HELP = 5;
    private static final int BUTTON_LOGIN_OUT = 6;
    private static final int BUTTON_QUIT = 7;

    /* star field stuff */
    private StarField mStarField;

    /* buttom text display fields */
    private static final int MSG_UPDATE_TEXT = 1;
    private static final String MSG_STRING = "m";
    private HTextView mTextView_buttom;
    private TextUpdateRunner mTextUpdateRunner; //  the runnable
    private TextUpdateHandler mTextUpdateHandler; // the handler

    /* mSensor stuff */
    @Nullable
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mSensor;
    private MenuSensorEventListener mSensorListener;

    /* other fields */
    private int button_clicked; // not used atm
    private boolean mClickStatus;
    private View.OnClickListener mButtonListener;
    private static final String TAG = "MenuFragment";

    /* observer data receiving classes */
//    private FireBaseLoginData fireBaseLoginData;

    /* interfaces to control the activity's actions */
    @Nullable
    private IFragmentFlipper mFragmentFlipper;
    @Nullable
    private IGameSoundNotifier mGameSoundNotifier;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentFlipper && context instanceof IGameSoundNotifier) {
            mFragmentFlipper = (IFragmentFlipper) context;
            mGameSoundNotifier = (IGameSoundNotifier) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IFragmentFlipper & IGameSoundNotifier");
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection IfMayBeConditional

    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu, container, false);

        if (mButtonListener == null) {
            mButtonListener = new MenuButtonClickHandler(this);
        }

        /* begin configuration for starfield and mSensor */
        mStarField = (StarField) root.findViewById(R.id.sf);
        mStarField.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, settings.prefsColour);

        mTextView_buttom = (HTextView) root.findViewById(R.id.menu_buttom_text);
        mTextView_buttom.setAnimateType(HTextViewType.EVAPORATE);
        mTextView_buttom.setOnClickListener(new OnLoginClickListener(this));
        mTextView_buttom.setTextColor(getSettings().textColour);

        title = (ImageView) root.findViewById(R.id.menu_title);
        title.setClickable(true);
        title.setOnClickListener(mButtonListener);

        mButtons[BUTTON_PLAY] = (RelativeLayout) root.findViewById(R.id.menu_button_play);
        mButtons_text[BUTTON_PLAY] = (AutoScaleTextView) root.findViewById(R.id.menu_button_play_text);

        mButtons[BUTTON_HIGHSCORE] = (RelativeLayout) root.findViewById(R.id.menu_button_highscore);
        mButtons_text[BUTTON_HIGHSCORE] = (AutoScaleTextView) root.findViewById(R.id.menu_button_highscore_text);

        mButtons[BUTTON_WORD_LISTS] = (RelativeLayout) root.findViewById(R.id.menu_button_word_lists);
        mButtons_text[BUTTON_WORD_LISTS] = (AutoScaleTextView) root.findViewById(R.id.menu_button_word_lists_text);

        mButtons[BUTTON_SETTINGS] = (RelativeLayout) root.findViewById(R.id.menu_button_settings);
        mButtons_text[BUTTON_SETTINGS] = (AutoScaleTextView) root.findViewById(R.id.menu_button_settings_text);

        mButtons[BUTTON_ABOUT] = (RelativeLayout) root.findViewById(R.id.menu_button_about);
        mButtons_text[BUTTON_ABOUT] = (AutoScaleTextView) root.findViewById(R.id.menu_button_about_text);

        mButtons[BUTTON_HELP] = (RelativeLayout) root.findViewById(R.id.menu_button_help);
        mButtons_text[BUTTON_HELP] = (AutoScaleTextView) root.findViewById(R.id.menu_button_help_text);

        mButtons[BUTTON_LOGIN_OUT] = (RelativeLayout) root.findViewById(R.id.menu_button_login_out);
        mButtons_text[BUTTON_LOGIN_OUT] = (AutoScaleTextView) root.findViewById(R.id.menu_button_login_out_text);
        mButtons[BUTTON_LOGIN_OUT].setTag(false); // logged in or not :)

        mButtons[BUTTON_QUIT] = (RelativeLayout) root.findViewById(R.id.menu_button_quit);
        mButtons_text[BUTTON_QUIT] = (AutoScaleTextView) root.findViewById(R.id.menu_button_quit_text);

        /* configure callback observer interfaces data classes */
//        fireBaseLoginData = new FireBaseLoginData(this);

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        /* configure the button colours */
        DrawableHelper.setButtonColors(mButtons, mButtons_text);

        /* register the mSensor */
        registerSensor();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mSensor != null && mSensorManager != null) mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mTextUpdateRunner = new TextUpdateRunner(this); //  the runnable
        mTextUpdateHandler = new TextUpdateHandler(this);
        mTextUpdateHandler.post(mTextUpdateRunner);
        showAll();
        super.onResume();
    }

    @Override
    public void onPause() {
        mTextUpdateHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mSensor != null && mSensorManager != null) mSensorManager.unregisterListener(mSensorListener, mSensor);
        super.onStop();
    }

    @Override
    public void onDetach() {
        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setBackground(null);
            mButtons[i] = null;
        }
        mFragmentFlipper = null;
        mGameSoundNotifier = null;
        super.onDetach();
    }

    private void setMenuButtonsClickable(final boolean value) {
        for (final RelativeLayout relativeLayout : mButtons) relativeLayout.setClickable(value);
    }

    private void registerSensor() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (mSensor != null) {
            mSensorListener = new MenuSensorEventListener(this);
            mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
            Log.i(TAG, "TYPE_GRAVITY mSensor registered");
        } else {
            Log.e(TAG, "TYPE_GRAVITY mSensor NOT registered");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor != null) {
                mSensorListener = new MenuSensorEventListener(this);
                mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
                Log.i(TAG, "TYPE_ACCELEROMETER mSensor registered");
            } else {
                mSensorManager = null;
                Log.e(TAG, "TYPE_ACCELEROMETER mSensor NOT registered");
            }
        }
    }

    /**
     * Re-displays the menu from scratch
     */
    private void showAll() {
//        Log.d("login showall", String.valueOf(mpc.name == null));
        YoYo.with(Techniques.FadeIn).duration(1000).withListener(new EnterAnimatorHandler(this)).playOn(title);
        for (final RelativeLayout button : mButtons) {
            button.setClickable(true);
            button.setOnClickListener(mButtonListener);
        }
        setLoginButton();
    }

    private void endMenu(final int gameMode, final RelativeLayout clickedView) {
        if (mClickStatus) {
            mClickStatus = false;
            for (final RelativeLayout iv : mButtons) if (iv != clickedView) YoYo.with(Techniques.ZoomOut).duration(100).playOn(iv);
            YoYo.with(Techniques.Pulse).duration(500).withListener(new ExitAnimatorHandler(this, gameMode)).playOn(clickedView);
        }
    }

    @SuppressWarnings({"unused", "AccessStaticViaInstance"})
    private void showNewGame() {
        final ArrayList<StartGameItem> startGameItems = new ArrayList<>(3);

        StartGameItem startGameItem;

        try {
            // If previous game is found, add it to list :-)
            final SaveGame saveGame = (SaveGame) getPrefs().getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
//            Log.d(TAG, saveGame.getLogic().toString());
            if (saveGame.getLogic() != null && !saveGame.getLogic().isGameOver()) {
//                if (mpc.name != null && saveGame.isMultiPlayer() && mpc.name.equals(saveGame.getPlayers()[0].getmName())) {
//                    startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : Multiplayer / Modstander : " + saveGame.getPlayers()[1].getmName(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
//                } else {
                    startGameItems.add(new StartGameItem(Constant.MODE_CONT_GAME, "Fortsæt sidste spil", "Type : Singleplayer / Gæt : " + saveGame.getLogic().getVisibleWord(), imageRefs[saveGame.getLogic().getNumWrongLetters()]));
//                }
            }
        } catch (final NullPointerException npe) {
            // nothing happends here, its just for not adding the option to continue a game.
        } finally {
            startGameItems.add(new StartGameItem(Constant.MODE_SINGLE_PLAYER, "Nyt singleplayer", "Tilfældigt ord.", imageRefs[0]));
            if (GameUtility.isLoggedIn() && getConnectionStatus() > -1) {
                if (!getMe().getGameList().isEmpty()) {
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
                    .title("Start spil"));
            WindowLayout.getMd().show();
        }
    }

    @SuppressWarnings("deprecation")
    private void dialogQuit() {
        WindowLayout.setMd(new MaterialDialog.Builder(getActivity())
                .content("Ønsker du virkelig af forlade spillet?")
                .cancelable(true)
                .onAny(new QuitDialogCallback(this))
                .positiveText(R.string.dialog_yes)
                .negativeText(R.string.dialog_no)
                .title("Afslutningen er nær...")
                .backgroundColor(Color.BLACK)
                .contentColor(getResources().getColor(R.color.colorAccent))
                .buttonRippleColor(getResources().getColor(R.color.colorPrimaryDark)));
        WindowLayout.getMd().show();
    }

//    @Override
//    public void onLoginResponse(final boolean result) {
//        if (result) {
//            WindowLayout.getLoadToast().success();
//            GameUtility.me.setName(mpc.name);
//            WindowLayout.showSnack("Du er nu logged ind som " + mpc.name, mStarField, true);
//        } else {
//            WindowLayout.getLoadToast().error();
//            GameUtility.me.setName("Mig");
//            WindowLayout.showSnack("Kunne ikke logge på.", mStarField, true);
//        }
//        mButtons[BUTTON_LOGIN_OUT].setTag(result);
//        setLoginButton();
//    }

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
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null && menuFragment.mStarField != null) {
                menuFragment.mStarField.setGravity(event.values[0], event.values[1]);
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
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                if (which == DialogAction.POSITIVE) {
                    menuFragment.endMenu(Constant.MODE_FINISH, menuFragment.mButtons[menuFragment.BUTTON_QUIT]);
                } else {
                    menuFragment.mClickStatus = true;
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
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                WindowLayout.getMd().dismiss();
                Log.d(TAG, "New game mode selected : " + view.getTag());
                menuFragment.endMenu((int) view.getTag(), menuFragment.mButtons[BUTTON_PLAY]);
            }
        }
    }

    private static class MenuButtonClickHandler extends WeakReferenceHolder<MenuFragment> implements View.OnClickListener {

        public MenuButtonClickHandler(final MenuFragment menuFragment) {
            super(menuFragment);
        }

        @Override
        public void onClick(final View v) {
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                menuFragment.mGameSoundNotifier.playGameSound(SFX_MENU_CLICK);
                boolean buttonStates = false;
                if (v instanceof RelativeLayout) {
                    menuFragment.setMenuButtonsClickable(false);

                    if (v == menuFragment.mButtons[BUTTON_PLAY]) {

                        menuFragment.button_clicked = BUTTON_PLAY;
                        menuFragment.showNewGame();

                    } else if (v == menuFragment.mButtons[BUTTON_LOGIN_OUT]) {

                        menuFragment.button_clicked = BUTTON_LOGIN_OUT;

                        if (getConnectionStatus() > -1) {
                            if (GameUtility.isLoggedIn()) {
                                menuFragment.mFragmentFlipper.flipFragment(Constant.MODE_LOGOUT);
                            } else {
                                menuFragment.endMenu(Constant.MODE_LOGIN, menuFragment.mButtons[BUTTON_LOGIN_OUT]);
                            }
//                            menuFragment.mTextView_buttom.callOnClick();
                        } else {
                            WindowLayout.showDialog(menuFragment.getContext(), "Fejl", "Ingen internetforbindelse tilstede.");
                        }

                        buttonStates = true;

                    } else if (v == menuFragment.mButtons[BUTTON_HIGHSCORE]) {

//                        if (getConnectionStatus() > -1) {
                            menuFragment.button_clicked = BUTTON_HIGHSCORE;
                            menuFragment.endMenu(Constant.MODE_HIGHSCORE, menuFragment.mButtons[BUTTON_HIGHSCORE]);
//                        } else {
//                            buttonStates = true;
//                            WindowLayout.showDialog(menuFragment.getContext(), "Fejl", "Ingen internetforbindelse tilstede.");
//                        }

                    } else if (v == menuFragment.mButtons[BUTTON_WORD_LISTS]) {

                        menuFragment.button_clicked = BUTTON_WORD_LISTS;
                        menuFragment.endMenu(Constant.MODE_WORD_LIST, menuFragment.mButtons[BUTTON_WORD_LISTS]);

                    } else if (v == menuFragment.mButtons[BUTTON_ABOUT]) {

                        menuFragment.button_clicked = BUTTON_ABOUT;
                        menuFragment.endMenu(Constant.MODE_ABOUT, menuFragment.mButtons[BUTTON_ABOUT]);

                    } else if (v == menuFragment.mButtons[BUTTON_HELP]) {

                        menuFragment.button_clicked = BUTTON_HELP;
                        menuFragment.endMenu(Constant.MODE_HELP, menuFragment.mButtons[BUTTON_HELP]);

                    } else if (v == menuFragment.mButtons[BUTTON_QUIT]) {

                        menuFragment.dialogQuit();
                        menuFragment.button_clicked = BUTTON_QUIT;

                    } else if (v == menuFragment.mButtons[BUTTON_SETTINGS]) {

                        menuFragment.button_clicked = BUTTON_SETTINGS;
                        menuFragment.endMenu(Constant.MODE_SETTINGS, menuFragment.mButtons[BUTTON_SETTINGS]);

//                        WindowLayout.showDialog(menuFragment.getContext(), "Hov!", "Denne funktion er ikke klar endnu.");
//                        buttonStates = true;
                    }

                    // we don't want to disable the mButtons for the action that doesn't switch the fragment/act
                    if (buttonStates) {
                        menuFragment.setMenuButtonsClickable(true);
                    }
                } else if (v instanceof ImageView) {
                    // title was clicked.
                    menuFragment.showAll();
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
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                new AllFadeIn(menuFragment).run();
                menuFragment.mClickStatus = true;
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
                final MenuFragment menuFragment = mWeakReference.get();
                if (menuFragment != null) {
                    YoYo.with(Techniques.Pulse).duration(800).playOn(menuFragment.title);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuFragment.mButtons[0]);
                    YoYo.with(Techniques.FadeIn).duration(900).playOn(menuFragment.mButtons[1]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuFragment.mButtons[2]);
                    YoYo.with(Techniques.FadeIn).duration(800).playOn(menuFragment.mButtons[3]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuFragment.mButtons[4]);
                    YoYo.with(Techniques.FadeIn).duration(700).playOn(menuFragment.mButtons[5]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuFragment.mButtons[6]);
                    YoYo.with(Techniques.FadeIn).duration(600).playOn(menuFragment.mButtons[7]);
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
//            final MenuFragment menuFragment = mWeakReference.get();
//            if (menuFragment != null) {
//                if (menuFragment.button_clicked >= 0 && menuFragment.button_clicked < menuFragment.mButtons.length) {
//                    menuFragment.mButtons[menuFragment.button_clicked].setColorFilter(null);
//                }
//            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                if (gameMode != Constant.MODE_CONT_GAME) {
                    menuFragment.mFragmentFlipper.flipFragment(gameMode);
//                } else if (gameMode == Constant.MODE_MULTI_PLAYER) {
//                    menuFragment.mFragmentFlipper.flipFragment(gameMode, mpc.lc.getFirstActiveGame());
                } else {
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.KEY_SAVE_GAME, (SaveGame)  getPrefs().getObject(Constant.KEY_SAVE_GAME, SaveGame.class));
                    menuFragment.mFragmentFlipper.flipFragment(gameMode, bundle);
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
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                menuFragment.mFragmentFlipper.flipFragment(Constant.MODE_LOGIN);
//                WindowLayout.setMd(new MaterialDialog.Builder(menuFragment.getActivity())
//                        .customView(R.layout.login, false)
//                        .cancelable(true)
//                        .positiveText("Ok")
//                        .negativeText(R.string.cancel)
//                        .onPositive(new OnLoginClickResponse(menuFragment))
//                        .onNegative(new QuitDialogCallback(menuFragment))
//                        .title(R.string.login));
//                WindowLayout.getMd().show();
            }
        }
    }

    /**
     * Listener for adding list dialog !
     */
//    private static class OnLoginClickResponse extends WeakReferenceHolder<MenuFragment> implements MaterialDialog.SingleButtonCallback {
//
//        public OnLoginClickResponse(final MenuFragment menuFragment) {
//            super(menuFragment);
//        }
//
//        private static boolean isValid(@NonNull final String title, @NonNull final String url) {
//            return !title.isEmpty() && !url.isEmpty();
//        }
//
//        @Override
//        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
//            final MenuFragment menuFragment = mWeakReference.get();
//            if (menuFragment != null) {
//                if (which == DialogAction.POSITIVE) {
//                    /* let's inflate the dialog view... */
//                    final View dialogCustomView = dialog.getCustomView();
//                    if (dialogCustomView != null) {
//                        WindowLayout.getLoadToast().show();
//                        final EditText editTextTitle = (EditText) dialogCustomView.findViewById(R.id.loginName);
//                        final EditText editTextURL = (EditText) dialogCustomView.findViewById(R.id.loginPass);
//                        final String user = editTextTitle.getText().toString().trim();
//                        final String pass = editTextURL.getText().toString().trim();
//                        final CheckBox check = (CheckBox) dialogCustomView.findViewById(R.id.createUserCheckBox);
//                        final boolean isChecked = check.isChecked();
//
//                        if (isValid(user, pass)) {
//                            WindowLayout.getLoadToast().show();
//                            if (isChecked) {
//                                GameUtility.mpc.pc.createPlayer(user, pass, menuFragment.fireBaseLoginData);
//                            } else {
//                                GameUtility.mpc.login(user, pass, menuFragment.fireBaseLoginData);
//                            }
//                        } else {
//                            WindowLayout.showSnack("Ugyldig information indtastet.", menuFragment.mTextView_buttom, true);
//                        }
//                    }
//                } else {
////                    menuFragment.setLoginButton();
//                    menuFragment.showAll();
//                }
//                dialog.dismiss();
//            }
//        }
//    }

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
                    final String theText = msg.getData().getString(MSG_STRING);
                    if (theText != null) {
                        menuFragment.mTextView_buttom.animateText(theText);
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

    private static class TextUpdateRunner extends WeakReferenceHolder<MenuFragment> implements Runnable {

        private static final CharSequence NONE = "Ingen.";
        private static final CharSequence NOTHING = "Intet.";

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
                sg = (SaveGame)  getPrefs().getObject(Constant.KEY_SAVE_GAME, SaveGame.class);
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

            final String logInName;
            if (GameUtility.isLoggedIn()) {
                logInName = getMe().getName();
//            } else if (settings.auth_status == SettingsDTO.AUTH_ANON) {
//                logInName = "Basis";
            } else {
                logInName = "Nej";
            }
            info.add(String.format(INFO_LOGGED_IN, logInName));

//            try {
//                info.add(String.format(INFO_LOGGED_IN, mpc != null && mpc.name != null ? mpc.name : "Nej"));
//            } catch (final Exception e) {
//                e.printStackTrace();
//                info.add("Nej");
//            }
            info.add(String.format(INFO_CONNECTION, getConnectionStatusName()));
//        try {
//            info.add(String.format(INFO_BATTERY, Integer.toString(batteryLevelRecieverData.getData().getLevel())));
//        } catch (final Exception e) {
//            info.add("Ukendt %");
//        }
        }

        @Override
        public void run() {
            final MenuFragment menuFragment = mWeakReference.get();
            if (menuFragment != null) {
                if (info.isEmpty()) {
                    updateMargueeScroller();
                    YoYo.with(Techniques.Flash).duration(2000).playOn(menuFragment.title);
                }
                bundle.clear();
                bundle.putString(MSG_STRING, info.pollFirst());
                final Message message = menuFragment.mTextUpdateHandler.obtainMessage(MSG_UPDATE_TEXT);
                message.setData(bundle);
                message.sendToTarget();
                menuFragment.mTextUpdateHandler.postDelayed(menuFragment.mTextUpdateRunner, 2000);
            }
        }
    }

    public void setLoginButton() {
        /* make sure the right button is set for the login/logout status */
        if (mButtons[BUTTON_LOGIN_OUT] != null) { // guard for when application first comes into foreground
            if (!GameUtility.isLoggedIn()) {
                mButtons_text[BUTTON_LOGIN_OUT].setText(R.string.menu_button_login_in);
            } else {
                mButtons_text[BUTTON_LOGIN_OUT].setText(R.string.menu_button_login_out);
            }
            YoYo.with(Techniques.Pulse).duration(300).playOn(mButtons[BUTTON_LOGIN_OUT]);
        }
    }

}