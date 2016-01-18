/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.support.highscore;

/**
 * @author rudz
 */
final class Player {

    // idiotklasse til spillerpoint..
    private static int pts;

    /* used by the game status fragment */
    private static final int DEF_START_SCORE = 0;

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
