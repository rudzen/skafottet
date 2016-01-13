package com.undyingideas.thor.skafottet.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.StringHelper;

import java.util.ArrayList;

public class Hanged21 implements Parcelable {

    private String theWord;
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int numWrongLetters;
    private int numCorrectLettersLast;
    private boolean lastLetterCorrect;
    private boolean isGameWon;
    private boolean isGameLost;

    private final static String TAG = "Hanged21";

    public Hanged21() {
    }

    public Hanged21(final String theWord) {
        this.theWord = theWord;
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
        theWord = GameUtility.words.getRandomWord();
        updateVisibleWord();
    }

    private void updateVisibleWord() {
        visibleWord = "";
        for (int n = 0; n < theWord.length(); n++) {
            final String letter = theWord.substring(n, n + 1);
            visibleWord += usedLetters.contains(letter) ? letter : "*";
        }
        isGameWon = !visibleWord.contains("*");
    }

    public void guessLetter(final String letter) {
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
        updateVisibleWord();
        logStatus();
    }

    private void logStatus() {
        Log.d(TAG, "---------------------");
        Log.d(TAG, "| ordet (skjult)    = " + theWord);
        Log.d(TAG, "| synligtOrd        = " + visibleWord);
        Log.d(TAG, "| forkerteBogstaver = " + Integer.toString(numWrongLetters));
        Log.d(TAG, "| brugeBogstaver    = " + usedLetters);
        Log.d(TAG, "---------------------");
        if (isGameLost) Log.d(TAG, "| SPILLET ER TABT");
        else if (isGameWon) Log.d(TAG, "| SPILLET ER VUNDET");
        Log.d(TAG, "---------------------");
    }

    public String getTheWord() {
        return theWord;
    }

    public void setTheWord(final String theWord) {
        this.theWord = theWord;
    }

    public ArrayList<String> getUsedLetters() {
        return usedLetters;
    }

    public void setUsedLetters(final ArrayList<String> usedLetters) {
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

    private Hanged21(final Parcel in) {
        theWord = in.readString();
        usedLetters = in.createStringArrayList();
        visibleWord = in.readString();
        numWrongLetters = in.readInt();
        numCorrectLettersLast = in.readInt();
        lastLetterCorrect = in.readByte() != 0;
        isGameWon = in.readByte() != 0;
        isGameLost = in.readByte() != 0;
    }

    public static final Creator<Hanged21> CREATOR = new HangedCreator();

    private static class HangedCreator implements Creator<Hanged21> {
        @Override
        public Hanged21 createFromParcel(final Parcel source) {return new Hanged21(source);}

        @Override
        public Hanged21[] newArray(final int size) {return new Hanged21[size];}
    }
}
