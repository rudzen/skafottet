package com.undyingideas.thor.skafottet.camera;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.gump.sensorapp.V3;



public interface IPlane3D {


	void drawLine(Canvas c, V3 v1, V3 v2, Paint paint);

	
}
