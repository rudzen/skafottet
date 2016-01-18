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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.dto.HelpFileDTO;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.io.IOException;
import java.io.InputStream;

import static com.undyingideas.thor.skafottet.support.utility.TinyDB.checkForNullKey;

/**
 * Simple help fragment with a webview
 * @author rudz
 */
public class HelpFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_help, container, false);
        final WebView wv = (WebView) fragmentView.findViewById(R.id.wv);
        final HelpFileDTO help = new HelpFileDTO();
        try {
            checkForNullKey(Constant.KEY_PREF_HELP);
            help.addStringData((HelpFileDTO) GameUtility.s_preferences.getObject(Constant.KEY_PREF_HELP, HelpFileDTO.class));
        } catch (final NullPointerException npe) {
            try {
                final InputStream input = getResources().openRawResource(R.raw.skafottet);
                final int size = input.available();
                final byte[] buffer = new byte[size];

                //noinspection ResultOfMethodCallIgnored
                input.read(buffer);
                input.close();

                help.addStringData(new String(buffer));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        wv.loadDataWithBaseURL("file:///android_res/raw/", help.getStringData(), "text/html", "UTF-8", null);
        return fragmentView;
    }


}
