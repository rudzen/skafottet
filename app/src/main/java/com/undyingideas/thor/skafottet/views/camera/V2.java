package com.undyingideas.thor.skafottet.views.camera;

public class V2 {
	public float x,y;
	
	public static V2 i;
	public static V2 j;
	
	static {
		i = new V2(1, 0);
		j = new V2(0, 1);
	}
	
	public V2(float x, float y){
		this.x =x;
		this.y =y;
	}
	
	public V2 add(V2 v){
		return new V2(x + v.x, y + v.y);  
	}

	public V2 sub(V2 v){
		return new V2(x-v.x,y-v.y);
	}
	
	public V2 mul(float k){
		return new V2(x*k , y*k);
	}
	public V2 mul(V2 v){
		return new V2(x*v.x,y*v.y);
	}
	public float det(V2 v){
		return (x*v.y-y*v.x);
	}
	
	public float distanceToPoint(V2 v){
		float dx = v.x - x;
		float dy = v.y - y;
		return (float)Math.sqrt(dx*dx + dy*dy);
	}
	
	public V2 projection(V2 v){
		return mul(v.mul((float)1.0/v.length()));
	}
	public float length(){
		return (float)Math.sqrt(x*x + y*y);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	public V2 norm(V2 v1){
		return new V2(v1.x - x, v1.y - y);
	}
	
	public void normL(){
		float temp = x;
		x = -y;
		y = temp;
	}

	public V2 unit() {
		float len = length();
		return new V2(x / len, y / len);
	}
	
	public float scal(V2 v) {
		return x*y + v.x*v.y;
	}
	
	public boolean isOrtogonal(V2 v) {
		return scal(v) == 0;
	}
	
	public static V2 rotate(V2 v, float degrees) {
		return new M2(	Math.cos(degrees), -Math.sin(degrees),
                		Math.sin(degrees),  Math.cos(degrees)).mul(v);
	}
	
	public float arg() {
		return (float)Math.atan(y / x);
	}
	
	public float projectionLen(V2 v) {
		return Math.abs(scal(v) / Math.abs(length()));
	}

	public V2 crossVector() {
		return new V2(y * (-1), x);
	}
	
	@Override
	public String toString() {
		return "[" + Float.toString(x) + " , " + Float.toString(y) + "]";
	}
	
	public boolean equals(V2 o){
		return (this.x == o.x && this.y == o.y);
	}
}
