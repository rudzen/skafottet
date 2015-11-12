package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Instructions extends AppCompatActivity {

    String instructionText = "<html><body><ol>" +
            "<li>Gæt på et bogstav ved at trykke på det</li>"+
            "<li>Hvis du gætter forkert bliver du lidt hængt</li>"+
            "<li>Hvis du gætter rigtigt bliver det vist på ordet</li>"+
            "<li><b>Vælg Multiplayer -> Hot Seat, for at kunne vælge et ord en af dine venner/fjender skal gætte</b></li>"+
            "<li>...</li>"+
            "<li>profit</li>"+
            "</ol></body></html>" ;
    WebView instructionDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruktioner);

        instructionDisplay = (WebView) findViewById(R.id.webView);
        instructionDisplay.loadData(instructionText, "text/html; charset=UTF-8", null);

    }
}
