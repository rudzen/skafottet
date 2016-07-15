
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

package com.undyingideas.thor.skafottet.support.highscore.local;

import android.content.Context;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class HighscoreManager {

    private static final String TAG = "HighScoreLoc";

    private static final String HIGHSCORE_FILE = "scores.dat";

    private final Context context;

    private ObjectOutputStream outputStream;

    private ArrayList<Score> scores;

    private final static int MAX = 10;

    public HighscoreManager(final Context context) {
        scores = new ArrayList<>();
        this.context = context;
    }

    public ArrayList<Score> getScores() {
        return scores;
    }

    private void sort() {
//        HashSet<Score> mmYeah = new HashSet<>();
        Log.d(TAG, "Before sort : " + scores.toString());
        Collections.sort(scores);
//        mmYeah.addAll(scores);
//        scores.clear();
//        int count = 0;
//
//        Iterator<Score> iterator = mmYeah.iterator();
//        while (count++ < MAX && iterator.hasNext()) {
//            scores.add(iterator.next());
//        }
        Log.d(TAG, "After sort : " + scores.toString());
    }

    /**
     * Check if the score parsed is in range of the current list.
     *
     * @param score The score to check.
     * @return true if score can be added, otherwise false.
     */
    public int checkScore(final int score) {
        if (scores.isEmpty()) {
            return 0;
        }
        if (scores.size() < MAX) {
            return scores.size();
        }
        if (scores.size() >= MAX && score >= scores.get(MAX - 1).getmScore()) {
            return MAX - 1;
        }
        return -1;
    }

    public int addScore(final String word, final String name, final int points) {
        sort();
        final int pos = checkScore(points);
        if (pos > -1) {
            scores.add(pos, new Score(word, name, points, Calendar.getInstance().getTime()));
            sort();
        }
        return pos;
    }

    public int addScore(final String word, final PlayerDTO player) {
        return addScore(word, player.getName(), player.getScore());
    }

    public void loadScoreFile() {
        boolean loaded = false;
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(HIGHSCORE_FILE));
            scores = (ArrayList<Score>) inputStream.readObject();
            Collections.sort(scores);
            loaded = true;
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
            if (!loaded) {
                /* generate an empty highscore list to avoid crashes */
                for (int i = 0; i < 10; i++) {
                    GameUtility.getHighscoreManager().addScore("skafottet", "rudz", 100 + i);
                }
                GameUtility.getHighscoreManager().saveHighScore();
            }
        }
    }

    public void saveHighScore() {
        try {

            outputStream = new ObjectOutputStream(context.openFileOutput(HIGHSCORE_FILE, Context.MODE_PRIVATE));
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
            sb.append(scores.get(i).getmName());
            sb.append(" [");
            sb.append(scores.get(i).getmScore());
            sb.append(']');
            sb.append('\n');
        }
        return sb.toString();
    }

    public static boolean deleteHighScore(final Context c) { return c.deleteFile(HIGHSCORE_FILE); }

}
