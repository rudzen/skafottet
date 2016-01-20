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
import android.preference.PreferenceFragment;
import android.view.KeyEvent;
import android.view.View;

import com.undyingideas.thor.skafottet.R;

/**
 * Created on 13-11-2015, 12:12.
 * Project : skafottet
 * @author rudz
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.skafottet_preferences);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        final View v = getView();
        if (v != null) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new OnBackKeyListener());
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private class OnBackKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                getActivity().finish();
                return true;
            }
            return false;
        }
    }
}
