package com.undyingideas.thor.skafottet.views.camera;

public class M3 {

	public float a, b, c, d, e, f, g, h, i;
	
	public final static M3 inverse;
    public final static M3 sX;
    public final static M3 sY;
    public final static M3 sZ;
    
    static {
    	inverse = new M3(1, 0, 0,
    					 0, 1, 0,
    					 0, 0, 1);
        sX = new M3(0, 0, 0,
			  		0, 0,-1,
			  		0, 1, 0);
    	sY = new M3(0, 0,-1,
		      	0, 0, 0,
		      	1, 0, 0);
    	sZ = new M3(0,-1, 0,
				1, 0, 0,
				0, 0, 0);
    }
    
	public M3(float a, float b, float c, float d, float e, float f, float g, float h, float i) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
	}

	public M3(V3 col1, V3 col2, V3 col3) {
		a = col1.x;
		b = col2.x;
		c = col3.x;
		d = col1.y;
		e = col2.y;
		f = col3.y;
		g = col1.z;
		h = col2.z;
		i = col3.z;
	}
	
	public static M3 empty() {
		return new M3(0,0,0,0,0,0,0,0,0);
	}
	
	public M3 add(M3 m) {
		return new M3(a + m.a, b + m.b, c + m.c, d + m.d, e + m.e, f + m.f, g + m.g, h + m.h, i + m.i);
	}

	public M3 sub(M3 m) {
		return new M3(a - m.a, b - m.b, c - m.c, d - m.d, e - m.e, f - m.f, g - m.g, h - m.h, i - m.i);
	}

	public M3 mul(float x) {
		return new M3(a * x, b * x, c * x, d * x , e*x , f*x , g*x,h*x,i*x);
	}

	public V3 getColumVector(int index) {
		float valX, valY, valZ = valX = valY = 0;
		if (index == 1) {
			valX += a;
			valY += d;
		    valZ += g;
		} else if (index == 2) {
			valX += b;
			valY += e;
			valZ += h;
		} else if (index == 3) {
			valX += c;
			valY += f;
			valZ += i;
		}
		return new V3(valX, valY, valZ);
	}
	
	public M3 mul(M3 m) {
		return new M3(
				a*m.a+b*m.d+c*m.g,
				a*m.b+b*m.e+c*m.h,
				a*m.c+b*m.f+c*m.i,
				d*m.a+e*m.d+f*m.g,
				d*m.b+e*m.e+f*m.h,
				d*m.c+e*m.f+f*m.i,
				g*m.a+h*m.d+i*m.g,
				g*m.b+h*m.e+i*m.h,
				g*m.c+h*m.f+i*m.i);
	}

	public V3 mul(V3 v) {
		return new V3(
				a*v.x + b*v.y+c*v.z,
				d*v.x + e*v.y+f*v.z,
				g*v.x + h*v.y+i*v.z);
	}

	/**
	 * Transposes this matrix.
	 * @return The matrix transposed
	 */
	public M3 transpose() {
		return new M3(a, d, g,
					  b, e, h,
					  c, f, i);
	}
	
	/**
	 * Transposes a arbitrary matrix.
	 * @param matrix : The matrix to transpose
	 * @return The matrix transposed
	 */
	public static M3 transpose(M3 matrix) {
		return new M3(matrix.a, matrix.d, matrix.g,
					  matrix.b, matrix.e, matrix.h,
					  matrix.c, matrix.f, matrix.i);
	}
	
	
}
