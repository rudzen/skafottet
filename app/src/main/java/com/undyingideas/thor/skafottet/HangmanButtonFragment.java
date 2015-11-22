package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HangmanButtonFragment extends AbstractPlayFragment implements View.OnClickListener {


    private ArrayList<Button> listOfButtons;
    private View root;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        root = i.inflate(R.layout.hangman_button,container,false);

        galgen = (ImageView) root.findViewById(R.id.imageView);
        galgen.setImageResource(R.mipmap.galge);

        CheckGameType();
        ordet = (TextView) root.findViewById(R.id.visibleText);
        ordet.setText(logik.getSynligtOrd());
        listOfButtons = initButtons();
        resetButtons();
        return root;
    }

//    public void buttonOnClick(View v){
//
//    }
    public void resetButtons(){
        for(int i=0; i<listOfButtons.size();i++){
            Button button;
            button = listOfButtons.get(i);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("buttons", "button clicked");
        Button button = (Button )v;
        ((Button) v).setVisibility(View.INVISIBLE);

        listOfButtons.add((Button) v);
        String letter = ((Button) v).getText().toString();
        guess(letter, true);
    }

    /**
     * well this is annoying
     * @return
     */
    public ArrayList<Button> initButtons(){
        ArrayList<Button> returnList = new ArrayList<>();
        returnList.add((Button) root.findViewById(R.id.button1));
        returnList.add((Button) root.findViewById(R.id.button2));
        returnList.add((Button) root.findViewById(R.id.button3));
        returnList.add((Button) root.findViewById(R.id.button4));
        returnList.add((Button) root.findViewById(R.id.button5));
        returnList.add((Button) root.findViewById(R.id.button6));
        returnList.add((Button) root.findViewById(R.id.button7));
        returnList.add((Button) root.findViewById(R.id.button8));
        returnList.add((Button) root.findViewById(R.id.button9));
        returnList.add((Button) root.findViewById(R.id.button10));
        returnList.add((Button) root.findViewById(R.id.button11));
        returnList.add((Button) root.findViewById(R.id.button12));
        returnList.add((Button) root.findViewById(R.id.button13));
        returnList.add((Button) root.findViewById(R.id.button14));
        returnList.add((Button) root.findViewById(R.id.button15));
        returnList.add((Button) root.findViewById(R.id.button16));
        returnList.add((Button) root.findViewById(R.id.button17));
        returnList.add((Button) root.findViewById(R.id.button18));
        returnList.add((Button) root.findViewById(R.id.button19));
        returnList.add((Button) root.findViewById(R.id.button20));
        returnList.add((Button) root.findViewById(R.id.button21));
        returnList.add((Button) root.findViewById(R.id.button22));
        returnList.add((Button) root.findViewById(R.id.button23));
        returnList.add((Button) root.findViewById(R.id.button24));
        returnList.add((Button) root.findViewById(R.id.button25));
        returnList.add((Button) root.findViewById(R.id.button26));
        returnList.add((Button) root.findViewById(R.id.button27));
        returnList.add((Button) root.findViewById(R.id.button28));

        //adding clickhandlers
        for (Button btn : returnList){
            btn.setOnClickListener(this);
        }

        return returnList;
    }


}
