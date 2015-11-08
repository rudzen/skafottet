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

    }

    public void startClck(View v){
        Intent startSpil = new Intent(MainActivity.this, Play.class);
        startSpil.putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd"));
        startActivity(startSpil);
    }

    public void instlClck(View v){
        startActivity(new Intent(MainActivity.this, Instillinger.class));

    }

    public void instClck(View view) {
        Intent Instillinger = new Intent(MainActivity.this, Instruktioner.class);
        startActivity(Instillinger);

    }
}
