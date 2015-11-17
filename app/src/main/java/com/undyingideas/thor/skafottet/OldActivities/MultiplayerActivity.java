package com.undyingideas.thor.skafottet.OldActivities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.WordPicker;

public class MultiplayerActivity extends AppCompatActivity {

    Button btnConnect;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        btnConnect = (Button) findViewById(R.id.btnConnect);

       mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

    }

    public void hotSeatClck(View view) {
        Intent hotSeatStart = new Intent(this, WordPicker.class);
        hotSeatStart.putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd"));
        hotSeatStart.putExtra("isHotSeat", true);
        startActivity(hotSeatStart);
        finish();

    }


    private class ConnectViaBlueTooth implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
        }
    }


}