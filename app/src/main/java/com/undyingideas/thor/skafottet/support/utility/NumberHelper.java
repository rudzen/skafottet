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

package com.undyingideas.thor.skafottet.support.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>Created on 24-01-2016, 10:55.<br>
 * Project : skafottet</p>
 *
 * @author rudz
 */
public final class NumberHelper {

    public static double round(final double value, final int places) {
        final BigDecimal bd = new BigDecimal(value);
        return bd.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}
