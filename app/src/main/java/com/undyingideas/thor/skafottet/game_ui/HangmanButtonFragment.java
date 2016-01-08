package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game.Galgelogik;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.views.HangedView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

import static com.undyingideas.thor.skafottet.utility.GameUtility.s_prefereces;

public class HangmanButtonFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_MULTIPLAYER = "mp";
    private static final String KEY_GAME_STATE = "gs";
    private static final String KEY_POSSIBLE_WORDS = "posw";
    private ArrayList<Button> listOfButtons;
    private int gameState;

    private Bundle lastBundle;

    private Galgelogik logik;
    private final ArrayList<String> possibleWords = new ArrayList<>();
    private boolean isHotSeat;
    private String theGuess; //bruges til at holde det aktuelle gæt
    private HangedView galgen;

    private TextView usedLetters, ordet, status;
    private EditText input;

    public static HangmanButtonFragment newInstance(final int gameState, final boolean multiPlayer, final ArrayList<String> possibleWords) {
        final HangmanButtonFragment hangmanButtonFragment = new HangmanButtonFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_GAME_STATE, gameState);
        args.putBoolean(KEY_MULTIPLAYER, multiPlayer);
        args.putStringArrayList(KEY_POSSIBLE_WORDS, possibleWords);
        hangmanButtonFragment.setArguments(args);
        return hangmanButtonFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)  {
        onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.hangman_button, container, false);

        galgen = (HangedView) root.findViewById(R.id.imageView);

        if (getArguments() != null) {
            lastBundle = getArguments();
            getBundleConfig(lastBundle);
        } else if (savedInstanceState != null) {
            lastBundle = savedInstanceState;
            getBundleConfig(lastBundle);
        }

        listOfButtons = new ArrayList<>();
        listOfButtons.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout1)));
        listOfButtons.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout2)));
        listOfButtons.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout3)));
        listOfButtons.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout4)));

        //adding clickhandlers
        for (final Button btn : listOfButtons){
            btn.setOnClickListener(this);
        }

        resetButtons();

        CheckGameType();
        ordet = (TextView) root.findViewById(R.id.visibleText);




        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ordet.setText(logik.getSynligtOrd());
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
        bundle.putStringArrayList(KEY_POSSIBLE_WORDS, possibleWords);
        return bundle;
    }

    private void getBundleConfig(final Bundle bundle) {
        if (bundle != null) {
            isHotSeat = bundle.getBoolean(KEY_MULTIPLAYER);
            gameState = bundle.getInt(KEY_GAME_STATE);
            possibleWords.clear();
            //noinspection ConstantConditions
            if (bundle.containsKey(KEY_POSSIBLE_WORDS)) {
                possibleWords.addAll(bundle.getStringArrayList(KEY_POSSIBLE_WORDS));
            }
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

    public void setPossibleWords(final ArrayList<String> possibleWords) {
        this.possibleWords.clear();
        this.possibleWords.addAll(possibleWords);
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
                view.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) { }

        @Override
        public void onAnimationRepeat(final Animator animation) { }
    }

    private void updateScreen(final boolean hasUsedLettersStat, final boolean hasInputTxtField) {
        ordet.setText(logik.getSynligtOrd());
        if (hasUsedLettersStat) usedLetters.append(theGuess);
        if (!logik.erSidsteBogstavKorrekt()) {
            final int wrongs = logik.getAntalForkerteBogstaver();
            galgen.setState(wrongs);
            YoYo.with(Techniques.Landing).duration(100).playOn(galgen);
        }
        if (hasInputTxtField) input.setText(null);
    }

    private void updateScreen() {
        updateScreen(true, true);
    }

    void CheckGameType() {//checks gametype, to se whether its a newgame by some extra info from activating classes, but that would require more refactoring
        isHotSeat = getArguments().getBoolean(GameUtility.KEY_IS_HOT_SEAT, false);
        Log.d("Play", "CheckGameType: isHotseat " + isHotSeat);
        if (isHotSeat) {
            possibleWords.add(getArguments().getString("theWord"));
        } else if (logik != null && logik.erSpilletSlut()) {
            logik.nulstil();
        } else {// for det tilfældes skyld at der er tale om et nyt spil
            possibleWords.addAll((HashSet<String>) s_prefereces.getObject(Constant.KEY_PREF_POSSIBLE_WORDS, HashSet.class));
        }
        logik = new Galgelogik(possibleWords);
    }

    private void StartEndgame() {// gathers need data for starting up the endgame Fragment
        // TODO : Erstat DU med spillerens navn og HAM med modstanderens navn..
        final EndOfGameFragment endOfGameFragment = EndOfGameFragment.newInstance(logik.erSpilletVundet(), logik.getAntalForkerteBogstaver(), logik.getOrdet(), "DU", "HAM", isHotSeat);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, endOfGameFragment).commit();
        Log.d("play", "finishing");
    }
    void guess(final String guess, final boolean isMultiButtonInterface) {
        final Boolean isMultiBtn = isMultiButtonInterface;

        theGuess = guess;
        if (theGuess.length() > 1) {
            theGuess = theGuess.substring(0, 1);
            logik.gætBogstav(theGuess);
            status.setText("Brug kun ét bogstav, resten vil blive ignoreret");
        } else {
            if (!isMultiBtn) status.setText("");
            logik.gætBogstav(theGuess);
        }

        if (!logik.erSpilletSlut()) {
            if (!isMultiBtn) updateScreen();
            else updateScreen(false, false); // because its the Hangman with mulityple buttons an no input fields
        } else {
            StartEndgame();
        }
    }

    void guess(final String guess) {
        guess(guess, true);
    }




}
