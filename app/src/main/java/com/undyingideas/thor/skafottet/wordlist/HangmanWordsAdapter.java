/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.wordlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import java.util.List;

/**
 * Custom adapter to force using custom layout.
 * Created by rudz on 09-11-2015.
 * @author rudz
 */
public class HangmanWordsAdapter extends ArrayAdapter<String> {

    public HangmanWordsAdapter(final Context context, final List<String> values) {
        super(context, R.layout.hang_man_adapter_row_layout, values);
    }

    public HangmanWordsAdapter(final Context context, final WordList wl) {
        super(context, R.layout.hang_man_adapter_row_layout, wl.getCurrentList());
    }

    static class ViewHolder {
        public TextView text;
        public ImageView img;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.hang_man_adapter_row_layout, null);
            // configure view holder
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.adapter_text_view);
            viewHolder.img = (ImageView) rowView.findViewById(R.id.adapter_image_view);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final String s = getItem(position);
        holder.text.setText(s);
        holder.img.setImageResource(R.mipmap.ic_launcher);

        return rowView;
    }

    /* old code */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater theInflater = LayoutInflater.from(getContext());
//
//        @SuppressLint("ViewHolder") View theView = theInflater.inflate(R.layout.adapter_row_layout, parent, false);
//
//        String itemName = getItem(position);
//        TextView theTextView = (TextView) theView.findViewById(R.id.adapter_text_view);
//        theTextView.setText(itemName);
//
//        ImageView theImageView = (ImageView) theView.findViewById(R.id.adapter_image_view);
//        theImageView.setImageResource(R.drawable.rudztheme_btn_radio_off_holo_dark);
//
//        return theView;
//    }

}
