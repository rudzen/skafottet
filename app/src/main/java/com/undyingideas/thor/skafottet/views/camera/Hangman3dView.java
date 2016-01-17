package com.undyingideas.thor.skafottet.views.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by theis on 15-01-2016.
 *
 */
public class Hangman3dView extends View{

    public final static String TAG = "hangman3dview";

    private float mPreviousX, mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 2f / 180;
    int framerate=25;
    int delay=1000/framerate;

    // Simulate time
    double ts;
    double t;     // simulation time in sec.
    // App
    Camera S = new Camera(150,150, 400,400);
    V3 u = new V3(0,1,1).unit();
    double phi = Math.PI/100;
    M3 Rz = M3.inverse.add(M3.sZ.mul((float)Math.sin(phi))).add(M3.sZ.mul(M3.sZ).mul((float) (1 - Math.cos(phi))));

    V3 c = new V3(0,0,3);
    Cube[] gallow;
    Cube[] body;
    Rope[] rope;
    int errors;
    Paint p = new Paint();

    public Hangman3dView(final Context context) {
        super(context);
        buildGallow();
        buildRope();
        buildBody();
        buildOther();
    }

    public Hangman3dView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        buildGallow();
        buildRope();
        buildBody();
        buildOther();
    }

    public Hangman3dView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildGallow();
        buildRope();
        buildBody();
        buildOther();
    }


    @Override
    public boolean onTouchEvent(final MotionEvent e){
        final float x = e.getX(), y = e.getY();
        switch (e.getAction()){
            case ACTION_MOVE :
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if (y > getHeight() / 2) {
                    dx *= -1 ;          // reverse direction of rotation above the mid-line
                }
                if (x < getWidth() / 2) {
                    dy *= -1;           // reverse direction of rotation to left of the mid-line
                }
                S.rotateLeft(dx * TOUCH_SCALE_FACTOR);
                invalidate();
                //noinspection fallthrough
            case ACTION_DOWN:
                if (e.getAction()!= ACTION_MOVE) Log.d(TAG, "touchstarted");
                mPreviousX = x;
                mPreviousY = y;
                break;
            case ACTION_UP :
                Log.d(TAG, "touchstopped");
                break;
            default:
                Log.d(TAG, "unaccounted event " +e.getAction());
        }
        return true;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        final float x = e.getX(), y = e.getY();
//        switch (e.getAction()) {
//            case ACTION_MOVE :
//                float dx = x - mPreviousX;
//                if (y > getHeight() / 2) {
//                    dx *= -1 ;          // reverse direction of rotation above the mid-line
//                }
//                acc += dx;
//                break;
//            case ACTION_DOWN:
//                mPreviousX = x;
//                mPreviousY = y;
//                break;
//            case ACTION_UP :
//                Log.d(TAG, "touchstopped");
//                break;
//            default:
//                Log.d(TAG, "unaccounted event " +e.getAction());
//        }
//        return true;
//    }
//
//    float acc = 0;



    private void buildGallow(){
        gallow = new Cube[8];
        gallow[0] = new Cube(new V3(-3f, -3f, 0.5f), 1f, 1f, 1f); // forhøjning
        gallow[1] = new Cube(new V3(-3, 3, 0.5f), 1, 1, 1);
        gallow[2] = new Cube(new V3(3, -3, 0.5f), 1, 1, 1);
        gallow[3] = new Cube(new V3(3, 3, 0.5f), 1, 1, 1);

        gallow[4] = new Cube(new V3(0,0, 1.25f), 7,0.5f, 7); // platform

        gallow[5] = new Cube(new V3(-3, 0, 6.5f), 1,10,1); // stolpe
        gallow[6] = new Cube(new V3(-0.5f, 0, 11), 1, 1, 4f); // bar
        float f1 = 0.2f, f2 = -0.2f, v1 = -2.5f, low = 10.5f, v2 = -2.2f;
        gallow[7] = new Cube(new V3(v1, f1, 10f), new V3(v1, f2, 10f), new V3(v1, f1, 10.125f), new V3(v1, f2, 10.125f),
                new V3(v2, f1, low), new V3(v2, f2, low), new V3(-2.3f, f1, low), new V3(-2.3f, f2, low));
        //gallow[7] = new Cube(new V3(0.75f,0,9.75f), 0.25f,1.5f, 0.25f); // snor
    }

    private void buildBody(){
        body = new Cube[6];
        body[0] = new Cube(new V3(0.75f, 0, 8.75f), 1,1,1); // head
        body[1] = new Cube(new V3(0.75f, 0, 6.75f), 0.5f, 3, 0.5f); // body
        body[2] = new Cube(new V3(1, 0.25f, 7.65f), new V3(1, -0.25f, 7.65f), new V3(1, 0.25f, 8f), new V3(1,-0.25f, 8f),
                new V3(2, 0.25f, 6.9f), new V3(2, -0.25f, 6.9f), new V3(2, 0.25f, 7.25f), new V3(2,-0.25f, 7.25f));
        body[3] = new Cube(new V3(0.5f, 0.25f, 7.65f), new V3(0.5f, -0.25f, 7.65f), new V3(0.5f, 0.25f, 8f), new V3(0.5f,-0.25f, 8f),
                new V3(-0.5f, 0.25f, 6.9f), new V3(-0.5f, -0.25f, 6.9f), new V3(-0.5f, 0.25f, 7.25f), new V3(-0.5f,-0.25f, 7.25f));

        body[4] = new Cube(new V3(1, 0.25f, 5.25f), new V3(1, -0.25f, 5.25f), new V3(1, 0.25f, 5.8f), new V3(1,-0.25f, 5.8f),
                new V3(2, 0.25f, 3.75f), new V3(2, -0.25f, 3.75f), new V3(2.3f, 0.25f, 3.75f), new V3(2.3f,-0.25f, 3.75f));// leg
        body[5] = new Cube(new V3(0.5f, 0.25f, 5.25f), new V3(0.5f, -0.25f, 5.25f), new V3(0.5f, 0.25f, 5.8f), new V3(0.5f,-0.25f, 5.8f),
                new V3(-0.6f, 0.25f, 3.75f), new V3(-0.6f, -0.25f, 3.75f), new V3(-0.8f, 0.25f, 3.75f), new V3(-0.8f,-0.25f, 3.75f));// leg

    }

    private void buildRope() {
        rope = new Rope[9];
        rope[0] = new Rope(new V3(0.80f, 0.5f, 10.5f), new V3(0.5f, 0.5f, 10.5f), new V3(0.80f, 0.5f, 11.5f), new V3(0.5f, 0.5f, 11.5f));
        rope[1] = new Rope(new V3(0.80f, -0.5f, 10.5f), new V3(0.5f, -0.5f, 10.5f), new V3(0.80f, -0.5f, 11.5f), new V3(0.5f, -0.5f, 11.5f));
        rope[2] = new Rope(new V3(0.80f, -0.5f, 11.5f), new V3(0.5f, -0.5f, 11.5f), new V3(0.80f, 0.5f, 11.5f), new V3(0.5f, 0.5f, 11.5f));

        rope[3] = new Rope(new V3(0.775f, 0.1f, 10), new V3(0.525f, 0.1f, 10), new V3(0.8f, 0.5f, 10.5f), new V3(0.5f, 0.5f, 10.5f));
        rope[4] = new Rope(new V3(0.775f, -0.1f, 10), new V3(0.525f, -0.1f, 10), new V3(0.8f, -0.5f, 10.5f), new V3(0.5f, -0.5f, 10.5f));

        rope[5] = new Rope(new V3(0.775f, 0.1f, 10), new V3(0.525f, 0.1f, 10), new V3(0.775f, 0.1f, 9.25f), new V3(0.525f, 0.1f, 9.25f));
        rope[6] = new Rope(new V3(0.775f, -0.1f, 10), new V3(0.525f, -0.1f, 10), new V3(0.775f, -0.1f, 9.25f), new V3(0.525f, -0.1f, 9.25f));

        rope[7] = new Rope(new V3(0.775f, -0.1f, 10), new V3(0.525f, -0.1f, 10), new V3(0.775f, 0.1f, 9.25f), new V3(0.525f, 0.1f, 9.25f));
        rope[8] = new Rope(new V3(0.775f, 0.1f, 10), new V3(0.525f, 0.1f, 10), new V3(0.775f, -0.1f, 9.25f), new V3(0.525f, -0.1f, 9.25f));
    }

    private void buildOther(){
        p.setStrokeWidth(10);
        p.setColor(Color.RED);
//        final Handler h = new Handler();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true) {
//                    if (acc < -0.01 || acc > 0.01) {
//                        S.rotateLeft(acc * TOUCH_SCALE_FACTOR);
//                        acc *= 0.75;
//                        h.
//                        h.invalidate();
//                    } else acc = 0;
//                }
//            }
//        }).start();

    }

    @Override
    protected void onDraw(final Canvas canvas){
        S.focus(c);
        for(final Cube cu: gallow){
            cu.draw(S, canvas, p); // draw gallow
        }
        p.setStrokeWidth(3f); p.setColor(Color.GRAY);
        for(final Rope r : rope)
            r.draw(S, canvas, p);
        p.setStrokeWidth(7f); p.setColor(Color.RED);
        for(int i = 0; i < errors; i++) {
            body[i].draw(S, canvas, p); // draw inflicted bodyparts
        }
    }

    /**
     * sets errors and redraws
     * @param errors = number of wrong guesses
     */
    public void setErrors(final int errors) {
        this.errors = errors;
        invalidate();
    }
}
