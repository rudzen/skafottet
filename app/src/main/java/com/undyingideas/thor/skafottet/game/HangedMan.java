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

package com.undyingideas.thor.skafottet.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.StringHelper;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
/**
 * Based on the original by J. Nordfalk.
 *
 * @author rudz
 */
public class HangedMan implements Parcelable {

    private static final Pattern ambersamRepl = Pattern.compile("&");

    private static final String TAG = "HangedMan";

    private ArrayList<String> usedLetters = new ArrayList<>();

    @Nullable
    private String theWord;

    private String visibleWord;

    private int numWrongLetters;

    private int numCorrectLettersLast;

    private boolean lastLetterCorrect;

    private boolean isGameWon;

    private boolean isGameLost;

    public HangedMan() {
        theWord = null;
        reset(true);
    }

    public HangedMan(@NonNull final String theWord) {
        this.theWord = theWord.toLowerCase();
        reset(false);
    }

    public boolean isGameOver() {
        return isGameWon || isGameLost;
    }

    private void reset(final boolean newWord) {
        usedLetters.clear();
        numWrongLetters = 0;
        numCorrectLettersLast = 0;
        isGameLost = false;
        isGameWon = false;
        if (newWord) theWord = GameUtility.getWordController().getRandomWord().toLowerCase();
        visibleWord = theWord != null ? updateVisibleWord(new StringBuilder(!theWord.isEmpty() ? theWord.length() : 0)).toString() : updateVisibleWord(new StringBuilder(0)).toString();
    }

    private StringBuilder updateVisibleWord(@NonNull final StringBuilder stringBuilder) {
        if (theWord != null) {
            for (int n = 0; n < theWord.length(); n++) {
                final String letter = theWord.substring(n, n + 1);
                stringBuilder.append(usedLetters.contains(letter) ? letter : Objects.equals(letter, " ") ? ' ' : '*');
            }
        }
        return stringBuilder;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public void guessLetter(@NonNull final String letter) {
        if (isGameWon || isGameLost || letter.length() != 1 || visibleWord.contains(letter)) return;
        Log.d(TAG, "Der gættes på bogstavet: " + letter);

        usedLetters.add(letter);

        if (theWord != null) {
            lastLetterCorrect = theWord.contains(letter);
        }

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
        Log.d(TAG, "SPILLET ER" + (isGameLost ? " TABT" : isGameWon ? " VUNDET" : " I GANG"));
        Log.d(TAG, "---------------------");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(75);
        sb.append("---------------------\n");
        sb.append("| ordet (skjult)    = ").append(theWord).append('\n');
        sb.append("| synligtOrd        = ").append(visibleWord).append('\n');
        sb.append("| forkerteBogstaver = ").append(numWrongLetters).append('\n');
        sb.append("| brugeBogstaver    = ").append(usedLetters).append('\n');
        sb.append("---------------------\n");
        sb.append("SPILLET ER").append(isGameLost ? " TABT" : isGameWon ? " VUNDET" : " I GANG").append('\n');
        sb.append("---------------------\n");
        sb.trimToSize();
        return sb.toString();
//        return "HangedMan{" + "usedLetters=" + usedLetters + ", theWord='" + theWord + '\'' + ", visibleWord='" + visibleWord + '\'' + ", numWrongLetters=" + numWrongLetters + ", numCorrectLettersLast=" + numCorrectLettersLast + ", lastLetterCorrect=" + lastLetterCorrect + ", isGameWon=" + isGameWon + ", isGameLost=" + isGameLost + '}';
    }

    /* ************************************* */
    /* ************************************* */
    /* ******** Getters & Setters ********** */
    /* ************************************* */
    /* ************************************* */

    @Nullable
    public String getTheWord() { return theWord; }

    public ArrayList<String> getUsedLetters() { return usedLetters; }

    public String getVisibleWord() { return visibleWord; }

    public int getNumWrongLetters() { return numWrongLetters; }

    public int getNumCorrectLettersLast() { return numCorrectLettersLast; }

    public boolean isGameLost() { return isGameLost; }

    public boolean isGameWon() { return isGameWon; }

    public boolean isLastLetterCorrect() { return lastLetterCorrect; }


    public void setTheWord(@Nullable final String theWord) { this.theWord = theWord; }

    public void setUsedLetters(final ArrayList<String> usedLetters) { this.usedLetters = usedLetters; }

    public void setVisibleWord(final String visibleWord) { this.visibleWord = visibleWord; }

    public void setNumWrongLetters(final int numWrongLetters) { this.numWrongLetters = numWrongLetters; }

    public void setNumCorrectLettersLast(final int numCorrectLettersLast) { this.numCorrectLettersLast = numCorrectLettersLast; }

    public void setIsGameLost(final boolean isGameLost) { this.isGameLost = isGameLost; }

    public void setIsGameWon(final boolean isGameWon) { this.isGameWon = isGameWon; }

    public void setLastLetterCorrect(final boolean lastLetterCorrect) { this.lastLetterCorrect = lastLetterCorrect; }


    /* ************************************* */
    /* ************************************* */
    /* ************** Parcel  ************** */
    /* ************************************* */
    /* ************************************* */

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

    protected HangedMan(final Parcel in) {
        theWord = in.readString();
        usedLetters = in.createStringArrayList();
        visibleWord = in.readString();
        numWrongLetters = in.readInt();
        numCorrectLettersLast = in.readInt();
        lastLetterCorrect = in.readByte() != 0;
        isGameWon = in.readByte() != 0;
        isGameLost = in.readByte() != 0;
    }

    public static final Creator<HangedMan> CREATOR = new HangedManCreator();

    private static class HangedManCreator implements Creator<HangedMan> {
        @Override
        public HangedMan createFromParcel(final Parcel source) {return new HangedMan(source);}

        @Override
        public HangedMan[] newArray(final int size) {return new HangedMan[size];}
    }
}
