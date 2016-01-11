package com.undyingideas.thor.skafottet.support.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

// TODO : Replace with async task class made for this !

/**
 * Created by Thor on 08-11-2015.
 * This class still uses a lot of danish, will be corrected at some other time
 * todo clean language
 */
public final class WordCollector {
    private static ArrayList<String> s_muligeOrd;

    private static ArrayList<String> samlOrd(final String url) throws Exception {
        s_muligeOrd = new ArrayList<>();
        if (url == null) {
            hentOrdFra("http://dr.dk");
        } else {
            hentOrdFra(url);
        }
        return s_muligeOrd;
    }

    private static String hentUrl(final String url) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        final StringBuilder sb = new StringBuilder(50);
        String linje = br.readLine();
        while (linje != null) {
            sb.append(linje).append("\n");
            linje = br.readLine();
        }
        return sb.toString();
    }

    @Deprecated
    private static void hentOrdFraMoths() throws Exception {
        String data = hentUrl("http://mothsordbog.dk/godt-prefs-igen");
        System.out.println("data = " + data);
        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        s_muligeOrd.clear();
        s_muligeOrd.addAll(new HashSet<>(Arrays.asList(data.split(" "))));
        System.out.println("s_muligeOrd = " + s_muligeOrd);

    }

    @Deprecated
    private static void hentOrdFraDr() throws Exception {
        String data = hentUrl("http://dr.dk");
        System.out.println("data = " + data);

        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        s_muligeOrd.clear();
        s_muligeOrd.addAll(new HashSet<>(Arrays.asList(data.split(" "))));

        System.out.println("s_muligeOrd = " + s_muligeOrd);
    }

    private static void hentOrdFra(final String url) throws Exception {
        String data = hentUrl(url);
        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        s_muligeOrd.clear();
        final LinkedList<String> list = new LinkedList<>();
        for (final String s : data.split(" "))
            if (s.length() > 2) list.add(s);
        s_muligeOrd.addAll(new HashSet<>(list));
    }

    public static ArrayList<String> samlOrd() throws Exception {
        return samlOrd(null);
    }
}
