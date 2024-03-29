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

/**
 * @author Anton Averin
 * mailto:
 * @author rudz
 * Minor speedups in general, and adjustments to fix maps.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class FontUtils {

    /* ********************************************** */
    /*              The best way!... */
    /* ********************************************** */
    public static void setDefaultFont(final Context context, final String staticTypefaceFieldName, final String fontAssetName) {
        replaceFont(staticTypefaceFieldName, Typeface.createFromAsset(context.getAssets(), fontAssetName));
    }

    private static void replaceFont(final String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    /* ********************************************** */






    public interface FontTypes {
        String LIGHT = "Light";
        String BOLD = "Bold";
    }

    /**
     * map of font types to font paths in assets
     */
    private static final Map<String, String> fontMap = new HashMap<>();

    static {
        fontMap.put(FontTypes.LIGHT, "fonts/Roboto-Light.ttf");
        fontMap.put(FontTypes.BOLD, "fonts/Roboto-Bold.ttf");
    }

    /* cache for loaded Roboto typefaces*/
    private static final HashMap<String, Typeface> typefaceCache = new HashMap<>();

    /**
     * Creates Roboto typeface and puts it into cache
     * @param context The context
     * @param fontType The font type, as complete asset path
     * @return
     */
    private static Typeface getRobotoTypeface(final Context context, final String fontType) {
        final String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }

    /**
     * Gets roboto typeface according to passed typeface style settings.
     * Will get Roboto-Bold for Typeface.BOLD etc
     *
     * @param context The context
     * @param originalTypeface The original typeface
     * @return
     */
    private static Typeface getRobotoTypeface(final Context context, final Typeface originalTypeface) {
        return getRobotoTypeface(context, originalTypeface != null && originalTypeface.getStyle() == Typeface.BOLD ? FontTypes.BOLD : FontTypes.LIGHT);
    }

    /**
     * Walks ViewGroups, finds TextViews and applies Typefaces taking styling in consideration
     * @param context
     *         - to reach assets
     * @param view
     *         - root view to apply typeface to
     */
    private static void setRobotoFont(final Context context, final View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setRobotoFont(context, ((ViewGroup) view).getChildAt(i));
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(getRobotoTypeface(context, ((TextView) view).getTypeface()));
        }
    }
}
