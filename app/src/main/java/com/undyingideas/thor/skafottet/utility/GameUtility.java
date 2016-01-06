package com.undyingideas.thor.skafottet.utility;

import com.undyingideas.thor.skafottet.wordlist.data.WordList;

/**
 * Created on 04-01-2016, 18:47.
 * Project : skafottet
 *
 * @author rudz
 */
public abstract class GameUtility {

    public static final String KEY_IS_HOT_SEAT = "isHotSeat";
    public static final String KEY_MULIGE_ORD = "muligeOrd";
    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static WordList s_wordList;

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static TinyDB s_prefereces;

}
