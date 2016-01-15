package com.undyingideas.thor.skafottet.camera;

public class V3 {
	
	public double x, y, z;
	public final static V3 i, j, k;
	
	static {
		i = new V3(1, 0, 0);
		j = new V3(0, 1, 0);
		k = new V3(0, 0, 1);
	}
	
	public V3(double x, double y ,double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public V3 add(V3 v){
		return new V3(x + v.x, y + v.y, z + v.z);  
	}
	
	public V3 add(double x, double y, double z) {
		return new V3(this.x + x, this.y + y, this.z + z);
	}
	
	public V3 add(double valueToAdd) {
		return new V3(x + valueToAdd, y + valueToAdd, z + valueToAdd);
	}

	public V3 sub(V3 v){
		return new V3(x - v.x, y - v.y, z - v.z);
	}
	
	public V3 mul(double k){
		return new V3(x * k, y * k , z * k);
	}
	public V3 mul(V3 v){
		return new V3(x * v.x, y * v.y , z * v.z);
	}
	
	public double length(){
		return Math.sqrt(x * x + y * y + z * z);
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
	public void setZ(double z){
		this.z = z;
	}
	public double getZ(){
		return z;
	}

	public V3 unit() {
		double len = length();
		return new V3(x / len, y / len, z / len);
	}
	
	public V3 cross(V3 v) {

		return new V3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);

	}
	
	public double dot(V3 v){
		return x * v.x + y * v.y + z * v.z;
	}

	@Override
	public String toString(){
		return "X " + x + " Y " + y + " Z " + z;
	}

}
