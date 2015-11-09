package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button StartBtn, InstilBtn, InstBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnMultiplayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MultiplayerActivity.class));
            }
        });

    }

    /**
     * knap til at starte 1p spil
     * @param v
     */
    public void startClck(View v){
        Intent startSpil = new Intent(MainActivity.this, Play.class);
        startSpil.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        //Intent startSpil = new Intent(MainActivity.this, LoadingScreen.class);

        startActivity(startSpil);
    }

    /**
     * Knap til instillings aktivitet
     * @param v
     */
    public void instlClck(View v){
        startActivity(new Intent(MainActivity.this, Instillinger.class));

    }

    /**
     * knap til instruktions aktivitet
     * @param view
     */
    public void instClck(View view) {
        Intent Instillinger = new Intent(MainActivity.this, Instruktioner.class);
        startActivity(Instillinger);

    }

    public void seOrdClck(View v){
        Intent SeOrd = new Intent(MainActivity.this, OrdVaelger.class);
        SeOrd.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
        startActivity(SeOrd);
    }
}
