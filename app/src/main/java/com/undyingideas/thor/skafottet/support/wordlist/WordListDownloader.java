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

package com.undyingideas.thor.skafottet.support.wordlist;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.undyingideas.thor.skafottet.activities.WordListActivity;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_wordController;

/**
 * Created on 09-01-2016, 16:06.
 * Project : skafottet
 * Generic wordlist downloader as AsyncTask
 *
 * @author rudz
 */
public class WordListDownloader extends AsyncTask<Void, String, WordItem> {

//    private static final Pattern removeSpaces = Pattern.compile("  ");
    private static final Pattern removeNonDK = Pattern.compile("[^a-zæøå]");
//    private static final Pattern removeTags = Pattern.compile(".<+?>");
    private final WeakReference<WordListActivity> wordListActivityWeakReference;
    private final String title, url;
    private WordListActivity wordListActivity;
    private final boolean multi;

    public WordListDownloader(final WordListActivity wordListActivity, final String title, final String url) {
        this(wordListActivity, title, url, false);
    }

    public WordListDownloader(final WordListActivity wordListActivity, final String title, final String url, final boolean multi) {
        wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        this.title = title;
        this.url = url;
        this.multi = multi;
    }

    private String downloadURL() throws IOException {
        final StringBuilder sb = new StringBuilder(500);
        int count = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            while (true) {
                final String line = br.readLine();
                if (line == null) break;
                if (count++ % 10 == 0) {
                    publishProgress("Downloader... " + Integer.toString(count));
                }
                sb.append(line);
            }
        } catch (final Exception e) {
            // meh
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        publishProgress("Udfritter resultatet", "Vent...");
        return Jsoup.parse(sb.toString()).text();
    }

    @Override
    protected WordItem doInBackground(final Void... params) {
        wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {

            publishProgress(title); //"Henter fra\n" + url, "Henter ordliste.");
            StringTokenizer stringTokenizer = null;
            try {
                stringTokenizer = new StringTokenizer(downloadURL(), " ");
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (stringTokenizer != null) {
                final ArrayList<String> words = new ArrayList<>();
                int wordSize = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    words.add(removeNonDK.matcher(stringTokenizer.nextToken()).replaceAll("").toLowerCase());
                    wordSize = words.size() - 1;
                    if (words.get(wordSize).length() < 4 || words.get(wordSize).contains("w")) {
                        words.remove(wordSize);
                    }
                    publishProgress("Antal ord : " + Integer.toString(wordSize));
                    if (wordSize % 5 == 0) {
                        publishProgress(Integer.toString(wordSize), "Udfritter ord..");
                    }
                }

                final HashSet<String> dude = new HashSet<>(wordSize);
                publishProgress("Rydder op.");
                dude.addAll(words);
                final WordItem wordItem = new WordItem(title, url, dude.size());
                wordItem.getWords().addAll(dude);
                Collections.sort(wordItem.getWords());
                s_wordController.replaceLocalWordList(wordItem);
                return wordItem;
            } else {
                wordListActivity.md.dismiss();
                cancel(true);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final WordItem wordItem) {
        wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            Log.d("Downloader", wordItem.toString());
            wordListActivity.loadToast.success();
            wordListActivity.refreshList();
            wordListActivity.onWindowFocusChanged(true);
            wordListActivity.loadToast.success();
        }
        super.onPostExecute(wordItem);
    }

    @Override
    protected void onProgressUpdate(final String... values) {
        if (wordListActivity != null) {
            if (values.length == 2) {
                wordListActivity.loadToast.setText(String.format("Indlæser ord [%s] :%s%s", values[0], System.lineSeparator(), values[1]));
            } else {
                wordListActivity.loadToast.setText(values[0]);
            }
        }
    }

    @Override
    protected void onCancelled() {
//        final AppCompatActivity appCompatActivity = wordListActivityWeakReference.get();
//        if (appCompatActivity != null && pd != null && pd.isShowing()) pd.dismiss();
//        Toast.makeText(appCompatActivity, "Download afbrudt.", Toast.LENGTH_SHORT).show();
//        super.onCancelled();
    }

    private static class DownloaderOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(final DialogInterface dialog) {
//            pd.dismiss();
        }
    }
}
