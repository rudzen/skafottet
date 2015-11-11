package com.undyingideas.thor.skafottet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Thor on 08-11-2015.
 * This class still uses a lot of danish, will be corrected at some other time
 * todo clean language
 */
public class WordCollector {
    private static ArrayList<String> muligeOrd;


    public static ArrayList<String> samlOrd(String url) throws Exception{
        muligeOrd = new ArrayList<String>();

        if(url == null){

                hentOrdFra("http://dr.dk");
                //hentOrdFraMoths();


            }
        else{
                hentOrdFra(url);
            }

        return muligeOrd;
    }

        private static String hentUrl(String url) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            StringBuilder sb = new StringBuilder();
            String linje = br.readLine();
            while (linje != null) {
                sb.append(linje + "\n");
                linje = br.readLine();
            }
            return sb.toString();
        }

    @Deprecated
    private static void hentOrdFraMoths() throws Exception{
            String data = hentUrl("http://mothsordbog.dk/godt-ord-igen");
            System.out.println("data = " + data);
            data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
            System.out.println("data = " + data);
            muligeOrd.clear();
            muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));
            System.out.println("muligeOrd = " + muligeOrd);

        }
    @Deprecated
    private static void hentOrdFraDr() throws Exception {
        String data = hentUrl("http://dr.dk");
        System.out.println("data = " + data);

        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + muligeOrd);

    }

    public static void hentOrdFra(String url) throws Exception{
        String data = hentUrl(url);
        System.out.println("data = " + data);

        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + muligeOrd);
    }



    public static ArrayList<String> samlOrd() throws Exception{
        return samlOrd(null);

    }


}
