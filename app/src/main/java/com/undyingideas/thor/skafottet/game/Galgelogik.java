package com.undyingideas.thor.skafottet.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

// TODO : Hele klassen er noget weird ass noget, skal erstattes af en mere moderne udgave.

public class Galgelogik {
    private static final Pattern tagPattern = Pattern.compile("<.+?>");
    private ArrayList<String> muligeOrd = new ArrayList<>();
    private String ordet;
    private final ArrayList<String> brugteBogstaver = new ArrayList<>();
    private String synligtOrd;
    private int antalForkerteBogstaver;
    private boolean sidsteBogstavVarKorrekt;
    private boolean spilletErVundet;
    private boolean spilletErTabt;


    public ArrayList<String> getBrugteBogstaver() {
        return brugteBogstaver;
    }

    public String getSynligtOrd() {
        return synligtOrd;
    }

    public String getOrdet() {
        return ordet;
    }

    public int getAntalForkerteBogstaver() {
        return antalForkerteBogstaver;
    }

    public boolean erSidsteBogstavKorrekt() {
        return sidsteBogstavVarKorrekt;
    }

    public boolean erSpilletVundet() {
        return spilletErVundet;
    }

    public boolean erSpilletTabt() {
        return spilletErTabt;
    }

    public boolean erSpilletSlut() {
        return spilletErTabt || spilletErVundet;
    }

    /**
     * Brug constructor med input i stedet for
     */
    @Deprecated
    public Galgelogik() {
  /*  muligeOrd.add("bil");
    muligeOrd.add("computer");
    muligeOrd.add("programmering");
    muligeOrd.add("motorvej");
    muligeOrd.add("busrute");
    muligeOrd.add("gangsti");
    muligeOrd.add("skovsnegl");
    muligeOrd.add("solsort");*/
        try {
            hentOrdFraMoths();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for single word instances, used for hotSeat and multiPlayer
     *
     * @param WordToBeGuessed
     *         the word to be guessed
     */
    public Galgelogik(final String WordToBeGuessed) {
        muligeOrd.add(WordToBeGuessed);
        nulstil();
    }

    public Galgelogik(final ArrayList<String> muligeOrd) {
        this.muligeOrd = muligeOrd;
        nulstil();
    }

    public void nulstil() {
        brugteBogstaver.clear();
        antalForkerteBogstaver = 0;
        spilletErVundet = false;
        spilletErTabt = false;

        ordet = muligeOrd.get(new Random().nextInt(muligeOrd.size()));
        opdaterSynligtOrd();
    }


    private void opdaterSynligtOrd() {
        synligtOrd = "";
        spilletErVundet = true;
        for (int n = 0; n < ordet.length(); n++) {
            final String bogstav = ordet.substring(n, n + 1);
            if (brugteBogstaver.contains(bogstav)) {
                synligtOrd += bogstav;
            } else {
                synligtOrd += "*";
                spilletErVundet = false;
            }
        }
    }

    public void gætBogstav(final String bogstav) {
        if (bogstav.length() != 1) return;
        System.out.println("Der gættes på bogstavet: " + bogstav);
        if (brugteBogstaver.contains(bogstav)) return;
        if (spilletErVundet || spilletErTabt) return;

        brugteBogstaver.add(bogstav);

        if (ordet.contains(bogstav)) {
            sidsteBogstavVarKorrekt = true;
            System.out.println("Bogstavet var korrekt: " + bogstav);
        } else {
            // Vi gættede på et bogstav der ikke var i ordet.
            sidsteBogstavVarKorrekt = false;
            System.out.println("Bogstavet var IKKE korrekt: " + bogstav);
            antalForkerteBogstaver += 1;
            if (antalForkerteBogstaver > 6) {
                spilletErTabt = true;
            }
        }
        opdaterSynligtOrd();
    }

    public void logStatus() {
        System.out.println("---------- ");
        System.out.println("- ordet (skult) = " + ordet);
        System.out.println("- synligtOrd = " + synligtOrd);
        System.out.println("- forkerteBogstaver = " + antalForkerteBogstaver);
        System.out.println("- brugeBogstaver = " + brugteBogstaver);
        if (spilletErTabt) System.out.println("- SPILLET ER TABT");
        if (spilletErVundet) System.out.println("- SPILLET ER VUNDET");
        System.out.println("---------- ");
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

    private void hentOrdFraMoths() throws Exception {
        String data = hentUrl("http://mothsordbog.dk/godt-prefs-igen");
        System.out.println("data = " + data);
        data = tagPattern.matcher(data).replaceAll(" ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<>(Arrays.asList(data.split(" "))));
        System.out.println("muligeOrd = " + muligeOrd);
        nulstil();
    }

    public void hentOrdFraDr() throws Exception {
        String data = hentUrl("http://dr.dk");
        System.out.println("data = " + data);

        data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
        System.out.println("data = " + data);
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + muligeOrd);
        nulstil();
    }
}