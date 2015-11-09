package com.undyingideas.thor.skafottet;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
