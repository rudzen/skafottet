package com.undyingideas.thor.skafottet;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Thor on 17-11-2015.
 */
public class MultiPlayerFragment extends Fragment {

    Button btnConnect;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rod = i.inflate(R.layout.activity_multiplayer, container, false);
        Button hotSeatBtn = (Button) rod.findViewById(R.id.HotSeatBtn);
        hotSeatBtn.setOnClickListener(new hotSeatClick());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return rod;
    }

    private class hotSeatClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("multiFrag", "button clicked");
            Fragment hotSeatStart = new WordPicker();
            Bundle wordPickerData = new Bundle();
            wordPickerData.putBoolean("isHotSeat",true);
            hotSeatStart.setArguments(wordPickerData);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragmentindhold, hotSeatStart).commit();
        }
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
