package com.undyingideas.thor.skafottet.views.camera;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Theis' on 17-01-2016.
 */
public class Rope {

    V3[][] rope = new V3[2][5];

    public Rope(V3 up1, V3 up2, V3 down1, V3 down2) {
        rope[0][0] = up1; rope[1][0] = up2;
        rope[0][2] = up1.add(down1).mul(0.5f); rope[1][2] = up2.add(down2).mul(0.5f);
        rope[0][1] = up1.add(rope[0][2]).mul(0.5f); rope[1][1] = up2.add(rope[1][2]).mul(0.5f);
        rope[0][3] = down1.add(rope[0][2]).mul(0.5f); rope[1][3] = down2.add(rope[1][2]).mul(0.5f);
        rope[0][4] = down1; rope[1][4] = down2;
    }

    public void draw(Camera s, Canvas c, Paint paint) {
        for(int i = 0; i < 4; i++)
            s.drawLine(c, rope[0][i], rope[1][i+1], paint);
        s.drawLine(c, rope[0][0], rope[0][4], paint);
        s.drawLine(c, rope[1][0], rope[1][4], paint);
    }
}