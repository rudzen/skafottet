/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;
import com.undyingideas.thor.skafottet.support.wordlist.WordList;

import java.util.ArrayList;

/**
 * Custom adapter to force using custom layout.
 * Created by rudz on 09-11-2015.
 * @author rudz
 */
public class HangmanWordsAdapter extends ArrayAdapter<WordItem> {

    public HangmanWordsAdapter(final Context context, final ArrayList<WordItem> values) {
        super(context, R.layout.hang_man_adapter_row_layout, values);
    }

    public HangmanWordsAdapter(final Context context, final WordList wl) {
        super(context, R.layout.hang_man_adapter_row_layout, wl.getCurrentList());
    }

    static class ViewHolder {
        public static TextView text;
        public static ImageView img;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View root = convertView;
        // reuse views
        if (root == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            root = inflater.inflate(R.layout.hang_man_adapter_row_layout, null);
            // configure view holder
            ViewHolder.text = (TextView) root.findViewById(R.id.adapter_text_view);
            ViewHolder.img = (ImageView) root.findViewById(R.id.adapter_image_view);
        }

        final ViewHolder holder = (ViewHolder) root.getTag();

        final WordItem s = getItem(position);
//        holder.text.setText(s);
//        holder.img.setImageResource(R.mipmap.ic_launcher);



        return root;
    }

    private Bitmap createIcon(final String letter) {
        return null;
    }




}
