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
        final Context context = weakReference.get();
        if (context != null) {
            WindowLayout.setLoadToast(new LoadToast(context));
            WindowLayout.getLoadToast().setProgressColor(Color.BLACK);
            WindowLayout.getLoadToast().setTextColor(Color.WHITE);
            WindowLayout.getLoadToast().setBackgroundColor(Color.RED);
            WindowLayout.getLoadToast().setTranslationY(WindowLayout.screenDimension.y / 3);
        }
    }
}
