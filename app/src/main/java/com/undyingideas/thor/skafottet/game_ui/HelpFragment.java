package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.DTO.HelpDTO;
import com.undyingideas.thor.skafottet.utility.Constant;
import com.undyingideas.thor.skafottet.utility.GameUtility;

import java.io.IOException;
import java.io.InputStream;

import static com.undyingideas.thor.skafottet.utility.TinyDB.checkForNullKey;

/**
 * Simple help fragment with a webview
 * @author rudz
 */
public class HelpFragment extends Fragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_help, container, false);
        final WebView wv = (WebView) fragmentView.findViewById(R.id.wv);
        final HelpDTO help = new HelpDTO();
        try {
            checkForNullKey(Constant.KEY_PREF_HELP);
            help.addStringData((HelpDTO) GameUtility.s_prefereces.getObject(Constant.KEY_PREF_HELP, HelpDTO.class));
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
