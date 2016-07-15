/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.support.preferences.colourpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A preference type that allows a user to choose a time
 *
 * @author Sergey Margaritov
 */
public class ColorPickerPreference
        extends
        Preference
        implements
        Preference.OnPreferenceClickListener,
        ColorPickerDialog.OnColorChangedListener {

    private View mView;
    private ColorPickerDialog mDialog;
    private int mValue = Color.BLACK;
    private float mDensity;
    private boolean mAlphaSliderEnabled;
    private boolean mHexValueEnabled;

    public ColorPickerPreference(final Context context) {
        super(context);
        init(context, null);
    }

    public ColorPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Method edited by
     *
     * @author Anna Berkovitch
     * added functionality to accept hex string as defaultValue
     * and to properly persist resources reference string, such as @color/someColor
     * previously persisted 0
     */
    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        final int colorInt;
        final String mHexDefaultValue = a.getString(index);
        if (mHexDefaultValue != null && !mHexDefaultValue.isEmpty() && mHexDefaultValue.charAt(0) == '#') {
            colorInt = convertToColorInt(mHexDefaultValue);
            return colorInt;

        } else {
            return a.getColor(index, Color.BLACK);
        }
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        onColorChanged(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
    }

    private void init(final Context context, final AttributeSet attrs) {
        mDensity = getContext().getResources().getDisplayMetrics().density;
        setOnPreferenceClickListener(this);
        if (attrs != null) {
            mAlphaSliderEnabled = attrs.getAttributeBooleanValue(null, "alphaSlider", false);
            mHexValueEnabled = attrs.getAttributeBooleanValue(null, "hexValue", false);
        }
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);
        mView = view;
        setPreviewColor();
    }

    private void setPreviewColor() {
        if (mView == null) return;
        final ImageView iView = new ImageView(getContext());
        final LinearLayout widgetFrameView = (LinearLayout) mView.findViewById(android.R.id.widget_frame);
        if (widgetFrameView == null) return;
        widgetFrameView.setVisibility(View.VISIBLE);
        widgetFrameView.setPadding(widgetFrameView.getPaddingLeft(), widgetFrameView.getPaddingTop(), (int) (mDensity * 8), widgetFrameView.getPaddingBottom());
        // remove already create preview image
        final int count = widgetFrameView.getChildCount();
        if (count > 0) {
            widgetFrameView.removeViews(0, count);
        }
        widgetFrameView.addView(iView);
        widgetFrameView.setMinimumWidth(0);
        iView.setBackground(new AlphaPatternDrawable((int) (5 * mDensity)));
        iView.setImageBitmap(getPreviewBitmap());
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private Bitmap getPreviewBitmap() {
        final int imageDensity = (int) (mDensity * 31); //30dip
        final int color = mValue;
        final Bitmap bm = Bitmap.createBitmap(imageDensity, imageDensity, Config.ARGB_8888);
        final int w = bm.getWidth();
        final int h = bm.getHeight();
        int c;
        for (int i = 0; i < w; i++) {
            for (int j = i; j < h; j++) {
                c = i <= 1 || j <= 1 || i >= w - 2 || j >= h - 2 ? Color.GRAY : color;
                bm.setPixel(i, j, c);
                if (i != j) {
                    bm.setPixel(j, i, c);
                }
            }
        }

        return bm;
    }

    @Override
    public void onColorChanged(final int color) {
        if (isPersistent()) {
            persistInt(color);
        }
        mValue = color;
        setPreviewColor();
        try {
            getOnPreferenceChangeListener().onPreferenceChange(this, color);
        } catch (final NullPointerException e) {
            // nothing here
        }
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        showDialog(null);
        return false;
    }

    private void showDialog(final Bundle state) {
        mDialog = new ColorPickerDialog(getContext(), mValue);
        mDialog.setOnColorChangedListener(this);
        if (mAlphaSliderEnabled) {
            mDialog.setAlphaSliderVisible(true);
        }
        if (mHexValueEnabled) {
            mDialog.setHexValueEnabled(true);
        }
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mDialog.show();
    }

    /**
     * Toggle Alpha Slider visibility (by default it's disabled)
     *
     * @param enable
     */
    public void setAlphaSliderEnabled(final boolean enable) {
        mAlphaSliderEnabled = enable;
    }

    /**
     * Toggle Hex Value visibility (by default it's disabled)
     *
     * @param enable
     */
    public void setHexValueEnabled(final boolean enable) {
        mHexValueEnabled = enable;
    }

    /**
     * For custom purposes. Not used by ColorPickerPreferrence
     *
     * @param color
     * @author Unknown
     */
    public static String convertToARGB(final int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = '0' + alpha;
        }

        if (red.length() == 1) {
            red = '0' + red;
        }

        if (green.length() == 1) {
            green = '0' + green;
        }

        if (blue.length() == 1) {
            blue = '0' + blue;
        }

        return '#' + alpha + red + green + blue;
    }

    /**
     * Method currently used by onGetDefaultValue method to
     * convert hex string provided in android:defaultValue to color integer.
     *
     * @param color
     * @return A string representing the hex value of color,
     * without the alpha value
     * @author Charles Rosaaen
     */
    public static String convertToRGB(final int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (red.length() == 1) {
            red = '0' + red;
        }

        if (green.length() == 1) {
            green = '0' + green;
        }

        if (blue.length() == 1) {
            blue = '0' + blue;
        }

        return '#' + red + green + blue;
    }

    /**
     * For custom purposes. Not used by ColorPickerPreferrence
     *
     * @param argb
     * @throws NumberFormatException
     * @author Unknown
     */
    public static int convertToColorInt(final String argb) throws IllegalArgumentException {
        return !(!argb.isEmpty() && argb.charAt(0) == '#') ? Color.parseColor('#' + argb) : Color.parseColor(argb);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof SavedState)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        showDialog(myState.dialogBundle);
    }

    private static class SavedState extends BaseSavedState {
        Bundle dialogBundle;

        public SavedState(final Parcel source) {
            super(source);
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(final Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new SavedStateCreator();

        private static class SavedStateCreator implements Creator<SavedState> {
            @Override
            public SavedState createFromParcel(final Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        }
    }
}