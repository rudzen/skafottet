package com.undyingideas.thor.skafottet.support.preferences.colourpicker;

/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import java.util.Locale;

public class ColorPickerDialog
        extends
        Dialog
        implements
        ColorPickerView.OnColorChangedListener,
        View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private ColorPickerView mColorPicker;

    private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;

    private EditText mHexVal;
    private boolean mHexValueEnabled;
    private ColorStateList mHexDefaultTextColor;

    private OnColorChangedListener mListener;
    private int mOrientation;
    private View mLayout;

    @Override
    public void onGlobalLayout() {
        if (getContext().getResources().getConfiguration().orientation != mOrientation) {
            final int oldcolor = mOldColor.getColor();
            final int newcolor = mNewColor.getColor();
            mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            setUp(oldcolor);
            mNewColor.setColor(newcolor);
            mColorPicker.setColor(newcolor);
        }
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public ColorPickerDialog(final Context context, final int initialColor) {
        super(context);

        init(initialColor);
    }

    private void init(final int color) {
        // To fight color banding.
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setUp(color);
    }

    private void setUp(final int color) {

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mLayout = inflater.inflate(R.layout.dialog_color_picker, null);
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mOrientation = getContext().getResources().getConfiguration().orientation;
        setContentView(mLayout);

        setTitle(R.string.dialog_color_picker);

        mColorPicker = (ColorPickerView) mLayout.findViewById(R.id.color_picker_view);
        mOldColor = (ColorPickerPanelView) mLayout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPickerPanelView) mLayout.findViewById(R.id.new_color_panel);

        mHexVal = (EditText) mLayout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mHexVal.getTextColors();

        mHexVal.setOnEditorActionListener(new OnColorPickerEditorActionListener());

        ((LinearLayout) mOldColor.getParent()).setPadding(
                Math.round(mColorPicker.getDrawingOffset()),
                0,
                Math.round(mColorPicker.getDrawingOffset()),
                0
        );

        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);

    }

    @Override
    public void onColorChanged(final int color) {
        mNewColor.setColor(color);

        if (mHexValueEnabled)
            updateHexValue(color);

		/*
        if (mListener != null) {
			mListener.onColorChanged(color);
		}
		*/
    }

    public void setHexValueEnabled(final boolean enable) {
        mHexValueEnabled = enable;
        if (enable) {
            mHexVal.setVisibility(View.VISIBLE);
            updateHexLengthFilter();
            updateHexValue(getColor());
        } else
            mHexVal.setVisibility(View.GONE);
    }

    public boolean getHexValueEnabled() {
        return mHexValueEnabled;
    }

    private void updateHexLengthFilter() {
        if (getAlphaSliderVisible())
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        else
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateHexValue(final int color) {
        if (getAlphaSliderVisible()) {
            mHexVal.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()));
        } else {
            mHexVal.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        }
        mHexVal.setTextColor(mHexDefaultTextColor);
    }

    public void setAlphaSliderVisible(final boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
        if (mHexValueEnabled) {
            updateHexLengthFilter();
            updateHexValue(getColor());
        }
    }

    private boolean getAlphaSliderVisible() {
        return mColorPicker.getAlphaSliderVisible();
    }

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener
     */
    public void setOnColorChangedListener(final OnColorChangedListener listener) {
        mListener = listener;
    }

    private int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.new_color_panel) {
            if (mListener != null) {
                mListener.onColorChanged(mNewColor.getColor());
            }
        }
        dismiss();
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt("oldc", mOldColor.getColor());
        state.putInt("newc", mNewColor.getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setColor(savedInstanceState.getInt("oldc"));
        mColorPicker.setColor(savedInstanceState.getInt("newc"), true);
    }

    private class OnColorPickerEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                final String s = mHexVal.getText().toString();
                if (s.length() > 5 || s.length() < 10) {
                    try {
                        mColorPicker.setColor(ColorPickerPreference.convertToColorInt(s), true);
                        mHexVal.setTextColor(mHexDefaultTextColor);
                    } catch (final IllegalArgumentException e) {
                        mHexVal.setTextColor(Color.RED);
                    }
                } else {
                    mHexVal.setTextColor(Color.RED);
                }
                return true;
            }
            return false;
        }
    }
}
