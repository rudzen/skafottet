package com.undyingideas.thor.skafottet.utility;

import android.support.annotation.DrawableRes;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game_ui.wordlist.data.WordList;

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

    public static @DrawableRes int[] imageRefs = new int[8];

    static {
        imageRefs[0] = R.drawable.terror0;
        imageRefs[1] = R.drawable.terror1;
        imageRefs[2] = R.drawable.terror2;
        imageRefs[3] = R.drawable.terror3;
        imageRefs[4] = R.drawable.terror4;
        imageRefs[5] = R.drawable.terror5;
        imageRefs[6] = R.drawable.terror6;
        imageRefs[7] = R.drawable.terror7;
    }

}
