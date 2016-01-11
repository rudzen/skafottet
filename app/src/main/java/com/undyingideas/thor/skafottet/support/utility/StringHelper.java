/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.utility;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Contains functionality to help with Strings and Patterns.
 * Created by rudz on 17-11-2015.
 *
 * @author rudz
 */
public final class StringHelper {

    private static final ArrayList<Character> invalidChars = new ArrayList<>();

    /* Settings for regEx matching of incomming words */
    private final static String regLowerCase = "[a-zæøå]";
    private final static String regJavaScript = "<script[^>]*>([\\s\\S]*?)</script>";

    public final static Pattern patGuess = Pattern.compile(regLowerCase);
    //Pattern.compile(regLowerCase + "|[A-ZÆØÅ]{" + Integer.toString(WORD_LENGTH) + ",}");
    public final static Pattern patHTML = Pattern.compile("(?i)<[^>]*>");
    public static final String VALID_URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static Pattern patLoad = setWordLength(regLowerCase, 4);
    public static Pattern patJS = Pattern.compile(regJavaScript);


    //'section'

    static {
        invalidChars.add(':');
        invalidChars.add('/');
        invalidChars.add('@');
        invalidChars.add('?');
        invalidChars.add('#');
        invalidChars.add('\\');
        invalidChars.add('<');
        invalidChars.add('$');
        invalidChars.add('+');
        invalidChars.add('%');
        invalidChars.add('>');
        invalidChars.add('!');
        invalidChars.add('`');
        invalidChars.add('&');
        invalidChars.add('*');
        invalidChars.add('‘');
        invalidChars.add('|');
        invalidChars.add('{');
        invalidChars.add('“');
        invalidChars.add('=');
        invalidChars.add('}');
        invalidChars.add(' ');
    }

    /**
     * Corrects any invalid characters not suited for the filesystem.
     *
     * @param filename
     *         The string to correct.
     * @return The corrected filename string.
     */
    public static String validFileName(String filename) {
        for (final Character c : invalidChars)
            if (filename.contains(c.toString())) filename = filename.replace(c, '_');
        return filename;
    }

    /**
     * Attempts to correct an URL string.<br>
     * Will try to add www. and / or http://
     *
     * @param url
     *         the base url to check.
     * @return the corrected url if possible, otherwise the original url.
     */
    public static String alignUrl(final String url) {
        final Pattern pureMatch = Pattern.compile(VALID_URL);
        /* pure url */
        if (pureMatch.matcher(url).find()) return url;

        /* check for www. */
        if (url.toLowerCase().contains("www.")) {
            /* try match with appending http */
            if (pureMatch.matcher("http://" + url).find()) return "http://" + url;
        } else if (url.toLowerCase().startsWith("http://") && url.length() > 7) {
            if (pureMatch.matcher(url.substring(0, 6) + "www." + url.substring(7)).find())
                return url.substring(0, 6) + "www." + url.substring(7);
        } else if (pureMatch.matcher("http://www." + url).find()) return "http://www." + url;

        return url;
    }

    /**
     * Counts a specific string inside another string<br>
     *
     * @param in
     *         : the input string
     * @param toCount
     *         : what string to count?
     * @return the count as an integer
     */
    public static int countString(final String in, final String toCount) {
        final int cLen = toCount.length();
        final int len = in.length();
        if (cLen == 0 || len == 0) return 0;
        final int count = len - in.replaceAll(toCount, "").length();
        return count != len ? count / cLen : 0;
    }

    private static Pattern setWordLength(final String regEx, final int length) {
        return Pattern.compile(regEx + "|" + regEx.toUpperCase() + "{" + Integer.toString(length) + ",25}");
    }

    public static void setPatterns() {
        /* configure pattern for loading of list, this is necessary as it might have changed! */
        patLoad = setWordLength(regLowerCase, 3);
    }

}
