package com.undyingideas.thor.skafottet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Gump on 04-01-2016.
 */
public class HighScoreFragment extends Fragment{

    private ListView highScoreList;
    private TextView title;

    //FIREBASE
    Firebase myFirebaseRef;
    HighScoreController pc;

    @Override
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rot = i.inflate(R.layout.activity_high_score, container, false);

        //getActivity might not work!
        Firebase.setAndroidContext(getActivity());
        myFirebaseRef = new Firebase("https://hangmandtu.firebaseio.com/highscorelist");
        //limit = 10
       // pc = new HighScoreController(myFirebaseRef,10);

        highScoreList = (ListView) rot.findViewById(R.id.listHighScore);
        title = (TextView) rot.findViewById(R.id.txtHighScores);


        highScoreList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, pc.players));


        return rot;
    }



}
