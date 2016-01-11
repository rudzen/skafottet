/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.support.highscore;

import java.util.Comparator;

class ScoreComparator implements Comparator<Score> {
    @Override
    public int compare(final Score score1, final Score score2) {
        final int sc1 = score1.getScore();
        final int sc2 = score2.getScore();

        if (sc1 > sc2) return -1;
        if (sc1 < sc2) return 1;
        return 0;
    }
}
