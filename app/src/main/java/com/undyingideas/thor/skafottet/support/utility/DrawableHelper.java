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

