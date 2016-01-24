
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

package com.undyingideas.thor.skafottet.support.highscore.local;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

class HighscoreManager {
    private static final String TAG = "HighScoreLoc";
    private ArrayList<Score> scores;
    private static final String HIGHSCORE_FILE = "scores.dat";

    private ObjectOutputStream outputStream;

    private final static int MAX = 10;

    public HighscoreManager() {
        scores = new ArrayList<>();
    }

    public ArrayList<Score> getScores(final Context c) {
        loadScoreFile(c);
        sort();
        return scores;
    }

    private void sort() {
        Collections.sort(scores, new ScoreComparator());
    }

    public boolean checkScore(final int score) {
        return score >= scores.get(scores.size() - 1).getScore();
    }

    public void addScore(final String word, final String name, final int score, final Context c) {
        loadScoreFile(c);
        final Calendar cal = Calendar.getInstance();
        scores.add(new Score(word, name, score, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        Collections.sort(scores, new ScoreComparator());
        if (scores.size() > 10) while (scores.size() > 10) scores.remove(scores.size() - 1);
        updateScoreFile(c);
    }

    private void loadScoreFile(final Context context) {
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(HIGHSCORE_FILE));
            scores = (ArrayList<Score>) inputStream.readObject();
            if (scores.size() < MAX) {
                final Calendar cal = Calendar.getInstance();
                for (int i = 1; i < MAX - scores.size(); i++) {
                    scores.add(new Score("Gruppe 23", "rudz.dk", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
                }
            }
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "[Load] FNF Error: " + e.getMessage());
        } catch (final IOException e) {
            Log.e(TAG, "[Load] IO Error: " + e.getMessage());
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "[Load] CNF Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (final IOException e) {
                Log.e(TAG, "[Load] IO Error: " + e.getMessage());
            }
        }
    }

    private void updateScoreFile(final Context c) {
        try {
            outputStream = new ObjectOutputStream(c.openFileOutput(HIGHSCORE_FILE, Context.MODE_PRIVATE));
            scores.trimToSize();
            outputStream.writeObject(scores);
        } catch (final FileNotFoundException e) {
            Log.e("HighScore", "[Update] FNF Error: " + e.getMessage() + ", the program will try and make a new file");
        } catch (final IOException e) {
            Log.e("HighScore", "[Update] IO Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (final IOException e) {
                Log.e("HighScore", "[Update] Error: " + e.getMessage());
            }
        }
    }

    public String getHighscoreString() {
        final StringBuilder sb = new StringBuilder((MAX << 4) + (MAX << 3));
        int x = scores.size();
        if (x > MAX) x = MAX;
        for (int i = 0; i < x; i++) {
            sb.append(i + 1);
            sb.append(".\t"); // padding can be inserted here if wanted!!!!
            sb.append(scores.get(i).getName());
            sb.append(" [");
            sb.append(scores.get(i).getScore());
            sb.append(']');
        }
        return sb.toString();
    }

    public static boolean deleteHighScore(final Context c) {
        return c.deleteFile(HIGHSCORE_FILE);
    }

    private void generateDefaults() {
        final Calendar cal = Calendar.getInstance();

        scores.add(new Score("Gruppe 23", "Rudy", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Thor", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Adam", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Emil", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Theis", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Christoffer", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Papagøjs", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Ole Olsen", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Franz Jäger", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.add(new Score("Gruppe 23","Anker", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        scores.clear();
//        for (int i = 1; i <= MAX; i++) {
//            scores.add(new Score("rudz.dk", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
//        }
    }

    /**
     * Interpolation search.<br>
     * Log Log n
     *
     * @param A
     *         The array to search in
     * @param val
     *         The value to search for
     * @return the index of found value, or -1 if fails.
     */
    public static int interpolationSearch(final int[] A, final int val) {
        final int len = A.length;
        if (len == 0) return -1;
        else if (len == 1 && A[0] == val) return 0;
        int mid, low = 0, high = len - 1;
        while (A[low] <= val && A[high] >= val) {
            mid = low + (val - A[low]) * (high - low) / (A[high] - A[low]);
            if (mid < 0) mid = 0;
            else if (mid > len - 1) mid = len;
            if (A[mid] < val) low = mid + 1;
            else if (A[mid] > val) high = mid - 1;
            else return mid;
        }
        if (A[low] == val) return low;
        return -1;
    }
}
