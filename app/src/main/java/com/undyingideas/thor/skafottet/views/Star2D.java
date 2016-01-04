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

import android.graphics.PointF;

class Star2D {
    public PointF xy;
    private float size;
    public float speedX;
    public float fadespeed;
    public int fade;
    boolean fadeDirection;

    Star2D() { }

    public Star2D(final float x, final float y, final float speed) {
        xy = new PointF(x, y);
        speedX = speed;
        fade = (int) (Math.random() * 50);
        fadeDirection = (int) (Math.random() + 1) == 1;
        fadespeed = (float) (Math.random() * 3 + 1);
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
