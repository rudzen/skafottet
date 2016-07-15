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

package com.undyingideas.thor.skafottet.support.wordlist;

import android.os.AsyncTask;
import android.util.Log;

import com.undyingideas.thor.skafottet.fragments.WordListFragment;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.ListFetcher;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

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

/**
 * Created on 09-01-2016, 16:06.
 * Project : skafottet
 * Generic wordlist downloader as AsyncTask
 *
 * @author rudz
 */
public class WordListDownloader extends AsyncTask<Void, String, WordItem> {

    private static final Pattern removeNonDK = Pattern.compile("[^a-zæøå]");
    private final WeakReference<WordListFragment> mWordListFragmentWeakReference;
    private final String mTitle;
    private final String mURL;
    private WordListFragment mWordListFragment;
    private final boolean multi;

    public WordListDownloader(final WordListFragment wordListFragment, final String title, final String url) {
        this(wordListFragment, title, url, false);
    }

    private WordListDownloader(final WordListFragment wordListFragment, final String title, final String url, final boolean multi) {
        mWordListFragmentWeakReference = new WeakReference<>(wordListFragment);
        mTitle = title;
        mURL = url.toLowerCase();
        this.multi = multi;
    }

    @Override
    protected WordItem doInBackground(final Void... params) {
        mWordListFragment = mWordListFragmentWeakReference.get();
        if (mWordListFragment != null) {

            publishProgress("Henter fra " + mURL);
            try {
                Thread.sleep(300);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            final StringBuilder sb = new StringBuilder(500);
            int count = 1;
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(new URL(mURL).openStream()))) {
                while (true) {
                    final String line = br.readLine();
                    if (line == null) break;
                    if (count++ % 50 == 0) {
                        publishProgress(Integer.toString(count));
                    }
                    sb.append(line);
                }
            } catch (final IOException e) {
                // meh
                e.printStackTrace();
                return null;
            }
            sb.trimToSize();
            final StringTokenizer stringTokenizer = new StringTokenizer(Jsoup.parse(sb.toString()).text(), " ");
            sb.setLength(0);
            final ArrayList<String> words = new ArrayList<>();
            int wordSize = 0;
            publishProgress("Diskripanterer..");
            try {
                Thread.sleep(300);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            while (stringTokenizer.hasMoreTokens()) {
                words.add(removeNonDK.matcher(stringTokenizer.nextToken().toLowerCase()).replaceAll(""));
                wordSize = words.size() - 1;
                if (words.get(wordSize).length() < 4 || words.get(wordSize).contains("w")) {
                    words.remove(wordSize);
                }
                if (wordSize % 25 == 0) {
                    publishProgress(Integer.toString(wordSize));
                }
            }

            final HashSet<String> dude = new HashSet<>(wordSize);
            publishProgress("Rydder op.");
            dude.addAll(words);
            final WordItem wordItem = new WordItem(mTitle, mURL, dude.size());
            wordItem.getWords().addAll(dude);
            Collections.sort(wordItem.getWords());
            GameUtility.getWordController().replaceLocalWordList(wordItem);
            return wordItem.getWords().isEmpty() ? null : wordItem;
        }
        return null;
    }

    @Override
    protected void onPostExecute(final WordItem wordItem) {
        mWordListFragment = mWordListFragmentWeakReference.get();
        if (mWordListFragment != null) {
            WindowLayout.hideStatusBar(mWordListFragment.getActivity().getWindow(), null); // sometimes the system just won't do as i ask!
            if (wordItem != null) {
                Log.d("Downloader", wordItem.toString());
                ListFetcher.getListHandler().post(ListFetcher.getListSaver());
                mWordListFragment.refreshList();
                WindowLayout.getLoadToast().success();
            } else {
                WindowLayout.showSnack("Fejl ved indhentning af listen", mWordListFragment.getActivity().getWindow().getDecorView(), true);
                WindowLayout.getLoadToast().error();
            }
        }
        super.onPostExecute(wordItem);
    }

    @Override
    protected void onProgressUpdate(final String... values) {
        mWordListFragment = mWordListFragmentWeakReference.get();
        if (mWordListFragment != null) {
            WindowLayout.getLoadToast().setText(values[0]);
        }
    }
}
