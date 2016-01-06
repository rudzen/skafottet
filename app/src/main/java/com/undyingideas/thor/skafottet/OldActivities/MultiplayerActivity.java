package com.undyingideas.thor.skafottet.OldActivities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.WordPicker;
import com.undyingideas.thor.skafottet.utility.GameUtility;

public class MultiplayerActivity extends AppCompatActivity {

    private Button btnConnect;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        btnConnect = (Button) findViewById(R.id.btnConnect);

       mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

    }

    public void hotSeatClck(final View view) {
        final Intent hotSeatStart = new Intent(this, WordPicker.class);
        hotSeatStart.putExtra(GameUtility.KEY_MULIGE_ORD,getIntent().getStringArrayListExtra(GameUtility.KEY_MULIGE_ORD));
        hotSeatStart.putExtra(GameUtility.KEY_IS_HOT_SEAT, true);
        startActivity(hotSeatStart);
        finish();

    }


    private static class ConnectViaBlueTooth implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
        }
    }


}
