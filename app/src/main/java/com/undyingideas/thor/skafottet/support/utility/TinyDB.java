/*
 * Copyright 2014 KC Ochibili
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

/*
 *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
 *  and unicode 2017 that are used for separating the items in a list.
 */

/*
 * Minor adjustments and small improvements by rudz.
 */
package com.undyingideas.thor.skafottet.support.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


@SuppressWarnings({"ClassNamingConvention", "ClassWithTooManyMethods", "unused", "BooleanMethodNameMustStartWithQuestion"})
public class TinyDB {

    private static final String DELIM = "‚‗‚";
    private final SharedPreferences mPreferences;
    private String mDefaultAppImagedataDirectory;
    private String mLastImagePath = "";

    public TinyDB(final Context appContext) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    /**
     * Check if external storage is writable or not
     *
     * @return true if writable, false otherwise
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Check if external storage is readable or not
     *
     * @return true if readable, false otherwise
     */
    public static boolean isExternalStorageReadable() {
        final String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Decodes the Bitmap from 'path' and returns it
     *
     * @param path
     *         image path
     * @return the Bitmap from 'path'
     */
    public static Bitmap getImage(final String path) {
        Bitmap bitmapFromPath = null;
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path);
        } catch (final Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return bitmapFromPath;
    }

    /**
     * Returns the String path of the last saved image
     *
     * @return string path of the last saved image
     */
    public final String getSavedImagePath() {
        return mLastImagePath;
    }

    /**
     * Saves 'theBitmap' into folder 'theFolder' with the name 'theImageName'
     *
     * @param theFolder
     *         the folder path dir you want to save it to e.g "DropBox/WorkImages"
     * @param theImageName
     *         the name you want to assign to the image file e.g "MeAtLunch.png"
     * @param theBitmap
     *         the image you want to save as a Bitmap
     * @return returns the full path(file system address) of the saved image
     */
    public final String putImage(final String theFolder, final String theImageName, final Bitmap theBitmap) {
        if (theFolder == null || theImageName == null || theBitmap == null) return null;

        mDefaultAppImagedataDirectory = theFolder;
        final String mFullPath = setupFullPath(theImageName);

        if (!mFullPath.isEmpty()) {
            mLastImagePath = mFullPath;
            saveBitmap(mFullPath, theBitmap);
        }

        return mFullPath;
    }

    /**
     * Saves 'theBitmap' into 'fullPath'
     *
     * @param fullPath
     *         full path of the image file e.g. "Images/MeAtLunch.png"
     * @param theBitmap
     *         the image you want to save as a Bitmap
     * @return true if image was saved, false otherwise
     */
    public static boolean putImageWithFullPath(final String fullPath, final Bitmap theBitmap) {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
    }

    // Getters

    /**
     * Creates the path for the image with name 'imageName' in DEFAULT_APP.. directory
     *
     * @param imageName
     *         name of the image
     * @return the full path of the image. If it failed to create directory, return empty string
     */
    private String setupFullPath(final String imageName) {
        final File mFolder = new File(Environment.getExternalStorageDirectory(), mDefaultAppImagedataDirectory);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }

    /**
     * Saves the Bitmap as a PNG file at path 'fullPath'
     *
     * @param fullPath
     *         path of the image file
     * @param bitmap
     *         the image as a Bitmap
     * @return true if it successfully saved, false otherwise
     */
    private static boolean saveBitmap(final String fullPath, final Bitmap bitmap) {
        return saveBitmap(fullPath, bitmap, CompressFormat.PNG, 100);
    }

    /**
     * Saves the Bitmap as a user specified image type at path 'fullPath'
     * Overload by rudz.
     *
     * @param fullPath
     *         path of the image file
     * @param bitmap
     *         the image as a Bitmap
     * @return true if it successfully saved, false otherwise
     */
    private static boolean saveBitmap(final String fullPath, final Bitmap bitmap, final CompressFormat format, final int compressionLevel) {
        if (fullPath == null || bitmap == null) return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        final File imageFile = new File(fullPath);

        if (imageFile.exists() & !imageFile.delete()) return false;

        try {
            fileCreated = imageFile.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(format, compressionLevel, out);
        } catch (final Exception e) {
            e.printStackTrace();
            bitmapCompressed = false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    streamClosed = true;
                } catch (final IOException e) {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }
        return fileCreated && bitmapCompressed && streamClosed;
    }

    /**
     * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key
     *         SharedPreferences key
     * @return int value at 'key' or 'defaultValue' if key not found
     */
    public final int getInt(final String key, final int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    /**
     * Get parsed ArrayList of Integers from SharedPreferences at 'key'
     *
     * @param key
     *         SharedPreferences key
     * @return ArrayList of Integers
     */
    public final ArrayList<Integer> getListInt(final String key) {
        final String[] myList = TextUtils.split(mPreferences.getString(key, ""), DELIM);
        final ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(myList));
        final ArrayList<Integer> newList = new ArrayList<>();

        for (final String item : arrayToList) newList.add(Integer.parseInt(item));

        return newList;
    }

    /**
     * Get long value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key
     *         SharedPreferences key
     * @param defaultValue
     *         long value returned if key was not found
     * @return long value at 'key' or 'defaultValue' if key not found
     */
    public final long getLong(final String key, final long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    /**
     * Get float value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key
     *         SharedPreferences key
     * @return float value at 'key' or 'defaultValue' if key not found
     */
    public final float getFloat(final String key, final float defaultValue) {
        return mPreferences.getFloat(key, defaultValue);
    }

    /**
     * Get double value from SharedPreferences at 'key'. If exception thrown, return 'defaultValue'
     *
     * @param key
     *         SharedPreferences key
     * @param defaultValue
     *         double value returned if exception is thrown
     * @return double value at 'key' or 'defaultValue' if exception is thrown
     */
    public final double getDouble(final String key, final double defaultValue) {
        final String number = getString(key);

        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get parsed ArrayList of Double from SharedPreferences at 'key'
     *
     * @param key
     *         SharedPreferences key
     * @return ArrayList of Double
     */
    public final ArrayList<Double> getListDouble(final String key) {
        final String[] myList = TextUtils.split(mPreferences.getString(key, ""), DELIM);
        final ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(myList));
        final ArrayList<Double> newList = new ArrayList<>();

        for (final String item : arrayToList) newList.add(Double.parseDouble(item));

        return newList;
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key
     *         SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public final String getString(final String key) {
        return getString(key, "");
    }
    public final String getString(final String key, final String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }


    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key
     *         SharedPreferences key
     * @return ArrayList of String
     */
    public final ArrayList<String> getListString(final String key) {
        return new ArrayList<>(Arrays.asList(TextUtils.split(mPreferences.getString(key, ""), DELIM)));
    }

    /**
     * Get boolean value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key
     *         SharedPreferences key
     * @return boolean value at 'key' or 'defaultValue' if key not found
     */
    public final boolean getBoolean(final String key) {
        return mPreferences.getBoolean(key, false);
    }

    public final boolean getBoolean(final String key, final boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    /**
     * Get parsed ArrayList of Boolean from SharedPreferences at 'key'
     *
     * @param key
     *         SharedPreferences key
     * @return ArrayList of Boolean
     */
    public final ArrayList<Boolean> getListBoolean(final String key) {
        final ArrayList<String> myList = getListString(key);
        final ArrayList<Boolean> newList = new ArrayList<>();

        for (final String item : myList) newList.add(Boolean.getBoolean(item));

        return newList;
    }


    // Put methods

    public final ArrayList<Object> getListObject(final String key, final Class<?> mClass) {
        final Gson gson = new Gson();

        final ArrayList<String> objStrings = getListString(key);
        final ArrayList<Object> objects = new ArrayList<>();

        for (final String jObjString : objStrings) objects.add(gson.fromJson(jObjString, mClass));

        return objects;
    }

    public final Object getObject(final String key, final Class<?> classOfT) {
        final String json = getString(key);
        final Object value = new Gson().fromJson(json, classOfT);
        if (value == null) throw new NullPointerException();
        return value;
    }

    /**
     * Put int value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         int value to be added
     */
    public final void putInt(final String key, final int value) {
        checkForNullKey(key);
        mPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Put ArrayList of Integer into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param intList
     *         ArrayList of Integer to be added
     */
    public final void putListInt(final String key, final ArrayList<Integer> intList) {
        checkForNullKey(key);
        final Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        mPreferences.edit().putString(key, TextUtils.join(DELIM, myIntList)).apply();
    }

    /**
     * Put long value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         long value to be added
     */
    public final void putLong(final String key, final long value) {
        checkForNullKey(key);
        mPreferences.edit().putLong(key, value).apply();
    }

    /**
     * Put float value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         float value to be added
     */
    public final void putFloat(final String key, final float value) {
        checkForNullKey(key);
        mPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * Put double value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         double value to be added
     */
    public final void putDouble(final String key, final double value) {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }

    /**
     * Put ArrayList of Double into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param doubleList
     *         ArrayList of Double to be added
     */
    public final void putListDouble(final String key, final ArrayList<Double> doubleList) {
        checkForNullKey(key);
        final Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        mPreferences.edit().putString(key, TextUtils.join(DELIM, myDoubleList)).apply();
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         String value to be added
     */
    public final void putString(final String key, final String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        mPreferences.edit().putString(key, value).apply();
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param stringList
     *         ArrayList of String to be added
     */
    public final void putListString(final String key, final ArrayList<String> stringList) {
        checkForNullKey(key);
        final String[] myStringList = stringList.toArray(new String[stringList.size()]);
        mPreferences.edit().putString(key, TextUtils.join(DELIM, myStringList)).apply();
    }

    /**
     * Put boolean value into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param value
     *         boolean value to be added
     */
    public final void putBoolean(final String key, final boolean value) {
        checkForNullKey(key);
        mPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Put ArrayList of Boolean into SharedPreferences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param boolList
     *         ArrayList of Boolean to be added
     */
    public final void putListBoolean(final String key, final ArrayList<Boolean> boolList) {
        checkForNullKey(key);
        final ArrayList<String> newList = new ArrayList<>();

        for (final Boolean item : boolList) newList.add(Boolean.toString(item));

        putListString(key, newList);
    }

    /**
     * Put ObJect any type into SharedPrefrences with 'key' and save
     *
     * @param key
     *         SharedPreferences key
     * @param obj
     *         is the Object you want to put
     */
    public final void putObject(final String key, final Object obj) {
        checkForNullKey(key);
        final Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }

    public final void putListObject(final String key, final ArrayList<Object> objArray) {
        checkForNullKey(key);
        final Gson gson = new Gson();
        final ArrayList<String> objStrings = new ArrayList<>();
        for (final Object obj : objArray) objStrings.add(gson.toJson(obj));

        putListString(key, objStrings);
    }

    /**
     * Remove SharedPreferences item with 'key'
     *
     * @param key
     *         SharedPreferences key
     */
    public final void remove(final String key) {
        mPreferences.edit().remove(key).apply();
    }

    /**
     * Delete image file at 'path'
     *
     * @param path
     *         path of image file
     * @return true if it successfully deleted, false otherwise
     */
    public static boolean deleteImage(final String path) {
        return new File(path).delete();
    }

    /**
     * Clear SharedPreferences (remove everything)
     */
    public final void clear() {
        mPreferences.edit().clear().apply();
    }

    /**
     * Retrieve all values from SharedPreferences. Do not modify collection return by method
     *
     * @return a Map representing a list of key/value pairs from SharedPreferences
     */
    public final Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    /**
     * Register SharedPreferences change listener
     *
     * @param listener
     *         listener object of OnSharedPreferenceChangeListener
     */
    public final void registerOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregister SharedPreferences change listener
     *
     * @param listener
     *         listener object of OnSharedPreferenceChangeListener to be unregistered
     */
    public final void unregisterOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     *
     * @param key
     *         SharedPreferences key
     */
    @SuppressWarnings("ConstantConditions")
    public static void checkForNullKey(final String key) {
        if (key == null) throw new NullPointerException("Key : " + key + " does not exist in preferences");
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     *
     * @param value
     *         SharedPreferences key
     */
    @SuppressWarnings("ConstantConditions")
    public static void checkForNullValue(final String value) {
        if (value == null) throw new NullPointerException("Value : " + value + " does not exist in preferences");
    }
}