package com.undyingideas.thor.skafottet.views.camera;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Camera {

	// base vectors
	public final V3 O = new V3(0,0,0);

	public V3 cameraCenter; // camera position
	// orientation vectors
	private V3 D = V3.i;
	private V3 U = V3.j;
	private V3 R = V3.k;
	private V3 focusPoint;

	public float pivor = 0;

	/** zoom **/
	private float z = 4;

	// display
	private final S2 s2;

	V3 c =new V3(0,0,0);
	M3 Rz;
	V3 u;
	M3 Su;
	public float northDegree = 0;

	public Camera(final float sx, final float sy, final  int ox, final int oy){
		s2=new S2(sx, sy, ox, oy);
		cameraCenter = new V3(12, 12, 6);
		moveCameraTo(cameraCenter);
		focusPoint = new V3(9,9,6);
		focus(focusPoint);
		z = 4.5f;

		u = new V3(0,1,1).unit();
		 Su = new M3(0,   -u.z,  u.y,
				u.z ,   0 , -u.x,
				-u.y, u.x , 0);

		final float phi=(float)(Math.PI/100);
//    M3 Rz=I.add(Sz.mul(Math.sin(phi))).add(Sz.mul(Sz).mul(1-Math.cos(phi)));
		Rz=M3.inverse.add(Su.mul((float)Math.sin(phi))).add(Su.mul(Su).mul((float) (1 - Math.cos(phi))));

	}


	public V2 project(final V3 p){
		V3 EP=p.sub(cameraCenter);

		float d=EP.dot(D);
		if(d < 0) return null;
		float u=EP.dot(U);
		float r=EP.dot(R);
		float rm=(r/d)*z;
		float um=(u/d)*z;
		return new V2(rm,um);
	}

	public void drawAxis(Canvas canvas, Paint paint){
		drawLine(canvas, new V3(0, 0, 0), new V3(10, 0, 0), paint);
		drawLine(canvas, new V3(0, 0, 0), new V3(0, 10, 0), paint);
		drawLine(canvas, new V3(0, 0, 0), new V3(0, 0, 10), paint);

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

	public void rotateAround(float phi){
		rotate(M3.sX, phi);
	}
	public void rotateUp(float phi){
		rotate(M3.sY, phi);
	}
	public void rotateLeft(float phi){
		rotate(M3.sZ, phi);
	}
	
	public void rotate(M3 s, float phi){
		M3 Rz = M3.inverse.add( s.mul((float)( Math.sin(phi)))).add(s.mul(s).mul((float) (1 - Math.cos(phi))));
		moveCameraTo(Rz.mul(cameraCenter));
	}
	public V3 rotateCameraFocusTo(M3 s, float phi){
		M3 Rz = M3.inverse.add( s.mul((float) Math.sin(phi))).add( s.mul( s).mul( (float)(1-Math.cos( phi))));
		V3 cen= new V3(cameraCenter.x,cameraCenter.y,cameraCenter.z);
		return Rz.mul(focusPoint.sub(cen)).add(cen);
	}


	public void setZoom(float zoom){
		z = zoom;
	}

	public void drawLine(Canvas c, V3 v1, V3 v2, Paint paint) {
		if(project(v1) == null ||project(v2) == null) return;
		s2.drawLine(c, project(v1), project(v2), paint);
	}
	public void drawPoint(Canvas c, V3 point,Paint p){
		if(project(point) == null ) return;
		s2.drawPoint(c, project(point), p);
	}
}