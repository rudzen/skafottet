package com.undyingideas.thor.skafottet.camera;

public class V2 {
	public double x,y;
	
	public static V2 i;
	public static V2 j;
	
	static {
		i = new V2(1, 0);
		j = new V2(0, 1);
	}
	
	public V2(double x, double y){
		this.x =x;
		this.y =y;
	}
	
	public V2 add(V2 v){
		return new V2(x + v.x, y + v.y);  
	}

	public V2 sub(V2 v){
		return new V2(x-v.x,y-v.y);
	}
	
	public V2 mul(double k){
		return new V2(x*k , y*k);
	}
	public V2 mul(V2 v){
		return new V2(x*v.x,y*v.y);
	}
	public double det(V2 v){
		return (x*v.y-y*v.x);
	}
	
	public double distanceToPoint(V2 v){
		double dx = v.x - x;
		double dy = v.y - y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public V2 projection(V2 v){
		return mul(v.mul(1.0/v.length()));
	}
	public double length(){
		return Math.sqrt(x*x + y*y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public V2 norm(V2 v1){
		return new V2(v1.x - x, v1.y - y);
	}
	
	public void normL(){
		double temp = x;
		x = -y;
		y = temp;
	}

	public V2 unit() {
		double len = length();
		return new V2(x / len, y / len);
	}
	
	public double scal(V2 v) {
		return x*y + v.x*v.y;
	}
	
	public boolean isOrtogonal(V2 v) {
		return scal(v) == 0;
	}
	
	public static V2 rotate(V2 v, double degrees) {
		return new M2(	Math.cos(degrees), -Math.sin(degrees),
                		Math.sin(degrees),  Math.cos(degrees)).mul(v);
	}
	
	public double arg() {
		return Math.atan(y / x);
	}
	
	public double projectionLen(V2 v) {
		return Math.abs(scal(v) / Math.abs(length()));
	}

	public V2 crossVector() {
		return new V2(y * (-1), x);
	}
	
	@Override
	public String toString() {
		return "[" + Double.toString(x) + " , " + Double.toString(y) + "]";
	}
	
	public boolean equals(V2 o){
		return (this.x == o.x && this.y == o.y);
	}
}
