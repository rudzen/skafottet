package com.undyingideas.thor.skafottet.views.camera;

public class M2 {
    public float a, b, c, d;

    public M2(final float a, final float b, final float c, final float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public M2 add(final M2 m) {
        return new M2(a + m.a, b + m.b, c + m.c, d + m.d);
    }

    public M2 sub(final M2 m) {
        return new M2(a - m.a, b - m.b, c - m.c, d - m.d);
    }

    public M2 scalarMul(final float x) {
        return new M2(a * x, b * x, c * x, d * x);
    }

    public M2 inverse() {
        return new M2(d, -b, -c, a).scalarMul(1 / det());
    }

    public float det() {
        return a * d - c * b;
    }

    public M2 mul(final M2 m) {
        return new M2(a * m.a + b * m.c, a * m.b + b * m.d,
                c * m.a + d * m.c, c * m.b + d * m.d);
    }

    public V2 mul(final V2 v) {
        return new V2(a * v.x + b * v.y, c * v.x + d * v.y);
    }

}
