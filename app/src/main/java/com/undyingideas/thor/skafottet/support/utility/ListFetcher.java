package com.undyingideas.thor.skafottet.support.utility;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.undyingideas.thor.skafottet.support.wordlist.WordController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple wrapper class to be able to save and load compressed WordController object.
 * <p>
 * Could easily be generalised.. QUICK AND DIRTY HACK!
 * </p>
 * @author rudz
 */
public final class ListFetcher {

    private static final String filename = "words.dat";

    public static Handler listHandler = new Handler();
    public static Runnable listSaver;

    private static byte[] compressWordList(final WordController wordController) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        try (final ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut)) {
            objectOut.writeObject(wordController);
            objectOut.close();
        }
        return baos.toByteArray();
    }

    private static WordController decompressWordList(final byte[] data) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final GZIPInputStream gzipIn = new GZIPInputStream(bais);
        final ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
        final WordController wordController = (WordController) objectIn.readObject();
        objectIn.close();
        return wordController;
    }

    public static WordController loadWordList(final Context context) {
        try {
            // load data
            final FileInputStream inputStream = context.openFileInput(filename);
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            final byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);
            buffer.flush();
            final byte[] returnBytes = buffer.toByteArray();
            Log.d(filename, filename + " file size (on disk) : " + buffer.size());
            buffer.close();
            return decompressWordList(returnBytes);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveWordLists(final WordController wordController, final Context context) {
        try {
            final byte[] comp = compressWordList(wordController);
            final FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(comp);
            outputStream.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteList(final Context context) {
        return context.deleteFile(filename);
    }

    public static String fileLocation(final Context context) {
        return context.getFileStreamPath(filename).getAbsolutePath();
    }

    public static class ListSaver implements Runnable {

        final WeakReference<Context> contextWeakReference;

        public ListSaver(final Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void run() {
            final Context context = contextWeakReference.get();
            if (context != null) {
                saveWordLists(GameUtility.s_wordController, context);
            }
        }
    }

}
