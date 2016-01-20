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

package com.undyingideas.thor.skafottet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.GameActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple help fragment with a webview
 * @author rudz
 */
public class HelpFragment extends Fragment {

    private static final byte[] emptyBytes = new byte[0];

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_help, container, false);
        final WebView wv = (WebView) fragmentView.findViewById(R.id.wv);
//        final HelpFileDTO help = new HelpFileDTO();
//        try {
//            checkForNullKey(Constant.KEY_PREF_HELP);
//            help.addStringData((HelpFileDTO) GameUtility.s_preferences.getObject(Constant.KEY_PREF_HELP, HelpFileDTO.class));
//            WindowLayout.showSnack("Gemt tekst hentet.", wv, true);
//        } catch (final NullPointerException npe) {
        String theText;
            try {
                final InputStream input = getResources().openRawResource(R.raw.skafottet);
                final int size = input.available();
                byte[] buffer = new byte[size];

                //noinspection ResultOfMethodCallIgnored
                input.read(buffer);
                input.close();
//                help.addStringData(new String(buffer, "UTF-8"));
//                buffer = emptyBytes; // clear it right away..
//                GameUtility.s_preferences.putObject(Constant.KEY_PREF_HELP, help);
//                WindowLayout.showSnack("Standard tekst hentet.", wv, true);
                theText = new String(buffer, "UTF-8");
            } catch (final IOException e) {
                e.printStackTrace();
                theText = "Fejl ved indhentning af fil.";
            }
//        }
        wv.loadDataWithBaseURL("file:///android_res/raw/", theText, "text/html", "UTF-8", null);
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(wv);
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        ((GameActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
    }
}
