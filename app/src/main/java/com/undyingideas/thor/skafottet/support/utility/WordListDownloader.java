package com.undyingideas.thor.skafottet.support.utility;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.activities.WordListActivity;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

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

    private static final Pattern removeSpaces = Pattern.compile("  ");
    private static final Pattern removeNonDK = Pattern.compile("[^a-zæøå]");
    private static final Pattern removeTags = Pattern.compile("<.+?>");
    private final WeakReference<WordListActivity> wordListActivityWeakReference;
    private final String title, url;
    private MaterialDialog pd;

    public WordListDownloader(final WordListActivity wordListActivity, final String title, final String url) {
        wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        this.title = title;
        this.url = url;
    }

    private String downloadURL() throws IOException {
        final StringBuilder sb = new StringBuilder(500);
        int count = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String line = br.readLine();
            while (line != null) {
                if (++count % 50 == 0) {
                    publishProgress("Downloader.... " + Integer.toString(count), "Vent...");
                    count = 1;
                }
                sb.append(line);
                line = br.readLine();
            }
        } catch (final Exception e) {
            // meh
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        publishProgress("Udfritter resultatet", "Vent...");
        return removeSpaces.matcher(removeNonDK.matcher(removeTags.matcher(sb.toString()).replaceAll(" ").toLowerCase()).replaceAll(" ")).replaceAll("").trim();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        final WordListActivity wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            pd = new MaterialDialog.Builder(wordListActivity)
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

    @Override
    protected WordItem doInBackground(final Void... params) {
        final WordListActivity wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            publishProgress("Henter fra\n" + url, "Henter ordliste.");
            try {
                final ArrayList<String> words = new ArrayList<>();
                final HashSet<String> dude = new HashSet<>();
                final StringTokenizer stringTokenizer = new StringTokenizer(downloadURL(), " ");
                int count = stringTokenizer.countTokens();
                while (stringTokenizer.hasMoreTokens()) {
                    words.add(stringTokenizer.nextToken());
                    if (count % 25 == 0) {
                        publishProgress(words.get(words.size() - 1), "Gemmer liste. " + Integer.toString(words.size()) + " / " + Integer.toString(count));
                        count = stringTokenizer.countTokens();
                    }
                }
                for (int i = 0; i < words.size(); i++) {
                    if (words.get(i).length() < 4 || words.get(i).contains("w")) {
                        words.remove(i);
                    }
                }

                publishProgress("Fjerner duplanter og sorterer.");
                dude.addAll(words);
                final WordItem wordItem = new WordItem(title, url);
                wordItem.getWords().addAll(dude);
                Collections.sort(wordItem.getWords());
                if (s_wordController.existsLocal(wordItem)) {
                    s_wordController.replaceLocalWordList(wordItem);
                } else {
                    s_wordController.addLocalWordList(wordItem.getTitle(), wordItem.getUrl(), wordItem.getWords());
                }
                ListFetcher.saveWordLists(s_wordController, wordListActivity.getApplicationContext());
                return wordItem;
            } catch (final IOException ioe) {
                pd.dismiss();
                cancel(true);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final WordItem wordItem) {

        Log.d("Downloader", wordItem.toString());
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        final WordListActivity wordListActivity = wordListActivityWeakReference.get();
        if (wordListActivity != null) {
            wordListActivity.refreshList();
        }
        super.onPostExecute(wordItem);
    }

    @Override
    protected void onProgressUpdate(final CharSequence... values) {
        pd.setMessage(values[0]);
        if (values.length >= 2) {
            pd.setTitle(values[1]);
        }
        super.onProgressUpdate(values);
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
