/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.text.util.Linkify.EMAIL_ADDRESSES;
import static android.text.util.Linkify.WEB_URLS;
import static android.text.util.Linkify.addLinks;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;

/**
 * Simple fragment to display some information about the app
 * @author rudz
 */
public class AboutFragment extends Fragment {

    private static final String SEP = System.lineSeparator();
    private static final String RUDY = "Rudy", ADAM = "Adam", THEIS = "Theis", THOR = "Thor";

    private static final String[] ROWS = {
            "Skafottet - et 3-ugers projekt, af Gruppe 23" + SEP +
                    "Gruppe medlemmer :" + SEP +
                    "s133235 Rudy Alex Kohn, s133235@student.dtu.dk" + SEP +
                    "sXXXXXX Adam Hammer Palmar" + SEP +
                    "sXXXXXX Thor Adrian Dahlstrøm" + SEP +
                    "sXXXXXX Theis Friis Strømming" + SEP +
                    "sXXXXXX Emil Hvis Hansen" + SEP +
            "Projekt ansvarlig : Rudy Alex Kohn" +
                    SEP + SEP +
                    "Design og Grafik : " + RUDY + SEP +
                    "Kode : " + RUDY + ", " + ADAM + ", " + THEIS + SEP +
                    "Firebase Design : " + ADAM + ", " + THEIS + SEP +
                    "Multiplayer Design : " + THEIS + ", " + RUDY + SEP +
                    "Baseret på originalt kode af : " + THOR + SEP +

            "Original galgelogik af Jakob Nordfalk." + SEP + "http://www.javabog.dk",
            "TinyDB preferences interface af kcochibili." + SEP + "https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo",
            "\"THE BEER-WARE LICENSE\" (Revision 42):" + SEP +
                    "  ~ <s133235@student.dtu> wrote this file. As long as you retain this notice you" + SEP +
                    "  ~ can do whatever you want with this stuff. If we meet some day, and you think" + SEP +
                    "  ~ this stuff is worth it, you can buy me a beer in return - Group 23"


    };

    @Nullable
    private TextView[] tv = new TextView[ROWS.length];

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final ScrollView sv = new ScrollView(getContext());
        sv.setSmoothScrollingEnabled(true);

        final LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);

        final int padding = (int) applyDimension(COMPLEX_UNIT_DIP, 4, getActivity().getResources().getDisplayMetrics());
        int i = 0;

        //noinspection ConstantConditions
        for (TextView t : tv) {
            //noinspection ObjectAllocationInLoop
            t = new TextView(getContext());
            t.setPadding(padding, padding, padding, padding);
            t.setText(ROWS[i++]);
            if (i < ROWS.length) t.setTextSize(COMPLEX_UNIT_SP, 20);
            else t.setTextSize(COMPLEX_UNIT_SP, 11);
            addLinks(t, WEB_URLS | EMAIL_ADDRESSES);
            ll.addView(t);
        }
        tv = null;
        sv.addView(ll);
        return sv;
    }

    @Override
    public void onDestroy() {
        tv = null;
        super.onDestroy();
    }
}
