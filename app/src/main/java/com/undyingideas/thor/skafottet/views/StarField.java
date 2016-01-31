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

package com.undyingideas.thor.skafottet.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.undyingideas.thor.skafottet.activities.support.Foreground;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.util.ArrayList;

public class StarField extends RelativeLayout implements Foreground.Listener {

    private static final float LOW_PASS_FILTER = 0.3f;
    private static final float HIGH_PASS_FILTER = 3.0f;
    private static final int CALC_FPS = 1000 / 60;
    private static final int DRAW_FPS = 1000 / 80;

    private final ArrayList<Star2D> stars = new ArrayList<>(50);
    private Paint p;
    private Handler handleCalculate, handleUpdate;
    private volatile boolean run;
    private int width, height;
    private volatile PointF gravity;
    private float z;
    private Runnable calculator, updater;

    private static final String TAG = "Starfield";

    public boolean isRun() {
        return run;
    }

    public void setRun(final boolean run) {
        if (this.run != run) {
            this.run = run;
            if (run) {
                handleCalculate = new Handler();
                handleUpdate = new Handler();
                calculator = new CalculateStarfield();
                updater = new UpdateStarfield();
                handleCalculate.post(calculator);
                handleCalculate.post(updater);
                p.setColor(GameUtility.getSettings().prefsColour);
            } else {
                if (handleCalculate != null) {
                    handleCalculate.removeCallbacksAndMessages(null);
                }
                if (handleUpdate != null) {
                    handleUpdate.removeCallbacksAndMessages(null);
                }

            }
        }
    }

    public StarField(final Context context) {
        super(context);
    }

    public StarField(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public StarField(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void addStar(final float x, final float y, final float speed) {
        stars.add(new Star2D(x > width ? width : x, y > height ? height : y, speed));
    }

    public void init(final int w, final int h, final int color) {
        gravity = new PointF(0f, 0f);
        width = w;
        height = h;
        if (p == null) {
            p = new Paint();
            p.setColor(GameUtility.getSettings().prefsColour);
            p.setStrokeWidth(3f);
            //p.setShadowLayer(2f, 2f, 2f, Color.GRAY);
        }
        Log.d(TAG, "Width  : " + w);
        Log.d(TAG, "Height : " + h);
        if (!stars.isEmpty()) stars.clear();
        for (int i = 1; i <= 50; i += 5) {
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() + 0.1f);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() + 0.1f);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() + 0.1f);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() + 0.1f);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() + 0.1f);
        }
        Foreground.get(getContext()).addListener(this);
    }

    @Override
    public boolean hasFocus() {
        if (!run) setRun(GameUtility.getSettings().prefsBlood);
        Log.d(TAG, "StarField focus");
        return super.hasFocus();
    }

    @Override
    public void onBecameForeground() {
        setRun(GameUtility.getSettings().prefsBlood);
        Log.d(TAG, "Starfield enabled itself.");
    }

    @Override
    public void onBecameBackground() {
        Log.d(TAG, "Starfield disabled itself.");
        setRun(false);
    }

    private class CalculateStarfield implements Runnable {
        @Override
        public void run() {
            if (run) {
                //final long time = System.nanoTime();
                for (final Star2D star2D : stars) {
                    star2D.xy.x -= star2D.speedX + gravity.x;
                    star2D.xy.y += gravity.y + star2D.weight;

                    if (star2D.xy.x > width) star2D.xy.x = 0;
                    else if (star2D.xy.x < 0) star2D.xy.x = width;

                    if (star2D.xy.y > height) star2D.xy.y = 0;
                    else if (star2D.xy.y < 0) star2D.xy.y = height;

                    if (star2D.fade > 249) star2D.fadeDirection = true;
                    else if (star2D.fade < 10) star2D.fadeDirection = false;

                    if (!star2D.fadeDirection) star2D.fade += (int) star2D.fadespeed;
                    else star2D.fade -= (int) star2D.fadespeed;
                }
                //Log.d(TAG, "Time to update was (ns) : " + (System.nanoTime() - time));
                handleCalculate.postDelayed(calculator, CALC_FPS);
            }
        }
    }

    private class UpdateStarfield implements Runnable {

        @Override
        public void run() {
            invalidate();
            if (getMeasuredHeight() > height && getMeasuredWidth() > width) {
                init(getMeasuredWidth(), getMeasuredHeight(), Color.RED);
            } else if (run) {
                handleUpdate.postDelayed(updater, DRAW_FPS);
            }
        }
    }

    /**
     * Apply adjustments for x / y speed
     * @param x The adjustment for X
     * @param y The adjustment for Y
     */
    public void setGravity(final float x, final float y) {
        if (run) {
            gravity.x = x >= LOW_PASS_FILTER || x <= -LOW_PASS_FILTER ? x : 0f;
            gravity.y = y >= LOW_PASS_FILTER || y <= -LOW_PASS_FILTER ? y : 0f;
            if (x > HIGH_PASS_FILTER) gravity.x = HIGH_PASS_FILTER;
            else if (x < -HIGH_PASS_FILTER) gravity.x = -HIGH_PASS_FILTER;
            if (y > HIGH_PASS_FILTER) gravity.y = HIGH_PASS_FILTER;
            else if (y < -HIGH_PASS_FILTER) gravity.y = -HIGH_PASS_FILTER;
        } else {
            gravity.x = 0.0f;
            gravity.y = 0.0f;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (run) {
            for (final Star2D star : stars) {
                p.setAlpha(star.fade);
                canvas.drawCircle(star.xy.x, star.xy.y, 7, p);
            }
        }
    }
}
