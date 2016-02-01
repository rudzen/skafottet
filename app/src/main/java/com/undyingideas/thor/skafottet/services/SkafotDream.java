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

package com.undyingideas.thor.skafottet.services;

import android.graphics.Color;
import android.service.dreams.DreamService;
import android.view.Display;

import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.views.StarField;

/**
 * This class is a sample implementation of a DreamService. When activated, a
 * TextView will repeatedly, move from the left to the right of screen, at a
 * random y-value.
 * <p>
 * Daydreams are only available on devices running API v17+.
 *
 * This is currently not working as intended!
 *
 */
public class SkafotDream extends DreamService {

    private StarField sf;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final Display display = getWindowManager().getDefaultDisplay();
        display.getSize(WindowLayout.screenDimension);
        setScreenBright(false);
        setInteractive(false);
        setFullscreen(true);
        sf = new StarField(this);
        setContentView(sf);
    }

    @Override
    public void onDreamingStarted() {
        sf.init(WindowLayout.screenDimension.x, WindowLayout.screenDimension.y, Color.RED);
        sf.setmIsRunning(true);
        super.onDreamingStarted();
    }

    @Override
    public void onDreamingStopped() {
        sf.setmIsRunning(false);
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
