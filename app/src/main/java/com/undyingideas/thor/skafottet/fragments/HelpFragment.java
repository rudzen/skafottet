package com.undyingideas.thor.skafottet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.firebase.DTO.HelpFileDTO;
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
