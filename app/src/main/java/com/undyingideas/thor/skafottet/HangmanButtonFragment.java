package com.undyingideas.thor.skafottet;

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
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState)  {
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
        final Button button = (Button )v;
        ((Button) v).setVisibility(View.INVISIBLE);

        listOfButtons.add((Button) v);
        final String letter = ((Button) v).getText().toString();
        guess(letter, true);
    }

    /**
     * TODO : RECODE RECODE RECODE RECODE!
     * well this is annoying
     * @return
     */
    private ArrayList<Button> initButtons(){
        final ArrayList<Button> returnList = new ArrayList<>();

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
        for (final Button btn : returnList){
            btn.setOnClickListener(this);
        }

        return returnList;
    }


}
