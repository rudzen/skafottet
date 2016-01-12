package com.undyingideas.thor.skafottet.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created on 12-01-2016, 07:25.
 * Project : skafottet
 * Special view for having underlined TextView.
 * @author rudz
 */
public class UnderlinedTextView extends TextView {
    private final Paint mPaint = new Paint(DRAWING_CACHE_QUALITY_LOW);
    private int mUnderlineHeight;
    private static final int DEFATULT_DPI = 2;

    public UnderlinedTextView(final Context context) {
        this(context, null);
    }

    public UnderlinedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnderlinedTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        mUnderlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFATULT_DPI, context.getResources().getDisplayMetrics());
    }

    @Override
    public void setPadding(final int left, final int top, final int right, final int bottom) {
        super.setPadding(left, top, right, bottom + mUnderlineHeight);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getTextColors().getDefaultColor());
        canvas.drawRect(0, getHeight() - mUnderlineHeight, getWidth(), getHeight(), mPaint);
    }
}
