package com.undyingideas.thor.skafottet.support.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.wordlist.WordController;
import com.undyingideas.thor.skafottet.support.wordlist.WordList;

/**
 * Created on 04-01-2016, 18:47.
 * Project : skafottet
 *
 * @author rudz
 */
public abstract class GameUtility {

    public static MultiplayerController mpc;
    public static Firebase fb;

    public static final String KEY_MULIGE_ORD = "muligeOrd";

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static WordList s_wordList = new WordList();

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static TinyDB s_prefereces;

    public static WordController wordController = new WordController();

    public static @DrawableRes
    final int[] imageRefs = new int[8];

    static {
        imageRefs[0] = R.drawable.terror0;
        imageRefs[1] = R.drawable.terror1;
        imageRefs[2] = R.drawable.terror2;
        imageRefs[3] = R.drawable.terror3;
        imageRefs[4] = R.drawable.terror4;
        imageRefs[5] = R.drawable.terror5;
        imageRefs[6] = R.drawable.terror6;
        imageRefs[7] = R.drawable.terror7;
    }

    public static Bitmap invert(final Context context, final int id) {
        final Bitmap bm = BitmapFactory.decodeResource(context.getResources(), imageRefs[id]);
        final Bitmap ret = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        final Canvas canvas = new Canvas(ret);

        final float[] m_Invert = {
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
        };

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final ColorMatrix cm = new ColorMatrix(m_Invert);

//        paint.setDither(true);
//        paint.setAntiAlias(true);

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bm, 0, 0, paint);

        return ret;
    }

}
