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

import java.util.HashMap;
import java.util.Map;

public class FontUtils {

    public interface FontTypes {
        String LIGHT = "Light";
        String BOLD = "Bold";
    }

    /**
     * map of font types to font paths in assets
     */
    private static Map<String, String> fontMap = new HashMap<>();

    static {
        fontMap.put(FontTypes.LIGHT, "fonts/Roboto-Light.ttf");
        fontMap.put(FontTypes.BOLD, "fonts/Roboto-Bold.ttf");
    }

    /* cache for loaded Roboto typefaces*/
    private static final HashMap<String, Typeface> typefaceCache = new HashMap<>();

    /**
     * Creates Roboto typeface and puts it into cache
     * @param context
     * @param fontType
     * @return
     */
    private static Typeface getRobotoTypeface(Context context, String fontType) {
        String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }

    /**
     * Gets roboto typeface according to passed typeface style settings.
     * Will get Roboto-Bold for Typeface.BOLD etc
     *
     * @param context
     * @param originalTypeface
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
    public static void setRobotoFont(final Context context, final View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setRobotoFont(context, ((ViewGroup) view).getChildAt(i));
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(getRobotoTypeface(context, ((TextView) view).getTypeface()));
        }
    }
}
