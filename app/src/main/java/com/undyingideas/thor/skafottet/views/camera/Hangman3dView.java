package com.undyingideas.thor.skafottet.views.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by theis on 15-01-2016.
 */
public class Hangman3dView extends View{
    public Hangman3dView(Context context) {
        super(context);
        buildGallow();
    }

    public Hangman3dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildGallow();
    }

    public Hangman3dView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildGallow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        Log.d("hangman", ""+x + " , "+y);

            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            // reverse direction of rotation above the mid-line
            if (y > getHeight() / 2) {
                dx = dx * -1 ;
            }

            // reverse direction of rotation to left of the mid-line
            if (x < getWidth() / 2) {
                dy = dy * -1 ;
            }
            Log.d("hangman", ""+dx);
            S.rotateLeft(dx * TOUCH_SCALE_FACTOR);

        invalidate();
            mPreviousX = x;
            mPreviousY = y;
            return true;

    }

    private float mPreviousX, mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 10.0f / 320;

    int framerate=25;
    int delay=1000/framerate;

    // Simulate time
    double ts;
    double t;     // simulation time in sec.

    // App
    Camera S=new Camera(150,150, 400,400, new Paint());
    V3 u = new V3(0,1,1).unit();
    //		M3 Sz = new M3(
//				0,   -u.z,  u.y,
//				u.z ,   0 , -u.x,
//				-u.y, u.x , 0);
    double phi = Math.PI/100;
    M3 Rz = M3.inverse.add(M3.sZ.mul((float)Math.sin(phi))).add(M3.sZ.mul(M3.sZ).mul((float) (1 - Math.cos(phi))));

    V3 c = new V3(0,0,3);
    Cube[] gallow;
    Cube[] body;
    int fejl = 0;


    void buildGallow(){

        gallow = new Cube[8];
        gallow[0] = new Cube(new V3(-3f, -3f, 0.5f), 1f, 1f, 1f); // forhøjning
        gallow[1] = new Cube(new V3(-3, 3, 0.5f), 1, 1, 1);
        gallow[2] = new Cube(new V3(3, -3, 0.5f), 1, 1, 1);
        gallow[3] = new Cube(new V3(3, 3, 0.5f), 1, 1, 1);

        gallow[4] = new Cube(new V3(0,0, 1.25f), 7,0.5f, 7); // platform

        gallow[5] = new Cube(new V3(-3, 0, 6.5f), 1,10,1); // stolpe
        gallow[6] = new Cube(new V3(-0.75f, 0, 11), 1, 1, 3.5f); // bar

        gallow[7] = new Cube(new V3(0.75f,0,9.75f), 0.25f,1.5f, 0.25f); // snor
    }

    void buildBody(){
        body = new Cube[6];
        body[0] = new Cube(new V3(0.75f, 0, 8.75f), 1,1,1); // head
        body[1] = new Cube(new V3(0.75f, 0, 6.75f), 0.5f, 3, 0.5f); // body
        body[2] = new Cube(new V3(1.5f, 0, 7.75f), 0.5f,0.5f,1); // arm
        body[3] = new Cube(new V3(0, 0, 7.75f), 0.5f,0.5f,1); // arm
        body[4] = new Cube(new V3(0.25f,0,4.75f),0.5f,2,0.5f); // leg
        body[5] = new Cube(new V3(1.25f,0,4.75f),0.5f,2,0.5f); // leg
    }


    @Override
    protected void onDraw(Canvas c){
        Paint p = new Paint();
        p.setStrokeWidth(10);
        p.setColor(Color.BLUE);

        Log.d("draw3", "draw");
        S.focus(this.c);
        for(Cube cu: gallow){
            cu.draw(S, c, p);
        }
    }

}
