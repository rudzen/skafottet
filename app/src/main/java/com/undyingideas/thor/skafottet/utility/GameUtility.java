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

    public static @DrawableRes int[] imageRefs = new int[7];

    static {
        imageRefs[0] = R.drawable.galge;
        imageRefs[1] = R.drawable.forkert1;
        imageRefs[2] = R.drawable.forkert2;
        imageRefs[3] = R.drawable.forkert3;
        imageRefs[4] = R.drawable.forkert4;
        imageRefs[5] = R.drawable.forkert5;
        imageRefs[6] = R.drawable.forkert6;
    }

}
