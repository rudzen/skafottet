package com.undyingideas.thor.skafottet.support.utility;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.activities.WordListActivity;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

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
public class WordListDownloader extends AsyncTask<Void, CharSequence, WordItem> {

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
                    publishProgress(Integer.toString(count), "Downloader...");
                }
                sb.append(line);
            }
        } catch (final Exception e) {
            // meh
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        publishProgress("Udfritter resultatet", "Vent...");
        return Jsoup.parse(sb.toString()).text().toLowerCase();

//        return removeSpaces.matcher(removeNonDK.matcher(removeTags.matcher(sb.toString()).replaceAll(" ").toLowerCase()).replaceAll(" ")).replaceAll("").trim();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            if (wordListActivity.md == null || !wordListActivity.md.isShowing()) {
                wordListActivity.md = new MaterialDialog.Builder(wordListActivity)
                        .title("Indhenter " + title)
                        .content("Vent...")
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .negativeText("Afbryd")
                        .cancelable(true)
                        .cancelListener(new DownloaderOnCancelListener())
                        .show();
            }
        }
    }

    @Override
    protected WordItem doInBackground(final Void... params) {
        wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            publishProgress("Henter fra\n" + url, "Henter ordliste.");
            StringTokenizer stringTokenizer = null;
            try {
                stringTokenizer = new StringTokenizer(downloadURL(), " ");
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (stringTokenizer != null) {
                publishProgress("Forbereder ordlisten..");
                final ArrayList<String> words = new ArrayList<>();
                int wordSize = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    words.add(removeNonDK.matcher(stringTokenizer.nextToken()).replaceAll(""));
                    wordSize = words.size() - 1;
                    if (words.get(wordSize).length() < 4 || words.get(wordSize).contains("w")) {
                        words.remove(wordSize);
                    }
                    publishProgress(Integer.toString(wordSize), "Udfritter ord..");
                }

                final HashSet<String> dude = new HashSet<>(wordSize);
                publishProgress("Fjerner duplanter og sorterer.");
                dude.addAll(words);
                final WordItem wordItem = new WordItem(title, url, dude.size());
                wordItem.getWords().addAll(dude);
                Collections.sort(wordItem.getWords());
                s_wordController.replaceLocalWordList(wordItem);
                ListFetcher.saveWordLists(s_wordController, wordListActivity.getApplicationContext());
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
            if (wordListActivity.md != null && wordListActivity.md.isShowing()) {
                wordListActivity.md.dismiss();
            }
            wordListActivity.refreshList();
            wordListActivity.setProgressBar(false);
        }

        super.onPostExecute(wordItem);
    }

    @Override
    protected void onProgressUpdate(final CharSequence... values) {
        if (wordListActivity != null) {
            if (values.length == 2) {
                wordListActivity.md.setContent(String.format("Indlæser ord [%s] :%s%s", values[0], System.lineSeparator(), values[1]));
            } else {
                // values.length == 1
                wordListActivity.md.setContent(values[0]);
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

    private class DownloaderOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(final DialogInterface dialog) {
//            pd.dismiss();
        }
    }
}
