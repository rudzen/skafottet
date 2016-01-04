package com.undyingideas.thor.skafottet;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created on 17-11-2015, 08:39.
 * Project : skafottet
 * @author Thor
 */
public class MultiPlayerFragment extends Fragment {

    Button btnConnect;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rod = i.inflate(R.layout.activity_multiplayer, container, false);
        final Button hotSeatBtn = (Button) rod.findViewById(R.id.HotSeatBtn);
        hotSeatBtn.setOnClickListener(new hotSeatClick());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return rod;
    }

    private class hotSeatClick implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            Log.d("multiFrag", "button clicked");
            final Fragment hotSeatStart = new WordPicker();
            final Bundle wordPickerData = new Bundle();
            wordPickerData.putBoolean("isHotSeat",true);
            hotSeatStart.setArguments(wordPickerData);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragmentindhold, hotSeatStart).commit();
        }
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
