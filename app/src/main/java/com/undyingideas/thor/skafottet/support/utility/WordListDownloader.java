package com.undyingideas.thor.skafottet.support.utility;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created on 09-01-2016, 16:06.
 * Project : skafottet
 * Generic wordlist downloader as AsyncTask
 *
 * @author rudz
 */
public class WordListDownloader extends AsyncTask<Void, CharSequence, ArrayList<String>> {

    private static final Pattern removeSpaces = Pattern.compile("  ");
    private static final Pattern removeNonDK = Pattern.compile("[^a-zæøå]");
    private static final Pattern removeTags = Pattern.compile("<.+?>");
    private final WeakReference<Fragment> fragmentWeakReference;
    private final WordItem wordItem;
    private ProgressDialog pd;

    public WordListDownloader(final Fragment fragment, final WordItem wordItem) {
        fragmentWeakReference = new WeakReference<>(fragment);
        this.wordItem = wordItem;
    }

    private String downloadURL() throws IOException {
        final StringBuilder sb = new StringBuilder(500);
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new URL(wordItem.getUrl()).openStream()))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
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
        final Fragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            pd = new ProgressDialog(fragment.getContext());
        }
    }

    @Override
    protected ArrayList<String> doInBackground(final Void... params) {
        final Fragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            publishProgress("Henter fra\n" + wordItem.getUrl(), "Henter ordliste.");
            final ArrayList<String> words = new ArrayList<>(100);
            try {
                final StringTokenizer stringTokenizer = new StringTokenizer(downloadURL(), "\n");
                pd.setMax(stringTokenizer.countTokens());
                while (stringTokenizer.hasMoreTokens()) {
                    words.add(stringTokenizer.nextToken());
                    if (words.size() % 25 == 0) {
                        publishProgress(words.get(words.size() - 1), "Gemmer liste.", Integer.toString(words.size()));
                    }
                }
            } catch (final IOException ioe) {
                pd.dismiss();
                cancel(true);
            }
            return words;
        }
        return null;
    }

    @Override
    protected void onPostExecute(final ArrayList<String> strings) {
        super.onPostExecute(strings);
    }

    @Override
    protected void onProgressUpdate(final CharSequence... values) {
        pd.setMessage(values[0]);
        if (values.length >= 2) {
            pd.setTitle(values[1]);
            if (values.length == 3) {
                pd.setProgress(Integer.valueOf(values[2].toString()));
            }
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(final ArrayList<String> strings) {
        super.onCancelled(strings);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
