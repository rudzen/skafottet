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

package com.undyingideas.thor.skafottet.views;

/**
 * <p>Created on 24-01-2016, 11:09.<br>
 * Project : skafottet</p>
 *
 * @author rudz
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.undyingideas.thor.skafottet.support.utility.NumberHelper;

public class DreamView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SampleView";

    private static final int[] colors = {
            Color.RED,
            Color.WHITE,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW
    };

    private int width;
    private int height;
    private final SurfaceHolder holder;

    public DreamView(final Context ctx) {
        super(ctx);

        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceCreated");

        new DreamThread().start();
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        Log.v(TAG, "surfaceChanged");

        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
    }

    private static int getColor() {
        return colors[(int) NumberHelper.round(Math.random() * colors.length, 0)];
    }

    private class DreamThread extends Thread {
        @Override
        public void run() {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);

            Canvas canvas;

            while (true) {
                canvas = holder.lockCanvas();

                if (canvas == null) {
                    break;
                }

                final float x = (float) (Math.random() * width);
                final float y = (float) (Math.random() * height);

                canvas.drawText("MUUUU", 30, 30, paint);
                paint.setColor(getColor());
                canvas.drawCircle(x, y, 5f, paint);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}