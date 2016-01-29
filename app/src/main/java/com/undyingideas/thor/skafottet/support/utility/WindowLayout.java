/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.support.utility;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.steamcrafted.loadtoast.LoadToast;

/**
 * Created on 22-12-2015, 11:46.
 * Project : Skafottet
 * Mixed window related utility functionality.
 * @author rudz
 */
public abstract class WindowLayout {

    public static final Point screenDimension = new Point();

    private static LoadToast loadToast;

    private static MaterialDialog md;

    private static Snackbar snackbar;

    /**
     * Hides the statusbar of the window
     * @param w The window to hide the statusbar in
     * @param ab The actionbar (can be null if no actionbar)
     */
    public static void hideStatusBar(final Window w, @Nullable final ActionBar ab) {
        if (Build.VERSION.SDK_INT >= 16) { //ye olde method
            final View decorView = w.getDecorView();

            /* Hide status bar. */
            final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            /* general practise is to also hide the ActionBar if hiding StatusBar */
            if (ab != null) ab.hide();
        } else w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Shows a snackbar with custom colours and clickhandler set.
     * @param text The main message of the snackbar
     * @param buttonText The text of the close button
     * @param v The root view
     * @param brief if true, lenght is short, otherwise lenght is long.
     */
    private static void showSnack(final CharSequence text, final CharSequence buttonText, final View v, final boolean brief) {
        // convoluted, but neccesary.
        snackbar = Snackbar.make(v, text,  brief ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG);
        snackbar.setAction(buttonText, new OnSnackBarClick());
        final View snackView = snackbar.getView();
        snackView.setBackgroundColor(Color.BLACK);
        final TextView snackText = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        snackText.setMaxLines(4);
        snackbar.show();
    }

    public static void showSnack(final CharSequence text, final View v, final boolean brief) {
        showSnack(text, "Luk", v, brief);
    }

    public static LoadToast getLoadToast() {
        return loadToast;
    }

    public static void setLoadToast(final LoadToast loadToast) {
        WindowLayout.loadToast = loadToast;
    }

    /**
     * Returns the current material dialog.
     * @return The dialog to return, this should be configured through the setMd,
     *         So only necessary to call .show() on it.
     */
    public static MaterialDialog getMd() {
        return md;
    }

    /**
     * Creates a new immersive mode material dialog from parsed in builder.
     * @param md The builder from which to create the dialog from.
     */
    public static void setMd(final MaterialDialog.Builder md) {
        WindowLayout.md = md.build();
        WindowLayout.md.setCanceledOnTouchOutside(true);
        WindowLayout.md.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowLayout.md.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        WindowLayout.md.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setImmersiveMode(WindowLayout.md.getWindow());
    }


    private static class OnSnackBarClick implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            snackbar.dismiss();
        }
    }

    public static void showDialog(final Context context, final CharSequence title, final CharSequence content) {
        setMd(new MaterialDialog.Builder(context)
                .backgroundColor(Color.BLACK)
                .title(title)
                .content(content)
                .positiveText("Ok"));
        md.show();
    }

    public static void setImmersiveMode(final Window w) {
        w.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
