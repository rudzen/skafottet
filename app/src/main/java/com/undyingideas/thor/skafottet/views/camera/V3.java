package com.undyingideas.thor.skafottet.views.camera;

public class V3 {

    public float x, y, z;
    public final static V3 i, j, k;

    static {
        i = new V3(1, 0, 0);
        j = new V3(0, 1, 0);
        k = new V3(0, 0, 1);
    }

    public V3(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public V3 add(final V3 v) {
        return new V3(x + v.x, y + v.y, z + v.z);
    }

    public V3 add(final float x, final float y, final float z) {
        return new V3(this.x + x, this.y + y, this.z + z);
    }

    public V3 add(final float valueToAdd) {
        return new V3(x + valueToAdd, y + valueToAdd, z + valueToAdd);
    }

    public V3 sub(final V3 v) {
        return new V3(x - v.x, y - v.y, z - v.z);
    }

    public V3 mul(final float k) {
        return new V3(x * k, y * k, z * k);
    }

    public V3 mul(final V3 v) {
        return new V3(x * v.x, y * v.y, z * v.z);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
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

    public void setZ(final float z) {
        this.z = z;
    }

    public float getZ() {
        return z;
    }

    public V3 unit() {
        final float len = length();
        return new V3(x / len, y / len, z / len);
    }

    public V3 cross(final V3 v) {

        return new V3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);

    }

    public float dot(final V3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    @Override
    public String toString() {
        return "X " + x + " Y " + y + " Z " + z;
    }

}
