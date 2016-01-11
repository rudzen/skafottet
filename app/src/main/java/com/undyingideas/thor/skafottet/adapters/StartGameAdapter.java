/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Adapter for showing the relevant information about game selection.
 * Tweaked to support custom tag for item selection based on current availble game modes.
 * @author rudz
 */
public class StartGameAdapter extends ArrayAdapter<StartGameItem> {

    private final Context context;
    private final int layoutResourceId;
    private final ArrayList<StartGameItem> data;

    public StartGameAdapter(final Context context, final int layoutResourceId, final ArrayList<StartGameItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    private static final class ViewHolder {
        public static TextView s_textViewItem;
        public static TextView s_textViewDesc;
        public static ImageView s_icon;
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder.s_textViewItem = (TextView) view.findViewById(R.id.new_game_item);
            ViewHolder.s_textViewDesc = (TextView) view.findViewById(R.id.new_game_description);
            ViewHolder.s_icon = (ImageView) view.findViewById(R.id.new_game_icon);
        }
        final StartGameItem startGameItem = data.get(position);
        ViewHolder.s_textViewItem.setText(startGameItem.itemName);
        ViewHolder.s_textViewDesc.setText(startGameItem.description);
        ViewHolder.s_icon.setImageResource(startGameItem.resourceIcon);
        view.setTag(startGameItem.itemId);
        return view;
    }

}