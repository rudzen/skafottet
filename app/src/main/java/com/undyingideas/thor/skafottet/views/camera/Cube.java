package com.undyingideas.thor.skafottet.views.camera;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Cube {
    private V3[] cube = new V3[8];
    private V3 c = new V3(0, 0, 0);

    public Cube(final V3... cube) {
        if (cube.length == 8) this.cube = cube;
        for (final V3 aCube : cube) c = c.add(aCube);
        c = c.mul(1f / cube.length);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Cube(final V3 center, final float width, final float length, final float height) {
        final float widthHalf = width / 2f;
        final float heightHalf = height / 2f;
        final float lengthHalf = length / 2f;
        cube[0] = center.add(new V3(-heightHalf, -widthHalf, -lengthHalf));
        cube[1] = center.add(new V3(-heightHalf, -widthHalf, lengthHalf));
        cube[2] = center.add(new V3(-heightHalf, widthHalf, -lengthHalf));
        cube[3] = center.add(new V3(-heightHalf, widthHalf, lengthHalf));
        cube[4] = center.add(new V3(heightHalf, -widthHalf, -lengthHalf));
        cube[5] = center.add(new V3(heightHalf, -widthHalf, lengthHalf));
        cube[6] = center.add(new V3(heightHalf, widthHalf, -lengthHalf));
        cube[7] = center.add(new V3(heightHalf, widthHalf, lengthHalf));
        c.x = center.x;
        c.y = center.y;
        c.z = center.z;
    }

    void move(final V3 vector) {
        c = c.add(vector);
        for (int i = 0; i < cube.length; i++) {
            cube[i] = cube[i].add(vector);
        }
    }

    void moveTo(final V3 point) {
        c = point.sub(c);
        for (int i = 0; i < cube.length; i++) {
            cube[i] = cube[i].add(c);
        }
        c.x = point.x;
        c.y = point.y;
        c.z = point.z;
    }

    void rotate(final M3 r) {
        for (int i = 0; i < cube.length; i++) {
            cube[i] = r.mul(cube[i].sub(c)).add(c);
        }
    }

    private V3[][] readyToPaint() {
        final V3[][] r = new V3[2][];
        r[0] = new V3[12];
        r[1] = new V3[12];
        r[0][0] = cube[0];
        r[1][0] = cube[1];
        r[0][1] = cube[1];
        r[1][1] = cube[3];
        r[0][2] = cube[3];
        r[1][2] = cube[2];
        r[0][3] = cube[2];
        r[1][3] = cube[0];
        r[0][4] = cube[4];
        r[1][4] = cube[5];
        r[0][5] = cube[5];
        r[1][5] = cube[7];
        r[0][6] = cube[7];
        r[1][6] = cube[6];
        r[0][7] = cube[6];
        r[1][7] = cube[4];
        r[0][8] = cube[0];
        r[1][8] = cube[4];
        r[0][9] = cube[1];
        r[1][9] = cube[5];
        r[0][10] = cube[3];
        r[1][10] = cube[7];
        r[0][11] = cube[2];
        r[1][11] = cube[6];
        return r;
    }

    public static void paintCube(final Canvas g, final Cube c, final Camera S) {
        final V3[][] v = c.readyToPaint();
        final Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(20);
        for (int i = 0; i < v[0].length; i++) {
            S.drawLine(g, v[0][i], v[1][i], p);
        }
    }

    public void draw(final Camera s, final Canvas c, final Paint paint) {
        s.drawLine(c, cube[0], cube[1], paint);
        s.drawLine(c, cube[1], cube[3], paint);
        s.drawLine(c, cube[3], cube[2], paint);
        s.drawLine(c, cube[2], cube[0], paint);
        s.drawLine(c, cube[4], cube[5], paint);
        s.drawLine(c, cube[5], cube[7], paint);
        s.drawLine(c, cube[7], cube[6], paint);
        s.drawLine(c, cube[6], cube[4], paint);
        s.drawLine(c, cube[0], cube[4], paint);
        s.drawLine(c, cube[1], cube[5], paint);
        s.drawLine(c, cube[3], cube[7], paint);
        s.drawLine(c, cube[2], cube[6], paint);
    }
}
