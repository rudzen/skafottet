package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;
import com.undyingideas.thor.skafottet.firebase.controller.LobbyController;
import com.undyingideas.thor.skafottet.firebase.controller.PlayerController;

import java.util.ArrayList;

public class Instructions extends AppCompatActivity {

    private static final String instructionText = "<html><body><ol>" +
            "<li>Gæt på et bogstav ved at trykke på det</li>"+
            "<li>Hvis du gætter forkert bliver du lidt hængt</li>"+
            "<li>Hvis du gætter rigtigt bliver det vist på ordet</li>"+
            "<li><b>Vælg Multiplayer -> Hot Seat, for at kunne vælge et ord en af dine venner/fjender skal gætte</b></li>"+
            "<li>...</li>"+
            "<li>profit</li>"+
            "</ol></body></html>" ;
    private WebView instructionDisplay;

    private ListView highScoreList;
    private TextView title;

    //FIREBASE
    Firebase myFirebaseRef;
    HighScoreController pc;
    ArrayList<PlayerScore> list;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruktioner);

        instructionDisplay = (WebView) findViewById(R.id.webView);
      //  instructionDisplay.loadData(instructionText, "text/html; charset=UTF-8", null);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hangmandtu.firebaseio.com/");
        //limit = 10,

        list = new ArrayList<>();

//        pc = new HighScoreController(myFirebaseRef,10, list,instructionDisplay);

        PlayerController pc = new PlayerController(myFirebaseRef);
        pc.createPlayer("Rudy");
        pc.createPlayer("Adam");
        pc.createPlayer("Theis");

        WordStatus ws = new WordStatus("hej", -1);
        ArrayList<WordStatus> ar = new ArrayList<>();
        ar.add(ws);
        LobbyPlayerStatus lps = new LobbyPlayerStatus("Rudy", ar);
        LobbyPlayerStatus lps2 = new LobbyPlayerStatus("Theis", ar);
        ArrayList<LobbyPlayerStatus> ar2 = new ArrayList<>();
        ar2.add(lps);
        ar2.add(lps2);
        LobbyController l = new LobbyController(myFirebaseRef);
        l.createLobby(new LobbyDTO(ar2));

        pc.getLobbyKey("Rudy");
        pc.getLobbyKey("Adam");
//        pc.createHighScore(player);


    }

    private static String wrap(final String text){
        return "<li>"+text+"</li>";
    }


}
