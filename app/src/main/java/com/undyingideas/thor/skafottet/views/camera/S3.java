package com.undyingideas.thor.skafottet.views.camera;

import android.graphics.Canvas;
import android.graphics.Paint;


public class S3 {
	private final S2 s2;

	public S3(final float sx, final float sy, final int ox, final int oy) {
		s2 = new S2(sx, sy, ox, oy);
	}

	public static V2 project(final V3 p) {
		return new V2(p.y, p.z);
	}


	public void drawLine(final Canvas c, final V3 v1, final V3 v2, final Paint paint) {
		 s2.drawLine(c,project(v1),project(v2),paint);
	}
}
