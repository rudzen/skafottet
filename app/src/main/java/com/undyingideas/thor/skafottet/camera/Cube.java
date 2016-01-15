package com.undyingideas.thor.skafottet.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Cube extends View {
	V3[] cube = new V3[8];
	public V3 c = new V3(0,0,0);
	public Cube(Context a, V3[] cube){
		super(a);
		if (cube.length == 8) this.cube = cube;
		for (int i=0; i<cube.length; i++) c=c.add(cube[i]);
		c=c.mul(1.0/cube.length);
	}
	public Cube(Context a ,V3 center, double width, double length, double height){
		super(a);
		c = center;
		cube[0] = c.add(new V3(-height/2,-width/2,-length/2));
		cube[1] = c.add(new V3(-height/2,-width/2,length/2));
		cube[2] = c.add(new V3(-height/2,width/2,-length/2));
		cube[3] = c.add(new V3(-height/2,width/2,length/2));
		cube[4] = c.add(new V3(height/2,-width/2,-length/2));
		cube[5] = c.add(new V3(height/2,-width/2,length/2));
		cube[6] = c.add(new V3(height/2,width/2,-length/2));
		cube[7] = c.add(new V3(height/2,width/2,length/2));
	}
	
	void move(V3 vector){
		c = c.add(vector);
		for(int i = 0; i < cube.length; i++)
			cube[i] = cube[i].add(vector);
	}
	void moveTo(V3 point){
		c = point.sub(c);
		for(int i = 0; i < cube.length; i++)
			cube[i] = cube[i].add(c);
		c = point;
	}
	void rotate(M3 r){
		for (int i=0; i<cube.length; i++){
	        cube[i]=r.mul(cube[i].sub(c)).add(c);
	      }
	}
	public V3[][] readyToPaint(){
		V3[][] r = new V3[2][];
		r[0] = new V3[12]; r[1] = new V3[12];
		r[0][0] = cube[0]; r[1][0] = cube[1];
		r[0][1] = cube[1]; r[1][1] = cube[3];
		r[0][2] = cube[3]; r[1][2] = cube[2];
		r[0][3] = cube[2]; r[1][3] = cube[0];
		r[0][4] = cube[4]; r[1][4] = cube[5];
		r[0][5] = cube[5]; r[1][5] = cube[7];
		r[0][6] = cube[7]; r[1][6] = cube[6];
		r[0][7] = cube[6]; r[1][7] = cube[4];
		r[0][8] = cube[0]; r[1][8] = cube[4];
		r[0][9] = cube[1]; r[1][9] = cube[5];
		r[0][10] = cube[3]; r[1][10] = cube[7];
		r[0][11] = cube[2]; r[1][11] = cube[6];
		return r;
	}
	
	public void paintCube(Canvas g, Cube c, Camera S){
		V3[][] v = c.readyToPaint();
		Paint p = new Paint();
		p.setColor(Color.BLUE);
		p.setStrokeWidth(20);
		for(int i = 0; i < v[0].length; i++)
			S.drawLine(g, v[0][i], v[1][i],p);
	}

	@Override
	public void onDraw(Canvas c){
//		paintCube(c);
	}
}
