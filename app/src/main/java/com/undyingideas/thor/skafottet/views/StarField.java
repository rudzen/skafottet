/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class StarField extends View implements PreferenceChangeListener {

    private static final float LOW_PASS_FILTER = 0.3f;
    private static final int UPDATE_RATE = 1000 / 100;

    private final ArrayList<Star2D> stars = new ArrayList<>(75);
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
        this.run = run;
        if (run) {
            handleCalculate = new Handler();
            handleUpdate = new Handler();
            calculator = new CalculateStarfield();
            updater = new UpdateStarfield();
            handleCalculate.post(calculator);
            handleCalculate.post(updater);
        } else {
            handleCalculate.removeCallbacksAndMessages(null);
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
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(color);
            p.setStrokeWidth(3f);
            //p.setShadowLayer(2f, 2f, 2f, Color.GRAY);
        }
        Log.d(TAG, "Width  : " + w);
        Log.d(TAG, "Height : " + h);
        if (!stars.isEmpty()) stars.clear();
        for (int i = 1; i <= 50; i += 5) {
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
        }
        stars.trimToSize();

        run = true;
        handleCalculate = new Handler();
        handleUpdate = new Handler();
        calculator = new CalculateStarfield();
        updater = new UpdateStarfield();
        handleCalculate.post(calculator);
        handleUpdate.post(updater);
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent pce) {
        // do nothing for now
    }

    private class CalculateStarfield implements Runnable {
        @Override
        public void run() {
            //final long time = System.nanoTime();
            for (final Star2D star2D : stars) {
                star2D.xy.x -= star2D.speedX + gravity.x;
                star2D.xy.y += gravity.y;

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
            if (run) handleCalculate.postDelayed(calculator, UPDATE_RATE);
        }
    }

    private class UpdateStarfield implements Runnable {
        private static final int FPS = 1000 / 30;

        @Override
        public void run() {
            invalidate();
            handleUpdate.postDelayed(updater, FPS);
        }
    }

    public void setGravity(final float x, final float y) {
        gravity.x = x >= LOW_PASS_FILTER || x <= -LOW_PASS_FILTER ? x : 0f;
        gravity.y = y >= LOW_PASS_FILTER || y <= -LOW_PASS_FILTER ? y : 0f;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (final Star2D star : stars) {
            p.setAlpha(star.fade);
            canvas.drawCircle(star.xy.x, star.xy.y, 5, p);
        }
    }
}
