package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class activity_high_score extends AppCompatActivity {

    //
    private ListView highScoreList;
    private TextView title;
    //FIREBASE
    Firebase myFirebaseRef;
    HighScoreController pc;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hangmandtu.firebaseio.com/highscorelist");
        //limit = 10
       // pc = new HighScoreController(myFirebaseRef,10);


        highScoreList = (ListView) findViewById(R.id.listHighScore);
        title = (TextView) findViewById(R.id.txtHighScores);


        highScoreList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, pc.players));


    }


}
