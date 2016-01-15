package com.undyingideas.thor.skafottet.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class S3 extends View implements IPlane3D {
	private S2 s2;

	public S3(Context a, double sx, double sy, int ox, int oy, Paint paint) {
		super(a);
		s2 = new S2(a,sx, sy, ox, oy,paint);
	}

	public static V2 project(V3 p) {
		return new V2(p.y, p.z);
	}


	@Override
	public void drawLine(Canvas c, V3 v1, V3 v2, Paint paint) {
		 s2.drawLine(c,project(v1),project(v2),paint);
	}
}
