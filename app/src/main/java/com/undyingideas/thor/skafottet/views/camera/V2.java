package com.undyingideas.thor.skafottet.views.camera;

@SuppressWarnings("SuspiciousNameCombination")
public class V2 {
    public float x, y;

    public static V2 i;
    public static V2 j;

    static {
        i = new V2(1, 0);
        j = new V2(0, 1);
    }

    public V2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public V2 add(final V2 v) {
        return new V2(x + v.x, y + v.y);
    }

    public V2 sub(final V2 v) {
        return new V2(x - v.x, y - v.y);
    }

    public V2 mul(final float k) {
        return new V2(x * k, y * k);
    }

    public V2 mul(final V2 v) {
        return new V2(x * v.x, y * v.y);
    }

    public float det(final V2 v) {
        return x * v.y - y * v.x;
    }

    public float distanceToPoint(final V2 v) {
        final float dx = v.x - x;
        final float dy = v.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public V2 projection(final V2 v) {
        return mul(v.mul((float) 1.0 / v.length()));
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float getX() {
        return x;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public V2 norm(final V2 v1) {
        return new V2(v1.x - x, v1.y - y);
    }

    public void normL() {
        final float temp = x;
        x = -y;
        y = temp;
    }

    public V2 unit() {
        final float len = length();
        return new V2(x / len, y / len);
    }

    public float scal(final V2 v) {
        return x * y + v.x * v.y;
    }

    public boolean isOrtogonal(final V2 v) {
        return scal(v) == 0;
    }

    public static V2 rotate(final V2 v, final float degrees) {
        return new M2((float) Math.cos(degrees), (float) -Math.sin(degrees),
                (float) Math.sin(degrees), (float) Math.cos(degrees)).mul(v);
    }

    public float arg() {
        return (float) Math.atan(y / x);
    }

    public float projectionLen(final V2 v) {
        return Math.abs(scal(v) / Math.abs(length()));
    }

    public V2 crossVector() {
        return new V2(-y, x);
    }

    @Override
    public String toString() {
        return "[" + Float.toString(x) + " , " + Float.toString(y) + "]";
    }

    public boolean equals(final V2 o) {
        return x == o.x && y == o.y;
    }
}
