package com.undyingideas.thor.skafottet.game_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import java.util.ArrayList;

public class HangmanButtonFragment extends AbstractPlayFragment implements View.OnClickListener {


    private ArrayList<Button> listOfButtons;
    private View root;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)  {
        onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.hangman_button,container,false);

        galgen = (ImageView) root.findViewById(R.id.imageView);
        galgen.setImageResource(R.drawable.galge);

        CheckGameType();
        ordet = (TextView) root.findViewById(R.id.visibleText);
        ordet.setText(logik.getSynligtOrd());
        listOfButtons = initButtons();
        resetButtons();
        return root;
    }

    private void resetButtons(){
        for(int i=0; i<listOfButtons.size();i++){
            final Button button;
            button = listOfButtons.get(i);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(final View v) {
        Log.d("buttons", "button clicked");
        v.setVisibility(View.INVISIBLE);

        listOfButtons.add((Button) v);
        final String letter = ((Button) v).getText().toString();
        guess(letter, true);
    }

    /**
     * well this is annoying
     * @return The letter buttons as arraylist
     */
    private ArrayList<Button> initButtons(){
        final ArrayList<Button> returnList = new ArrayList<>();

        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout1)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout2)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout3)));
        returnList.addAll(getChildren((ViewGroup) root.findViewById(R.id.linearLayout4)));

        //adding clickhandlers
        for (final Button btn : returnList){
            btn.setOnClickListener(this);
        }

        return returnList;
    }

    private static ArrayList<Button> getChildren(final ViewGroup vg) {
        final ArrayList<Button> a = new ArrayList<>();
        for(int i = 0; i < vg.getChildCount(); i++) a.add((Button)vg.getChildAt(i));
        return a;
    }


}
