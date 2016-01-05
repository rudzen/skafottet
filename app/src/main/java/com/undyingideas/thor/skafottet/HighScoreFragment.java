package com.undyingideas.thor.skafottet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

/**
 * Created on 04-01-2016, 06:34.
 * Project : skafottet
 * @author Gump
 */
public class HighScoreFragment extends Fragment{

    private ListView highScoreList;
    private TextView title;

    //FIREBASE
    Firebase myFirebaseRef;
    HighScoreController pc;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View rot = inflater.inflate(R.layout.activity_high_score, container, false);

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
