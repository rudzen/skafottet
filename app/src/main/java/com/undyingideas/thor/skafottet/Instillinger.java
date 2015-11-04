package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Instillinger extends AppCompatActivity {

    WebView etParOrd;
    String ordene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instillinger);

        ordene = "<html><body>Her skal der være nogle instiller som styres med knapper, " +
                "og som bliver skrevet ned i en XML fil, som play kan hente frem</body></html>";
        etParOrd = (WebView) findViewById(R.id.etParOrd);
        etParOrd.loadData(ordene, "text/html; charset=UTF-8", null);

    }
}
