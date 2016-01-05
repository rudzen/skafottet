/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.highscore;

/**
 * @author rudz
 */
public class Player {

    // idiotklasse til spillerpoint..
    public static int pts;

    /* used by the game status fragment */
    public static final int DEF_START_SCORE = 0;

    /* define the shifting values for scores on future difficulty settings */
    public static final int DIF_EASY_SHIFT_MULTIPLIER = 2;
    public static final int DIF_MEDIUM_SHIFT_MULTIPLIER = 1;

    static {
        pts = DEF_START_SCORE;
    }

    public static void reset() {
        pts = DEF_START_SCORE;
    }

    public static String getPtsString() {
        return Integer.toString(pts);
    }
}
