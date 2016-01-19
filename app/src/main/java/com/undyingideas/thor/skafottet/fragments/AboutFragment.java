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

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
    private static final String RUDY = "Rudy", ADAM = "Adam", THEIS = "Theis";

    private static final String[] ROWS = {
            "Skafottet - et 3-ugers projekt, af Gruppe 23" + SEP +
                    "Gruppe medlemmer der har tilføjet til projektet :" + SEP +
                    "s133235 Rudy Alex Kohn, s133235@student.dtu.dk" + SEP +
                    "s130063 Theis Friis Strømming, s130063@student.dtu.dk" + SEP +
                    "s144868 Adam Hammer Palmar, s144868@student.dtu.dk" + SEP +
                    "Projekt ansvarlig : Rudy Alex Kohn" +
                    SEP + SEP +
                    "Overordnet Design / Grafik / Kode : " + RUDY + SEP +
                    "Menu Design / Kode : " + RUDY + SEP +
                    "Galge 3D Kode : " + THEIS + SEP +
                    "Firebase Design / Kode : " + THEIS + SEP +
                    "Yderligere Firebase Design / Kode : " + ADAM + SEP +
                    "Yderligere Firebase Kode : " + RUDY + SEP + SEP + SEP +
                    "Baseret på originalt skafott design af Thor" + SEP + SEP + SEP + SEP +
                    "Dette projekt indeholder følgende : " + SEP +
                    "- Delvist fragment baseret design med parcel dataoverførsel." + SEP +
                    "- Ny Galgelogik" + SEP +
                    "- Savegame" + SEP +
                    "- Grafik (Herunder sensorstyret flertrådet stjernebaggrund samt 3D galge med touchevents)" + SEP +
                    "- Animationer (egne og 3. parts)" + SEP +
                    "- Vibrator" + SEP +
                    "- Sensor (Tyngdekraft -> Accelerometer) (Benyttet til menu stjernebaggrund)." + SEP +
                    "- Specialt lavet TextView med understreget tekst til ordlisterne," + SEP +
                    "- AsyncTasks" + SEP +
                    "- Specielt designede Broadcast Recievers struktur med overtagelse af Runnable Interface (Internet adgang, batteri status)" + SEP +
                    "- Tråde (Via custom Observere til broadcast recievers, og andre steder dømt brugbart)" + SEP +
                    "- Parcel data objekt overførsel." + SEP +
                    "- Serialiseret filgemning via GZIPstream (Ordlister)." + SEP +
                    "- Ordliste tilføjelse (fra internet addresser)." + SEP +
                    "- Korrekt Ord-filtrering." + SEP +
                    "- Ordliste opdatering." + SEP +
                    "- Ordliste sletning." + SEP +
                    "- Firebase forbindelse." + SEP +
                    "- Firebase opdateringer af informationer." + SEP +
                    "- Firebase Multiplayer funktionalitet (inkl. simpel login)." + SEP +
                    "- Firebase highscore." + SEP +
                    "- Hjælpe funktion via asset html." + SEP +
                    "- Programatisk opsætning af layout." + SEP +
                    "- Baggrundsmusik via service." + SEP +
                    "- Lydeffekter via trådet SoundPool afspilning." + SEP +
                    "- Adaptere (Array, RecycleView, BaseAdapter) inkl. Viewholders." + SEP + SEP + SEP +

                    "Følgende software er blevet brugt under udviklingen :" + SEP +
                    "- Windows 10 Professional" + SEP +
                    "- Android Studio v1.5.1" + SEP +
                    "- Genymotion v2.6" + SEP +
                    "- Adobe Photoshop CS5" + SEP +
                    "- Audacity v2.1.1" + SEP +
                    "- lamedropXPd v3.1" + SEP +
                    "- Kompozer v0.8b3" + SEP + SEP + SEP +

                    "Følgende er brugt som kilder :" + SEP +
                    "- Musik af : http://www.purple-planet.com/horror/4583971268" + SEP +
//                    "- Musik af Kevin Macloud (https://incompetech.com/)." + SEP +
                    "- Diverse lydstumper fra forskellige sider med gratis lydklip." + SEP + SEP + SEP +


            "Original galgelogik af Jakob Nordfalk." + SEP + "http://www.javabog.dk",
            "TinyDB preferences interface af kcochibili." + SEP + "https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo" + SEP + SEP
            + SEP + SEP + "Ingen dyr led nød under udviklingen af denne app, ej heller under brugen af samme. Vi forventer en invasion af flok tapirer i nær fremtid, så det skal slås fast vi er dyrevenner!"

            ,
            "Copyright 2016 Rudy Alex Kohn" + SEP + SEP +
            "Licensed under the Apache License, Version 2.0 (the \"License\")" + SEP +
            "you may not use this file except in compliance with the License." + SEP +
            "You may obtain a copy of the License at" + SEP + SEP +
            "http://www.apache.org/licenses/LICENSE-2.0" + SEP + SEP +
            "Unless required by applicable law or agreed to in writing, software" + SEP +
            "distributed under the License is distributed on an \"AS IS\" BASIS," + SEP +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + SEP +
            "See the License for the specific language governing permissions and" + SEP +
            "limitations under the License."
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

        /* configure toolbar home button */


        final int padding = (int) applyDimension(COMPLEX_UNIT_DIP, 4, getActivity().getResources().getDisplayMetrics());
        int i = 0;

        //noinspection ConstantConditions
        for (TextView t : tv) {
            //noinspection ObjectAllocationInLoop
            t = new TextView(getContext());
            t.setPadding(padding, padding, padding, padding);
            t.setText(ROWS[i++]);
            if (i < ROWS.length) t.setTextSize(COMPLEX_UNIT_SP, 15);
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onDestroy();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
