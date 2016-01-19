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

/**
 * Created on 04-01-2016, 18:47.
 * Project : skafottet
 *
 * @author rudz
 */
@SuppressWarnings("StaticVariableNamingConvention")
public abstract class GameUtility {

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static MultiplayerController mpc;
    public static Firebase firebase;

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static TinyDB s_preferences;

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static WordController s_wordController;

    public static int connectionStatus;
    public static String connectionStatusName;

    public static final int SFX_GUESS_WRONG = 0;
    public static final int SFX_GUESS_RIGHT = 1;
    public static final int SFX_INTRO = 2;
    public static final int SFX_MENU_CLICK = 3;
    public static final int SFX_WON = 4;
    public static final int SFX_LOST = 5;
    public static final int SFX_CHALLENGE = 6;

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

    public static void writeNullGame() {
        s_preferences.remove(Constant.KEY_SAVE_GAME);
    }
}
