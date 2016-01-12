/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple wrapper class to be able to save and load WordList object.
 * <p>This is not very pretty, but it is condensed code (somewhat).</p>
 *
 * ** DESIGNED FOR SINGLE FILE USE **
 * ** FOR LOOPED OBJECT SAVING, PLEASE MODIFY FIRST FOR MORE EFFICIENT SAVING/LOADING **
 *
 * Created by rudz on 11-11-2015.
 * @author rudz
 */
public class ObjectFileHandler<E> {

    private int bufferSize;

    public ObjectFileHandler(final int buffer) {
        bufferSize = buffer;
    }

    public ObjectFileHandler() {
        this(16384);
    }

    private byte[] compressObject(final E object) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        try (final ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut)) {
            objectOut.writeObject(object);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return baos.toByteArray();
    }

    @SuppressWarnings({"unchecked", "IOResourceOpenedButNotSafelyClosed"})
    private E decompressWordList(final byte[] data) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final GZIPInputStream gzipIn = new GZIPInputStream(bais);
        final ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
        final E obj = (E) objectIn.readObject();
        objectIn.close();
        gzipIn.close();
        bais.close();
        return obj;
    }

    public E loadObject(final Context context, final String filename) {
        try {
            // load data
            final FileInputStream inputStream = context.openFileInput(filename);
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            final byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);
            buffer.flush();
            return decompressWordList(buffer.toByteArray());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveObject(final E object, final Context context, final String filename) {
        try {
            final byte[] comp = compressObject(object);
            final FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(comp);
            outputStream.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
