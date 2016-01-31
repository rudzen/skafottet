package com.undyingideas.thor.skafottet.views.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * <p>Created on 15-01-2016, 08:25.<br>
 * Project : skafottet</p>
 * @author theis
 */
@SuppressWarnings("ClassWithTooManyFields")
public class Hangman3dView extends View {

    private final static String TAG = "hangman3dview";

    private float mPreviousX, mPreviousY;
    private static final float TOUCH_SCALE_FACTOR = 2f / 180;
    private static final int framerate = 25;

    // Simulate time
    double ts;
    double t;     // simulation time in sec.
    // App
    private Camera S = new Camera(150, 150, 500, 600);
    V3 u = new V3(0, 1, 1).unit();
    private static final double phi = Math.PI / 100;
    M3 Rz = M3.inverse.add(M3.sZ.mul((float) Math.sin(phi))).add(M3.sZ.mul(M3.sZ).mul((float) (1 - Math.cos(phi))));

    private final V3 c = new V3(0, 0, 3);
    private Cube[] gallow;
    private Cube[] body;
    private Rope[] rope;
    private int errors;
    private final Paint noosePainter = new Paint();

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
    public boolean onTouchEvent(final MotionEvent e) {
        if (e.getPointerCount() == 1) {
            final float x = e.getX(), y = e.getY();
            switch (e.getAction()) {
                case ACTION_MOVE:
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    if (y > getHeight() >> 1) {
                        dx *= -1;          // reverse direction of rotation above the mid-line
                    }
                    if (x < getWidth() >> 1) {
                        dy *= -1;           // reverse direction of rotation to left of the mid-line
                    }
                    S.rotateLeft(dx * TOUCH_SCALE_FACTOR);
                    invalidate();
                    //noinspection fallthrough
                case ACTION_DOWN:
                    if (e.getAction() != ACTION_MOVE) Log.d(TAG, "touchstarted");
                    mPreviousX = x;
                    mPreviousY = y;
                    break;
                case ACTION_UP:
                    Log.d(TAG, "touchstopped");
                    break;
                default:
                    Log.d(TAG, "unaccounted event " + e.getAction());
            }
        } else multiTouch(e);
        return true;
    }
    int x1, x2, y1, y2;
    private float mPreviousX1;
    private float mPreviousX2;
    private float mPreviousY1;
    private float mPreviousY2;
    private static final float ZOOM_SCALE_FACTOR = 1f / 180;
    private boolean startleft;
    private boolean startdown;
    private void multiTouch(final MotionEvent e) {
        final float x1 = e.getX(0), y1 = e.getY(0), x2 = e.getX(1), y2 = e.getY(1);
        switch (e.getAction()) {
            case ACTION_MOVE:
                final float dx1 = x1 - mPreviousX1;
                final float dx2 = x2 - mPreviousX2;
                final float dy1 = y1 - mPreviousY1;
                final float dy2 = y2 - mPreviousY2;
                float z;
                z = startleft ? S.z + (dx1 - dx2) * ZOOM_SCALE_FACTOR : S.z + (dx2 - dx1) * ZOOM_SCALE_FACTOR;
                z += startdown ? (dy1 - dy2) * ZOOM_SCALE_FACTOR : (dy2 - dy1) * ZOOM_SCALE_FACTOR;
                if (z > 1 && z < 20) S.setZoom(z); else if (z > 20) S.setZoom(20); else S.setZoom(1);
                invalidate();
                //noinspection fallthrough
            case ACTION_DOWN:
                if (e.getAction() == ACTION_DOWN) Log.d("Hangman", "action down");
                //noinspection fallthrough
            case 5:
                if (e.getAction() == 5) Log.d("Hangman", "5");
                //noinspection fallthrough
            case 261:
                if (e.getAction() == 261) {
                    Log.d(TAG, "touchstarted261");
                    startleft = x1 > x2;
                    startdown = y1 > y2;
                }
                mPreviousX1 = x1;
                mPreviousX2 = x2;
                mPreviousY1 = y1;
                mPreviousY2 = y2;

                break;
            case ACTION_UP:
                Log.d(TAG, "touchstopped multi");
                break;
            case 262:
                Log.d("hangman", "262");
                mPreviousX = e.getX(0);
                mPreviousY = e.getY(0);
                break;
            case 6:
                Log.d("hangman", "6");
                mPreviousX = e.getX(1);
                mPreviousY = e.getY(1);
                break;
            default:
                Log.d(TAG, "unaccounted event " + e.getAction());
        }
    }

    private void buildGallow() {
        gallow = new Cube[8];
        gallow[0] = new Cube(new V3(-3f, -3f, 0.5f), 1f, 1f, 1f); // forhøjning
        gallow[1] = new Cube(new V3(-3, 3, 0.5f), 1, 1, 1);
        gallow[2] = new Cube(new V3(3, -3, 0.5f), 1, 1, 1);
        gallow[3] = new Cube(new V3(3, 3, 0.5f), 1, 1, 1);

        gallow[4] = new Cube(new V3(0, 0, 1.25f), 7, 0.5f, 7); // platform

        gallow[5] = new Cube(new V3(-3, 0, 6.5f), 1, 10, 1); // stolpe
        gallow[6] = new Cube(new V3(-0.5f, 0, 11), 1, 1, 4f); // bar
        final float f1 = 0.2f;
        final float f2 = -0.2f;
        final float v1 = -2.5f;
        final float low = 10.5f;
        final float v2 = -2.2f;
        gallow[7] = new Cube(new V3(v1, f1, 10f), new V3(v1, f2, 10f), new V3(v1, f1, 10.125f), new V3(v1, f2, 10.125f),
                new V3(v2, f1, low), new V3(v2, f2, low), new V3(-2.3f, f1, low), new V3(-2.3f, f2, low));
        //gallow[7] = new Cube(new V3(0.75f,0,9.75f), 0.25f,1.5f, 0.25f); // snor
    }

    private void buildBody() {
        body = new Cube[6];
        body[0] = new Cube(new V3(0.75f, 0, 8.75f), 1, 1, 1); // head
        body[1] = new Cube(new V3(0.75f, 0, 6.75f), 0.5f, 3, 0.5f); // body
        body[2] = new Cube(new V3(1, 0.25f, 7.65f), new V3(1, -0.25f, 7.65f), new V3(1, 0.25f, 8f), new V3(1, -0.25f, 8f),
                new V3(2, 0.25f, 6.9f), new V3(2, -0.25f, 6.9f), new V3(2, 0.25f, 7.25f), new V3(2, -0.25f, 7.25f));
        body[3] = new Cube(new V3(0.5f, 0.25f, 7.65f), new V3(0.5f, -0.25f, 7.65f), new V3(0.5f, 0.25f, 8f), new V3(0.5f, -0.25f, 8f),
                new V3(-0.5f, 0.25f, 6.9f), new V3(-0.5f, -0.25f, 6.9f), new V3(-0.5f, 0.25f, 7.25f), new V3(-0.5f, -0.25f, 7.25f));

        body[4] = new Cube(new V3(1, 0.25f, 5.25f), new V3(1, -0.25f, 5.25f), new V3(1, 0.25f, 5.8f), new V3(1, -0.25f, 5.8f),
                new V3(2, 0.25f, 3.75f), new V3(2, -0.25f, 3.75f), new V3(2.3f, 0.25f, 3.75f), new V3(2.3f, -0.25f, 3.75f));// leg
        body[5] = new Cube(new V3(0.5f, 0.25f, 5.25f), new V3(0.5f, -0.25f, 5.25f), new V3(0.5f, 0.25f, 5.8f), new V3(0.5f, -0.25f, 5.8f),
                new V3(-0.6f, 0.25f, 3.75f), new V3(-0.6f, -0.25f, 3.75f), new V3(-0.8f, 0.25f, 3.75f), new V3(-0.8f, -0.25f, 3.75f));// leg

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

    private void buildOther() {
        noosePainter.setStrokeWidth(10);
        noosePainter.setColor(GameUtility.getSettings().prefsColour);
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

    private boolean firstrun = true;

    @Override
    protected void onDraw(final Canvas canvas) {
        if (firstrun) {
            S = new Camera(150, 150, getWidth() >> 1, (getHeight() >> 1) + (getHeight() >> 3));
            firstrun = false;
        }
        S.focus(c);
        for (final Cube cu : gallow) {
            cu.draw(S, canvas, noosePainter); // draw gallow
        }
        noosePainter.setStrokeWidth(3f);
        noosePainter.setColor(GameUtility.getSettings().textColour == Color.BLACK ? Color.GRAY : GameUtility.getSettings().textColour);
        for (final Rope r : rope)
            r.draw(S, canvas, noosePainter);
        noosePainter.setStrokeWidth(7f);
        noosePainter.setColor(GameUtility.getSettings().prefsColour);
        for (int i = 0; i < errors; i++) {
            body[i].draw(S, canvas, noosePainter); // draw inflicted bodyparts
        }
    }

    /**
     * sets errors and redraws
     *
     * @param errors
     *         = number of wrong guesses
     */
    public void setErrors(final int errors) {
        this.errors = errors;
        invalidate();
    }
}
