package com.undyingideas.thor.skafottet.views.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class S3 extends View {
	private S2 s2;

	public S3(Context a, float sx, float sy, int ox, int oy, Paint paint) {
		super(a);
		s2 = new S2(a,sx, sy, ox, oy,paint);
	}

	public static V2 project(V3 p) {
		return new V2(p.y, p.z);
	}


	public void drawLine(Canvas c, V3 v1, V3 v2, Paint paint) {
		 s2.drawLine(c,project(v1),project(v2),paint);
	}
}
