package com.undyingideas.thor.skafottet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;
import com.undyingideas.thor.skafottet.firebase.WordList.WordListController;
import com.undyingideas.thor.skafottet.firebase.WordList.WordListDTO;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;

import java.util.ArrayList;
import java.util.List;

public class Instructions extends AppCompatActivity implements Runnable {

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
    ArrayList<String> wordListDog = new ArrayList<>();

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
        wordListDog.add("scæfer");
        wordListDog.add("Pudel");
        wordListDog.add("Granddanois");
        Log.d("emil", ""+wordListDog.size());
        WordListDTO wlDTO = new WordListDTO("Hunde",wordListDog);
        Log.d("emil", ""+wlDTO.getWordList().size());
//        pc = new HighScoreController(myFirebaseRef,10, list,instructionDisplay);
        WordListController wlc = new WordListController(myFirebaseRef);
        MultiplayerController mpc = new MultiplayerController(myFirebaseRef, this);

        wlc.addList(wlDTO);

        mpc.pc.createPlayer("Rudy");
        mpc.pc.createPlayer("Adam");
        mpc.pc.createPlayer("Theis");

        mpc.pc.playerList.put("Rudy", new PlayerDTO("Rudy", 0, new ArrayList<String>()));
        mpc.login("Rudy");
        WordStatus ws = new WordStatus("hej", -1);
        ArrayList<WordStatus> ar = new ArrayList<>();
        ar.add(ws);
        LobbyPlayerStatus lps = new LobbyPlayerStatus("Rudy", ar);
        LobbyPlayerStatus lps2 = new LobbyPlayerStatus("Adam", ar);
        ArrayList<LobbyPlayerStatus> ar2 = new ArrayList<>();
        ar2.add(lps);
        ar2.add(lps2);

        mpc.lc.createLobby(new LobbyDTO(ar2));

//      pc.createHighScore(player);



    }


    @Override
    public void run() {

    }
}
