package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.views.HangedView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HangmanButtonFragment extends AbstractPlayFragment implements View.OnClickListener {

    private static final String KEY_MULTIPLAYER = "mp";
    private static final String KEY_GAME_STATE = "gs";
    private ArrayList<Button> listOfButtons;
    private View root;

    private int gameState;

    private Bundle lastBundle;

    public static HangmanButtonFragment newInstance(final int gameState, final boolean multiPlayer) {
        final HangmanButtonFragment hangmanButtonFragment = new HangmanButtonFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_GAME_STATE, gameState);
        args.putBoolean(KEY_MULTIPLAYER, multiPlayer);
        hangmanButtonFragment.setArguments(args);
        return hangmanButtonFragment;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)  {
        onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.hangman_button,container,false);

        galgen = (HangedView) root.findViewById(R.id.imageView);

        if (getArguments() != null) {
            lastBundle = getArguments();
            getBundleConfig(lastBundle);
        } else if (savedInstanceState != null) {
            lastBundle = savedInstanceState;
            getBundleConfig(lastBundle);
        }

//        if (lastBundle != null) {
//            getBundleConfig(lastBundle);
//        }


//        final Point size = new Point();
//        final Display display = getActivity().getWindowManager().getDefaultDisplay();
//        display.getSize(size);

//        galgen.init(size.x, galgen.getHeight());
//        Log.d("galge", String.valueOf(size.x));
//        Log.d("galge", String.valueOf(size.y));

        //galgen.setImageResource(R.drawable.galge);

        CheckGameType();
        ordet = (TextView) root.findViewById(R.id.visibleText);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ordet.setText(logik.getSynligtOrd());
        listOfButtons = initButtons();
        resetButtons();
        galgen.init();
        galgen.setState(gameState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = setBundleConfig();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        getBundleConfig(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    private Bundle setBundleConfig() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_MULTIPLAYER, isHotSeat);
        bundle.putInt(KEY_GAME_STATE, gameState);
        return bundle;
    }

    private void getBundleConfig(final Bundle bundle) {
        if (bundle != null) {
            isHotSeat = bundle.getBoolean(KEY_MULTIPLAYER);
            gameState = bundle.getInt(KEY_GAME_STATE);
        }
    }

    private void resetButtons() {
        for (final Button button : listOfButtons) {
            YoYo.with(Techniques.RollIn).duration(250).playOn(button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
        }
        listOfButtons.clear();
    }

    @Override
    public void onClick(final View v) {
        YoYo.with(Techniques.ZoomOutUp).duration(200).withListener(new OnButtonClickAnimatorListener(v)).playOn(v);
        Log.d("buttons", "button clicked");
        listOfButtons.add((Button) v);
        guess(((Button) v).getText().toString(), true);
    }

    /**
     * well this is annoying
     * @return The letter buttons as arraylist
     */
    private ArrayList<Button> initButtons(){
        final ArrayList<Button> returnList = new ArrayList<>();

        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout1)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout2)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout3)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout4)));

        //adding clickhandlers
        for (final Button btn : returnList){
            btn.setOnClickListener(this);
        }

        return returnList;
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for(int i = 0; i < vg.getChildCount(); i++) a.add((Button)vg.getChildAt(i));
        return a;
    }

    private static class OnButtonClickAnimatorListener implements Animator.AnimatorListener {

        private final WeakReference<View> viewWeakReference;

        public OnButtonClickAnimatorListener(final View view) {
            viewWeakReference = new WeakReference<>(view);
        }

        @Override
        public void onAnimationStart(final Animator animation) { }

        @Override
        public void onAnimationEnd(final Animator animation) {
            final View view = viewWeakReference.get();
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }
}
