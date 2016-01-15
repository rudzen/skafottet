package com.undyingideas.thor.skafottet.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;


public class S2 extends View {
	public V2 o; // Origo
	private static final M2 F; // Flip
	private M2 S, T; // Scale, Transform
	Paint p;
	public Cube cube;
	public ArrayList<V2> point;
	static {
		F = new M2(1, 0, 0, -1);
	}
	public S2(Context a){
		super(a);
	}

	public S2(Context a, double sx, double sy, double ox, double oy, Paint p) {
		super(a);
		o = new V2(ox, oy);
		S = new M2(sx, 0, 0, sy);
		T = F.mul(S);
		this.p = p;
	}
	@Override
	protected void onDraw(Canvas c){
		super.onDraw(c);
//		V2 p1 = new V2(20,3);
//		V2 p2 = new V2(10,-20);
//		drawLine(c,p1,p2,p);
//		drawCube(c,cube,p);
//		c.drawLine((float) p1w.x, (float) p1w.y, (float) p2w.x, (float) p2w.y, p);
//		c.drawLine(500,200,300,500,p);
//		c.drawLine(800, 200, 300, 500,p);
	}

	public V2 transform(V2 v) {
		return T.mul(v).add(o);
	}


	public void drawLine(Canvas g, V2 p1, V2 p2, Paint p) {
		V2 p1w = transform(p1);
		V2 p2w = transform(p2);
		g.drawLine((float) p1w.x, (float) p1w.y, (float) p2w.x, (float) p2w.y, p);
		draw(g);
	}
	public void drawPoint(Canvas c, V2 point, Paint p){
		V2 point1 = transform(point);
		c.drawPoint((float)point1.x, (float)point1.y, p);
		draw(c);
	}

	public void drawCube(Canvas g, Cube cube, Camera camera){
		cube.paintCube(g, cube, camera);
	}

	public void setOrigo(int ox, int oy) {
		o.x = ox;
		o.y = oy;
	}


}
