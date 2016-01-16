package com.undyingideas.thor.skafottet.views.camera;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;


public class S2 {
	public V2 o; // Origo
	private static final M2 F; // Flip
	private final M2 S;
	private final M2 T; // Scale, Transform
	public Cube cube;
	public ArrayList<V2> point;
	static {
		F = new M2(1, 0, 0, -1);
	}

	public S2(final float sx, final float sy, final float ox, final float oy) {
		o = new V2(ox, oy);
		S = new M2(sx, 0, 0, sy);
		T = F.mul(S);
	}

	public V2 transform(final V2 v) {
		return T.mul(v).add(o);
	}


	public void drawLine(final Canvas g, final V2 p1, final V2 p2, final Paint p) {
		final V2 p1w = transform(p1);
		final V2 p2w = transform(p2);
		g.drawLine((float) p1w.x, (float) p1w.y, (float) p2w.x, (float) p2w.y, p);
	}
	public void drawPoint(final Canvas c, final V2 point, final Paint p){
		final V2 point1 = transform(point);
		c.drawPoint((float) point1.x, (float) point1.y, p);
	}

	public void drawCube(final Canvas g, final Cube cube, final Camera camera){
		cube.paintCube(g, cube, camera);
	}

	public void setOrigo(final int ox, final int oy) {
		o.x = ox;
		o.y = oy;
	}


}
