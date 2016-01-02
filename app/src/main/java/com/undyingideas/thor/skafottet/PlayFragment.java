package com.undyingideas.thor.skafottet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 * @author Thor
 */
public class PlayFragment extends AbstractPlayFragment {




    @Override
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View root = i.inflate(R.layout.activity_play, container, false);


        //instantierer widgets
        ok = (Button) root.findViewById(R.id.InputBtn);
        ok.setOnClickListener(new OkClick());
        usedLetters = (TextView) root.findViewById(R.id.usedLetters);
        ordet = (TextView) root.findViewById(R.id.synligtOrd);
        input = (EditText) root.findViewById(R.id.gaet);
        galgen = (ImageView) root.findViewById(R.id.galgen);
        possibleWords = new ArrayList<>();
        CheckGameType();

        ordet.setText(logik.getSynligtOrd());
        status = (TextView) root.findViewById(R.id.statusText);
        return root;
    }

    private class OkClick implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
                logik.logStatus();
                guess(input.getText().toString());

        }
    }
}



