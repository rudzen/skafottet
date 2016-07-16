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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StarField extends RelativeLayout implements Foreground.Listener {

    private static final float LOW_PASS_FILTER = 0.3f;
    private static final float HIGH_PASS_FILTER = 3.0f;
    private static final int CALC_FPS = 1000 / 60;
    private static final int DRAW_FPS = 1000 / 80;

    private final ArrayList<Star2D> mStars = new ArrayList<>(50);
    private Paint mPainter;
    private Handler mHandleCalculate, mHandleUpdate;
    private final AtomicBoolean mIsRunning = new AtomicBoolean();
    private int mWidth, mHeight;
    private final AtomicReference<PointF> mGravity = new AtomicReference<>();
    private Runnable mCalculator;
    private Runnable mUpdater;

    private static final String TAG = "Starfield";

    public boolean ismIsRunning() {
        return mIsRunning.get();
    }

    public void setmIsRunning(final boolean mIsRunning) {
        if (this.mIsRunning.get() != mIsRunning) {
            this.mIsRunning.set(mIsRunning);
            if (mIsRunning) {
                mHandleCalculate = new Handler();
                mHandleUpdate = new Handler();
                mCalculator = new CalculateStarfield();
                mUpdater = new UpdateStarfield();
                mHandleCalculate.post(mCalculator);
                mHandleCalculate.post(mUpdater);
                mPainter.setColor(GameUtility.getSettings().prefsColour);
            } else {
                if (mHandleCalculate != null) {
                    mHandleCalculate.removeCallbacksAndMessages(null);
                }
                if (mHandleUpdate != null) {
                    mHandleUpdate.removeCallbacksAndMessages(null);
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
        mStars.add(new Star2D(x > mWidth ? mWidth : x, y > mHeight ? mHeight : y, speed));
    }

    public void init(final int w, final int h, final int color) {
        mGravity.set(new PointF(0f, 0f));
        mWidth = w;
        mHeight = h;
        if (mPainter == null) {
            mPainter = new Paint();
            mPainter.setColor(GameUtility.getSettings().prefsColour);
            mPainter.setStrokeWidth(3f);
            //mPainter.setShadowLayer(2f, 2f, 2f, Color.GRAY);
        }
        Log.d(TAG, "Width  : " + w);
        Log.d(TAG, "Height : " + h);
        if (!mStars.isEmpty()) mStars.clear();
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
        if (!mIsRunning.get()) setmIsRunning(GameUtility.getSettings().prefsBlood);
        Log.d(TAG, "Focus");
        return super.hasFocus();
    }

    @Override
    public void onBecameForeground() {
        setmIsRunning(GameUtility.getSettings().prefsBlood);
        Log.d(TAG, "Enabled itself.");
    }

    @Override
    public void onBecameBackground() {
        Log.d(TAG, "Disabled itself.");
        setmIsRunning(false);
    }

    private class CalculateStarfield implements Runnable {
        @Override
        public void run() {
            if (mIsRunning.get()) {
                //final long time = System.nanoTime();
                for (final Star2D star2D : mStars) {
                    star2D.xy.x -= star2D.speedX + mGravity.get().x;
                    star2D.xy.y += mGravity.get().y + star2D.weight;

                    if (star2D.xy.x > mWidth) star2D.xy.x = 0;
                    else if (star2D.xy.x < 0) star2D.xy.x = mWidth;

                    if (star2D.xy.y > mHeight) star2D.xy.y = 0;
                    else if (star2D.xy.y < 0) star2D.xy.y = mHeight;

                    if (star2D.fade > 249) star2D.fadeDirection = true;
                    else if (star2D.fade < 10) star2D.fadeDirection = false;

                    if (!star2D.fadeDirection) star2D.fade += (int) star2D.fadespeed;
                    else star2D.fade -= (int) star2D.fadespeed;
                }
                //Log.d(TAG, "Time to update was (ns) : " + (System.nanoTime() - time));
                mHandleCalculate.postDelayed(mCalculator, CALC_FPS);
            }
        }
    }

    private class UpdateStarfield implements Runnable {

        @Override
        public void run() {
            invalidate();
            if (getMeasuredHeight() > mHeight && getMeasuredWidth() > mWidth) {
                init(getMeasuredWidth(), getMeasuredHeight(), Color.RED);
            } else if (mIsRunning.get()) {
                mHandleUpdate.postDelayed(mUpdater, DRAW_FPS);
            }
        }
    }

    /**
     * Apply adjustments for x / y speed
     * @param x The adjustment for X
     * @param y The adjustment for Y
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public void setGravity(final float x, final float y) {
        if (mIsRunning.get()) {
            mGravity.get().x = x >= LOW_PASS_FILTER || x <= -LOW_PASS_FILTER ? x : 0f;
            mGravity.get().y = y >= LOW_PASS_FILTER || y <= -LOW_PASS_FILTER ? y : 0f;
            if (x > HIGH_PASS_FILTER) mGravity.get().x = HIGH_PASS_FILTER;
            else if (x < -HIGH_PASS_FILTER) mGravity.get().x = -HIGH_PASS_FILTER;
            if (y > HIGH_PASS_FILTER) mGravity.get().y = HIGH_PASS_FILTER;
            else if (y < -HIGH_PASS_FILTER) mGravity.get().y = -HIGH_PASS_FILTER;
        } else {
            mGravity.get().x = 0.0f;
            mGravity.get().y = mGravity.get().x;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mIsRunning.get()) {
            for (final Star2D star : mStars) {
                mPainter.setAlpha(star.fade);
                canvas.drawCircle(star.xy.x, star.xy.y, 7, mPainter);
            }
        }
    }
}
