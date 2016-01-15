package com.undyingideas.thor.skafottet.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.LinkedList;

public class Camera extends View {

	// base vectors
	public final V3 O = new V3(0,0,0);

	public V3 cameraCenter; // camera position
	// orientation vectors
	private V3 D = V3.i;
	private V3 U = V3.j;
	private V3 R = V3.k;
	private V3 focusPoint;

	public double pivor = 0;

	Paint paint;
	Paint blackPaint;


	public LinkedList<V3> traj=new LinkedList<>();


	/** zoom **/
	private double z = 4;

	// display
	private S2 s2;

	V3[] cube;
	V3 c =new V3(0,0,0);
	M3 Rz;
	V3 u;
	M3 Su;
	public double northDegree = 0;

	public Camera(Context a,double sx, double sy, int ox, int oy, Paint paint){
		super(a);
		s2=new S2(a,sx, sy, ox, oy, paint);
		this.paint= paint;
		cameraCenter = new V3(12, 12, 6);
		moveCameraTo(cameraCenter);
		focusPoint = new V3(9,9,6);
		focus(focusPoint);
		setZoom(5);


		blackPaint = new Paint();
		blackPaint.setColor(Color.BLACK);
		blackPaint.setStrokeWidth(20);


		cube = new V3[8];
		System.out.println(Math.toDegrees(Math.PI/100));
		cube[0]=new V3(1,4,1);
		cube[1]=new V3(1,4,3);
		cube[2]=new V3(1,6,1);
		cube[3]=new V3(1,6,3);
		cube[4]=new V3(3,4,1);
		cube[5]=new V3(3,4,3);
		cube[6]=new V3(3,6,1);
		cube[7]=new V3(3,6,3);
		for (int i=0; i<cube.length; i++) c=c.add(cube[i]);
		c=c.mul(1.0/cube.length);


		u = new V3(0,1,1).unit();
		 Su = new M3(
				0,   -u.z,  u.y,
				u.z ,   0 , -u.x,
				-u.y, u.x , 0);

		double phi=Math.PI/100;
//    M3 Rz=I.add(Sz.mul(Math.sin(phi))).add(Sz.mul(Sz).mul(1-Math.cos(phi)));
		Rz=M3.inverse.add(Su.mul(Math.sin(phi))).add(Su.mul(Su).mul(1-Math.cos(phi)));

	}


	public V2 project(V3 p){
		V3 EP=p.sub(cameraCenter);
		
		double d=EP.dot(D);
		if(d < 0) return null;
		double u=EP.dot(U);
		double r=EP.dot(R);
		double rm=(r/d)*z;
		double um=(u/d)*z;
		return new V2(rm,um);
	}

	public void redraw(){
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		cube.paintCube(canvas,cube,this);
//		s2.drawLine(canvas,new V2 (1,1),new V2(10,10),paint);
//		s2.drawLine(canvas, new V2(10, 10), new V2(20, 10), paint);
		drawAxis(canvas);
		for(int i = 0; i < 10; i++){
			drawLine(canvas, new V3(i, 0, 0), new V3(i, 10, 0), paint);
			drawLine(canvas, new V3(0, i, 0), new V3(10, i, 0), paint);
		}

		for(int i = 25; i < 35; i++){
			drawLine(canvas, new V3(i, 25, 0), new V3(i, 35, 0), paint);
			drawLine(canvas, new V3(25, i, 0), new V3(10+25, i, 0), paint);
		}

		for(int i = 25; i < 35; i++){
			drawLine(canvas, new V3(i, 0, 0), new V3(i, 10, 0), paint);
			drawLine(canvas, new V3(0, i, 0), new V3(10, i, 0), paint);
		}

//		s2.draw(canvas);


		for (int i=0; i<cube.length; i++){
			cube[i]=Rz.mul(cube[i].sub(c)).add(c);
		}


		drawLine(canvas, cube[0], cube[1], blackPaint);
		drawLine(canvas, cube[1], cube[3], blackPaint);
		drawLine(canvas, cube[3], cube[2], blackPaint);
		drawLine(canvas, cube[2], cube[0], blackPaint);
		drawLine(canvas, cube[4], cube[5], blackPaint);
		drawLine(canvas, cube[5], cube[7], blackPaint);
		drawLine(canvas, cube[7], cube[6],blackPaint);
		drawLine(canvas, cube[6], cube[4],blackPaint);
		drawLine(canvas, cube[0], cube[4],blackPaint);
		drawLine(canvas, cube[1], cube[5],blackPaint);
		drawLine(canvas, cube[3], cube[7],blackPaint);
		drawLine(canvas, cube[2], cube[6],blackPaint);

		drawLine(canvas, new V3(1, 0, 0), new V3(1, 0, 1), blackPaint);

		drawPoint(canvas, getFocusPoint(), blackPaint);
		blackPaint.setTextSize(100f);

		Paint p = new Paint(Color.BLUE);
		p.setAntiAlias(true);
		p.setStrokeWidth(10f);

		for (V3 v3 : traj) {
			drawPoint(canvas, new V3(v3.y,0,v3.z) ,p );
		}



		canvas.drawText((int) pivor + "", 100, 100, blackPaint);
		canvas.drawText((int) northDegree + "" , 100,200, blackPaint);

	}
	public void drawAxis(Canvas canvas){
		drawLine(canvas, new V3(0, 0, 0), new V3(10, 0, 0), paint);
		drawLine(canvas, new V3(0, 0, 0), new V3(0, 10, 0), paint);
		drawLine(canvas, new V3(0,0,0), new V3(0,0,10),paint);

	}

	public V3 getFocusPoint(){
		return focusPoint;
	}

	public void setFocusPoint(V3 v){
		focusPoint = v;
		focus(focusPoint);
	}
	public void move(V3 vector){
		cameraCenter = cameraCenter.add(vector);
	}

	public V3 getCameraCenter(){
		return cameraCenter;
	}

	public void moveCameraTo(V3 p){
		cameraCenter=new V3(p.getX(),p.getY(),p.getZ());
	}

	public void focus(V3 p){
		D=p.sub(cameraCenter).unit();
		R=D.cross(V3.k).unit();
		U=R.cross(D);
	}

	public void rotateAround(double phi){
		rotate(M3.sX, phi);
	}
	public void rotateUp(double phi){
		rotate(M3.sY, phi);
	}
	public void rotateLeft(double phi){
		rotate(M3.sZ, phi);
	}
	
	public void rotate(M3 s, double phi){
		M3 Rz = M3.inverse.add( s.mul( Math.sin(phi))).add( s.mul( s).mul( 1-Math.cos( phi)));
		moveCameraTo(Rz.mul(cameraCenter));
	}
	public V3 rotateCameraFocusTo(M3 s, double phi){
		M3 Rz = M3.inverse.add( s.mul( Math.sin(phi))).add( s.mul( s).mul( 1-Math.cos( phi)));
		V3 cen= new V3(cameraCenter.x,cameraCenter.y,cameraCenter.z);
		return Rz.mul(focusPoint.sub(cen)).add(cen);
	}


	public void setZoom(double zoom){
		z = zoom;
	}

	@Override
	public void drawLine(Canvas c, V3 v1, V3 v2, Paint paint) {
		if(project(v1) == null ||project(v2) == null) return;
		s2.drawLine(c, project(v1), project(v2), paint);
	}
	public void drawPoint(Canvas c, V3 point,Paint p){
		if(project(point) == null ) return;
		s2.drawPoint(c, project(point), p);
	}
}