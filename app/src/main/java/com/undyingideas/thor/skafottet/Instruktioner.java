package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Instruktioner extends AppCompatActivity {

    String instruktionText = "<html><body>blaa blaa blaa såndan spilles spillet <br> <br> <b>192</b> points.</body></html>" ;
    WebView instruktioner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruktioner);

        instruktioner = (WebView) findViewById(R.id.webView);
        instruktioner.loadData(instruktionText,"text/html; charset=UTF-8", null);

    }
}
