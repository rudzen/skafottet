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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class StarField extends View {

    private final ArrayList<Star2D> stars = new ArrayList<>(100);
    private Paint p;
    private Handler updateHandler;
    private boolean run;
    private int width, height;
    private PointF gravity;
    private float z;
    private int color;

    private static final String TAG = "Starfield";

    public boolean isRun() {
        return run;
    }

    public void setRun(final boolean run) {
        this.run = run;
        if (run) {
            updateHandler = new Handler();
            updateHandler.post(new UpdateStarfield());
        } else {
            updateHandler.removeCallbacksAndMessages(null);
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

    public int getStarCount() {
        return stars.size();
    }

    private void addStar(final float x, final float y, final float speed) {
        stars.add(new Star2D(x > width ? width : x, y > height ? height : y, speed));
    }

    public void init(final int w, final int h, final int color) {
        this.color = color;
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
        for (int i = 1; i <= 75; i += 5) {
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
            addStar((float) Math.random() * w, (float) Math.random() * h, (float) Math.random() * 3);
        }

        updateHandler = new Handler();
        run = true;
        updateHandler.post(new UpdateStarfield());
    }

    private class UpdateStarfield implements Runnable {
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

                if (!star2D.fadeDirection) star2D.fade += star2D.fadespeed;
                else star2D.fade -= star2D.fadespeed;
            }
            //Log.d(TAG, "Time to update was (ns) : " + (System.nanoTime() - time));
            if (run) updateHandler.postDelayed(new UpdateStarfield(), 1000 / 70);
        }
    }

    public void setGravity(final float x, final float y) {
        gravity.x = x >= 0.3f || x <= -0.3f ? x : 0f;
        gravity.y = y >= 0.3f || y <= -0.3f ? y : 0f;
    }

    public void removeStar() {
        if (!stars.isEmpty()) {
            stars.remove(0);
            stars.trimToSize();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        for (final Star2D star : stars) {
            p.setAlpha(star.fade);
//            if (gravity.y > 5 || gravity.y < -5)
//                canvas.drawLine(star.xy.x, star.xy.y, star.xy.x + gravity.x, star.xy.y + gravity.y + gravity.x, p);
//            else if (gravity.x > 5 || gravity.x < -5)
//                canvas.drawLine(star.xy.x, star.xy.y, star.xy.x + gravity.x, star.xy.y - gravity.y + gravity.x, p);
//            else
                canvas.drawCircle(star.xy.x, star.xy.y, 3, p);
        }
    }

    public double getAvrSpeed() {
        double as = 0;
        for (final Star2D star : stars) as += star.getSpeedX();
        return as / stars.size();
    }

    public void updateSpeed(final float max, final float increment) {
        for (final Star2D star : stars) star.speedX = !star.fadeDirection ? star.speedX + increment : star.speedX - increment;
    }
}
