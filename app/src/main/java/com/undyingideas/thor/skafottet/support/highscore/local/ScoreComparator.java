/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
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

package com.undyingideas.thor.skafottet.support.highscore.local;

import java.io.Serializable;
import java.util.Comparator;

class ScoreComparator implements Comparator<Score>, Serializable {

    private static final long serialVersionUID = 6944625214625451817L;

    @Override
    public int compare(final Score score1, final Score score2) {
        final int sc1 = score1.getScore();
        final int sc2 = score2.getScore();

        if (sc1 > sc2) return -1;
        if (sc1 < sc2) return 1;

        /* equal score, so sort after date! */
        final long sc1date = score1.getDate();
        final long sc2date = score2.getDate();

        if (sc1date > sc2date) return -1;
        if (sc1date < sc2date) return 1;

        return 0;
    }
}
