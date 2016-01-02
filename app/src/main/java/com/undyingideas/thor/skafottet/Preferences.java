package com.undyingideas.thor.skafottet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Preferences extends AppCompatActivity {

    private WebView explanation;
    private String theText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instillinger);

        theText = "<html><body>Her skal der være nogle instiller som styres med knapper, " +
                "og som bliver skrevet ned i en XML fil, eller som gemmes af sharePreferences</body></html>";
        explanation = (WebView) findViewById(R.id.etParOrd);
        explanation.loadData(theText, "text/html; charset=UTF-8", null);

    }
}
