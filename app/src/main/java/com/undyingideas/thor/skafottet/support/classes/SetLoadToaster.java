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

package com.undyingideas.thor.skafottet.support.classes;

import android.content.Context;
import android.graphics.Color;

import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import net.steamcrafted.loadtoast.LoadToast;

/**
 <p>
 Project : skafottet<br>
 Created by : rudz<br>
 On : jan.26.2016 - 12:42
 </p>
 */
public class SetLoadToaster extends WeakReferenceHolder<Context> implements Runnable {

    public SetLoadToaster(final Context objectReference) {
        super(objectReference);
    }

    @Override
    public void run() {
        final Context context = mWeakReference.get();
        if (context != null) {
            WindowLayout.setLoadToast(new LoadToast(context));
            WindowLayout.getLoadToast().setProgressColor(Color.BLACK);
            WindowLayout.getLoadToast().setTextColor(Color.WHITE);
            WindowLayout.getLoadToast().setBackgroundColor(Color.RED);
            WindowLayout.getLoadToast().setTranslationY(WindowLayout.screenDimension.y / 3);
        }
    }
}
