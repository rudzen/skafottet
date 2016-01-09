package com.undyingideas.thor.skafottet.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.undyingideas.thor.skafottet.utility.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Hanged implements Parcelable {

//    private static final long serialVersionUID = 23111;

    private String theWord;

    private ArrayList<String> possibleWords = new ArrayList<>();
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int numWrongLetters;
    private int numCorrectLettersLast;
    private boolean lastLetterCorrect;
    private boolean isGameWon;
    private boolean isGameLost;

    private final static String TAG = "GalgeLogik2";

    public Hanged() {
    }

    public Hanged(final boolean defaults) {
        if (defaults) setDefaults();
    }

    public Hanged(final String theWord) {
        this.theWord = theWord;
        reset();
    }

    public Hanged(final ArrayList<String> def_list) {
        if (!def_list.isEmpty()) {
            possibleWords.addAll(def_list);
            Collections.sort(possibleWords);
        } else setDefaults();
        reset(4);
    }

    private void setDefaults() {
        possibleWords.add("bil");
        possibleWords.add("busrute");
        possibleWords.add("gangsti");
        possibleWords.add("solsort");
        possibleWords.add("computer");
        possibleWords.add("motorvej");
        possibleWords.add("skovsnegl");
        possibleWords.add("programmering");
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
        updateVisibleWord();
    }

    public void reset(final int minWordLenght) {
        usedLetters.clear();
        numWrongLetters = 0;
        numCorrectLettersLast = 0;
        isGameLost = false;
        isGameWon = false;
        theWord = "";
        final Random r = new Random();
        while (theWord.length() < minWordLenght) theWord = possibleWords.get(r.nextInt(possibleWords.size()));
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

    public void updatePossibleWords(final ArrayList<String> nyeOrd) {
        if (!possibleWords.isEmpty()) possibleWords.clear();
        possibleWords.addAll(nyeOrd);
        Log.d(TAG, "muligeOrd størrelse : " + possibleWords.size());
        reset(4);
    }

    public ArrayList<String> getPossibleWords() {
        return possibleWords;
    }

    public void setPossibleWords(final ArrayList<String> possibleWords) {
        this.possibleWords.clear();
        this.possibleWords.addAll(possibleWords);
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
        dest.writeStringList(possibleWords);
        dest.writeStringList(usedLetters);
        dest.writeString(visibleWord);
        dest.writeInt(numWrongLetters);
        dest.writeInt(numCorrectLettersLast);
        dest.writeByte(lastLetterCorrect ? (byte) 1 : (byte) 0);
        dest.writeByte(isGameWon ? (byte) 1 : (byte) 0);
        dest.writeByte(isGameLost ? (byte) 1 : (byte) 0);
    }

    private Hanged(final Parcel in) {
        theWord = in.readString();
        possibleWords = in.createStringArrayList();
        usedLetters = in.createStringArrayList();
        visibleWord = in.readString();
        numWrongLetters = in.readInt();
        numCorrectLettersLast = in.readInt();
        lastLetterCorrect = in.readByte() != 0;
        isGameWon = in.readByte() != 0;
        isGameLost = in.readByte() != 0;
    }

    public static final Creator<Hanged> CREATOR = new HangedCreator();

    private static class HangedCreator implements Creator<Hanged> {
        @Override
        public Hanged createFromParcel(final Parcel source) {return new Hanged(source);}

        @Override
        public Hanged[] newArray(final int size) {return new Hanged[size];}
    }
}
