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

package com.undyingideas.thor.skafottet.interfaces;

import android.os.Bundle;

import com.undyingideas.thor.skafottet.support.utility.Constant;

/**
 * Created on 22-01-2016, 19:43.
 * Project : skafottet
 * Generic fragmentflipper interface for modes.
 * @author rudz
 */
public interface IFragmentFlipper {
    /**
     * Flips the fragment to the corresponding mode.
     * @param gameMode The mode as defined by {@link Constant}.
     */
    void flipFragment(final int gameMode);

    // to use with savegame
    void flipFragment(final int gameMode, final Bundle bundle);
}
