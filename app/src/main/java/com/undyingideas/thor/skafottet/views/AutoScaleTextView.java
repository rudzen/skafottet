package com.undyingideas.thor.skafottet.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

/**
 * A custom Text View that lowers the text size when the text is to big for the TextView. Modified version of one found on stackoverflow
 *
 * @author Andreas Krings - www.ankri.de
 *
 * @author rudz
 * @version 1.1
 *          Cleanup of the mess.
 */
public class AutoScaleTextView extends TextView {
    private final Paint mTextPaint;

    private float mPreferredTextSize;
    private float mMinTextSize;

    public AutoScaleTextView(final Context context) {
        this(context, null);
    }

    public AutoScaleTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.autoScaleTextViewStyle);
        // Use this constructor, if you do not want use the default style
        // super(context, attrs);
    }

    public AutoScaleTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mTextPaint = new Paint();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView, defStyle, 0);
        mMinTextSize = a.getDimension(R.styleable.AutoScaleTextView_minTextSize, 10f);
        a.recycle();

        mPreferredTextSize = getTextSize();
    }

    /**
     * Set the minimum text size for this view
     *
     * @param minTextSize
     *         The minimum text size
     */
    public void setMinTextSize(final float minTextSize) {
        this.mMinTextSize = minTextSize;
    }

    /**
     * Resize the text so that it fits
     *
     * @param text
     *         The text. Neither {@code null} nor empty.
     * @param textWidth
     *         The width of the TextView. > 0
     */
    private void refitText(final String text, final int textWidth) {
        if (textWidth <= 0 || text == null || text.isEmpty())
            return;

        // the width
        final int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();

        final float threshold = 0.5f; // How close we have to be

        mTextPaint.set(getPaint());

        while (mPreferredTextSize - mMinTextSize > threshold) {
            final float size = (mPreferredTextSize + mMinTextSize) / 2;
            mTextPaint.setTextSize(size);
            if (mTextPaint.measureText(text) >= targetWidth)
                mPreferredTextSize = size; // too big
            else
                mMinTextSize = size; // too small
        }
        // Use min size so that we undershoot rather than overshoot
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mMinTextSize);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), getWidth());
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        if (width != oldwidth)
            refitText(getText().toString(), width);
    }

}