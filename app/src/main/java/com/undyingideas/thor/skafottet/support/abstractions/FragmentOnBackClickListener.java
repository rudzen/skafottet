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

package com.undyingideas.thor.skafottet.support.abstractions;

import android.view.KeyEvent;
import android.view.View;

import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;

/* Put the following in the fragment onViewCreated..
        final View v = getView();
        if (v != null) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new FragmentOnBackClickListener(iFragmentFlipper, Constant.MODE_MENU));
        }
*/

/**
 * Created on 23-01-2016, 08:28.
 * Project : skafottet
 * To facilitate fragment back button catching :-)
 *
 * @author rudz
 */
class FragmentOnBackClickListener extends WeakReferenceHolder<IFragmentFlipper> implements View.OnKeyListener {

    private final int newMode;

    public FragmentOnBackClickListener(final IFragmentFlipper iFragmentFlipper, final int newMode) {
        super(iFragmentFlipper);
        this.newMode = newMode;
    }

    @Override
    public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            final IFragmentFlipper iFragmentFlipper = weakReference.get();
            if (iFragmentFlipper != null) {
                iFragmentFlipper.flipFragment(newMode);
            }
        }
        return false;
    }
}
