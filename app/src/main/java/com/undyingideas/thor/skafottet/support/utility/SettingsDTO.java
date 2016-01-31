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

package com.undyingideas.thor.skafottet.support.utility;

import android.graphics.Color;
import android.support.annotation.Nullable;

/**
 * Created on 20-01-2016, 11:37.
 * Project : skafottet
 * To hold the current settings for the game.
 * @author rudz
 */
public final class SettingsDTO {

    @Nullable
    public String lastPw;

    public boolean prefsMusic;
    public boolean prefsSfx;
    public boolean prefsBlood;

    public boolean prefsHeptic;

    public boolean keepLogin;


    public int prefsColour;
    public int prefsColour_r;
    public int prefsColour_g;
    public int prefsColour_b;

    public int textColour; // determined by the prefsColour.

    public void setContrastColor() {
        prefsColour_r = prefsColour >> 16 & 0x000000FF;
        prefsColour_g = prefsColour >> 8 & 0x000000FF;
        prefsColour_b = prefsColour & 0x000000FF;
        textColour = (299 * prefsColour_r + 587 * prefsColour_g + 114 * prefsColour_b) / 1000 >= 128 ? Color.BLACK : Color.WHITE;
    }

}
