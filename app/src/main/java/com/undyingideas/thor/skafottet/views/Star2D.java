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

import android.graphics.PointF;

import com.undyingideas.thor.skafottet.support.utility.NumberHelper;

@SuppressWarnings("ClassNamingConvention")
class Star2D {
    private static final int FADE_MULTIPLIER = 50;
    public final PointF xy = new PointF();
    private float size;
    public float speedX;
    public float fadespeed;
    public int fade;
    boolean fadeDirection;
    public final float weight;

    public Star2D(final float x, final float y, final float speed) {
        xy.x = x;
        xy.y = y;
        speedX = speed;
        fade = (int) NumberHelper.round(Math.random() * FADE_MULTIPLIER, 0);
        fadeDirection = (int) NumberHelper.round(Math.random() + 1, 0) == 1;
        fadespeed = (float) (Math.random() * 3 + 1);
        weight = fadespeed / 3;
    }

    public float getFadeSpeed() {
        return fadespeed;
    }

    public void setFadeSpeed(final float newFadeSpeed) {
        fadespeed = newFadeSpeed;
    }

    public PointF getXY() {
        return xy;
    }

    public void setXY(final PointF v) {
        xy.x = v.x;
        xy.y = v.y;
    }

    public float getX() {
        return xy.x;
    }

    public void setX(final float x) {
        xy.x = x;
    }

    public void addX(final float x) {
        xy.x += x;
    }

    public float getY() {
        return xy.y;
    }

    public void setY(final float y) {
        xy.y = y;
    }

    public void addY(final float y) {
        xy.y += y;
    }

    public float getSize() {
        return size;
    }

    public void setSize(final float size) {
        this.size = size;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(final float newSpeedX) {
        speedX = newSpeedX;
    }

    public int getFade() {
        return fade;
    }

    public void setFade(final int fade) {
        this.fade = fade;
    }

    public boolean isFadeDirection() {
        return fadeDirection;
    }

    public void setFadeDirection(final boolean fadeIn) {
        fadeDirection = fadeIn;
    }
}
