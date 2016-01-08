package com.undyingideas.thor.skafottet.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.undyingideas.thor.skafottet.R;

/**
 * Created on 04-01-2016, 09:50.
 * Project : skafottet
 *
 * @author rudz
 */
public class HangedView extends View {

    private int state;
    private final SparseArray<Bitmap> images = new SparseArray<>();
    private Paint p;

    public HangedView(final Context context) {
        super(context);
    }

    public HangedView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public HangedView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        if (p == null) {
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        images.put(0, BitmapFactory.decodeResource(getResources(), R.drawable.galge));
        images.put(1, BitmapFactory.decodeResource(getResources(), R.drawable.forkert1));
        images.put(2, BitmapFactory.decodeResource(getResources(), R.drawable.forkert2));
        images.put(3, BitmapFactory.decodeResource(getResources(), R.drawable.forkert3));
        images.put(4, BitmapFactory.decodeResource(getResources(), R.drawable.forkert4));
        images.put(5, BitmapFactory.decodeResource(getResources(), R.drawable.forkert5));
        images.put(6, BitmapFactory.decodeResource(getResources(), R.drawable.forkert6));
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(images.get(state), 0, 0, p);
        super.onDraw(canvas);
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
        invalidate();
    }

}
