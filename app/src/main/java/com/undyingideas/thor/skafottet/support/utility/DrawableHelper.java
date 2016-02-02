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

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.RelativeLayout;

import com.undyingideas.thor.skafottet.views.AutoScaleTextView;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.support.utility<br>
 * Created by : rudz<br>
 * On : jan.26.2016 - 18:00
 * </p>
 */
public final class DrawableHelper {

    public static void setButtonColors(final RelativeLayout[] buttons, final AutoScaleTextView... button_text) {
        final int[] cols = new int[3];
        cols[0] = Color.BLACK;
        cols[1] = GameUtility.getSettings().prefsColour;
        cols[2] = Color.BLACK;
        Drawable drawable;
        for (int i = 0; i < buttons.length; i++) {
            drawable = buttons[i].getBackground();
            ((GradientDrawable) drawable).setColors(cols);
            buttons[i].setBackground(drawable);
            button_text[i].setTextColor(GameUtility.getSettings().textColour);
        }
    }


}

