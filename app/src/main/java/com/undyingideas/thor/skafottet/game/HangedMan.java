package com.undyingideas.thor.skafottet.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.StringHelper;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class HangedMan implements Parcelable {

    private String theWord;
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int numWrongLetters;
    private int numCorrectLettersLast;
    private boolean lastLetterCorrect;
    private boolean isGameWon;
    private boolean isGameLost;

    private final static String TAG = "HangedMan";

    public HangedMan() {
        theWord = null;
        reset();
    }

    public HangedMan(@NonNull final String theWord) {
        this.theWord = theWord.toLowerCase();
        reset();
    }

    public boolean isGameOver() {
        return isGameWon || isGameLost;
    }

    private void reset() {
        usedLetters.clear();
        numWrongLetters = 0;
        numCorrectLettersLast = 0;
        isGameLost = false;
        isGameWon = false;
        if (theWord == null) theWord = GameUtility.s_wordController.getRandomWord().toLowerCase();
        visibleWord = updateVisibleWord(new StringBuilder(theWord.length())).toString();
    }

    private StringBuilder updateVisibleWord(@NonNull final StringBuilder stringBuilder) {
        for (int n = 0; n < theWord.length(); n++) {
            final String letter = theWord.substring(n, n + 1);
            stringBuilder.append(usedLetters.contains(letter) ? letter : Objects.equals(letter, " ") ? ' ' : '*');
        }
        return stringBuilder;
    }

    public void guessLetter(@NonNull final String letter) {
        if (letter.length() != 1) return;
        if (visibleWord.contains(letter) || isGameWon || isGameLost) return;
        Log.d(TAG, "Der gættes på bogstavet: " + letter);

        usedLetters.add(letter);

        lastLetterCorrect = theWord.contains(letter);

        if (lastLetterCorrect) {
            numCorrectLettersLast = StringHelper.countString(theWord, letter);
            Log.d(TAG, "Bogstavet var korrekt : " + letter);
        } else {
            numCorrectLettersLast = 0;
            // Vi gættede på et bogstav der ikke var i ordet.
            Log.d(TAG, "Bogstavet var ikke korrekt : " + letter);
            if (++numWrongLetters > 6) isGameLost = true;
        }
        visibleWord = updateVisibleWord(new StringBuilder(theWord.length())).toString();
        isGameWon = !visibleWord.contains("*");
        logStatus();
    }

    private void logStatus() {
        Log.d(TAG, "---------------------");
        Log.d(TAG, "| ordet (skjult)    = " + theWord);
        Log.d(TAG, "| synligtOrd        = " + visibleWord);
        Log.d(TAG, "| forkerteBogstaver = " + Integer.toString(numWrongLetters));
        Log.d(TAG, "| brugeBogstaver    = " + usedLetters);
        Log.d(TAG, "---------------------");
        Log.d(TAG, "SPILLET ER " + (isGameLost ? " TABT" : isGameWon ? " VUNDET" : " I GANG"));
        Log.d(TAG, "---------------------");
    }

    public String getTheWord() {
        return theWord;
    }

    public void setTheWord(@NonNull final String theWord) {
        this.theWord = theWord;
    }

    public ArrayList<String> getUsedLetters() {
        return usedLetters;
    }

    public void setUsedLetters(@NonNull final ArrayList<String> usedLetters) {
        this.usedLetters.clear();
        this.usedLetters.addAll(usedLetters);
    }

    public String getVisibleWord() {
        return visibleWord;
    }

    public void setVisibleWord(final String visibleWord) {
        this.visibleWord = visibleWord;
    }

    public int getNumWrongLetters() {
        return numWrongLetters;
    }

    public void setNumWrongLetters(final int numWrongLetters) {
        this.numWrongLetters = numWrongLetters;
    }

    public int getNumCorrectLettersLast() {
        return numCorrectLettersLast;
    }

    public void setNumCorrectLettersLast(final int numCorrectLettersLast) {
        this.numCorrectLettersLast = numCorrectLettersLast;
    }

    public boolean isLastLetterCorrect() {
        return lastLetterCorrect;
    }

    public void setLastLetterCorrect(final boolean lastLetterCorrect) {
        this.lastLetterCorrect = lastLetterCorrect;
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    public void setGameWon(final boolean gameWon) {
        isGameWon = gameWon;
    }

    public boolean isGameLost() {
        return isGameLost;
    }

    public void setGameLost(final boolean gameLost) {
        isGameLost = gameLost;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(theWord);
        dest.writeStringList(usedLetters);
        dest.writeString(visibleWord);
        dest.writeInt(numWrongLetters);
        dest.writeInt(numCorrectLettersLast);
        dest.writeByte(lastLetterCorrect ? (byte) 1 : (byte) 0);
        dest.writeByte(isGameWon ? (byte) 1 : (byte) 0);
        dest.writeByte(isGameLost ? (byte) 1 : (byte) 0);
    }

    private HangedMan(final Parcel in) {
        theWord = in.readString();
        usedLetters = in.createStringArrayList();
        visibleWord = in.readString();
        numWrongLetters = in.readInt();
        numCorrectLettersLast = in.readInt();
        lastLetterCorrect = in.readByte() != 0;
        isGameWon = in.readByte() != 0;
        isGameLost = in.readByte() != 0;
    }

    public static final Creator<HangedMan> CREATOR = new HangedCreator();

    private static class HangedCreator implements Creator<HangedMan> {
        @Override
        public HangedMan createFromParcel(final Parcel source) {return new HangedMan(source);}

        @Override
        public HangedMan[] newArray(final int size) {return new HangedMan[size];}
    }
}
