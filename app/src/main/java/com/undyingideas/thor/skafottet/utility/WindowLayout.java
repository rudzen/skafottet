/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.utility;

import android.app.ActionBar;
import android.graphics.Point;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created on 22-12-2015, 11:46.
 * Project : R-TicTacToe
 *
 * @author rudz
 */
public abstract class WindowLayout {

    private static final Point screenDimension = new Point();

    public static void hideStatusBar(final Window w, final ActionBar ab) {
        if (Build.VERSION.SDK_INT >= 16) { //ye olde method
            final View decorView = w.getDecorView();

            /* Hide status bar. */
            final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            /* general practise is to also hide the ActionBar if hiding StatusBar */
            if (ab != null) ab.hide();
        } else w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void showSnack(final CharSequence text, final View v, final boolean brief) {
        Snackbar.make(v, text, brief ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public static void setScreenDimension(final AppCompatActivity appCompatActivity) {
        final Display display = appCompatActivity.getWindowManager().getDefaultDisplay();
        display.getSize(screenDimension);
    }

    public static void setImmersiveMode(final Window w) {
        w.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
