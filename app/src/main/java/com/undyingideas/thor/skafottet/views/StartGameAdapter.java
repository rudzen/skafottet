/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.views;

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
 * Project : R-TicTacToe<br>
 * Adapter for showing the relevant information about game selection.
 * @author rudz
 */
public class StartGameAdapter extends ArrayAdapter<StartGameItem> {

    private final Context mContext;
    private final int layoutResourceId;
    private final ArrayList<StartGameItem> data;

    public StartGameAdapter(final Context mContext, final int layoutResourceId, final ArrayList<StartGameItem> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
        }

        final StartGameItem startGameItem = data.get(position);

        final TextView textViewItem = (TextView) view.findViewById(R.id.new_game_item);
        textViewItem.setText(startGameItem.itemName);
        textViewItem.setTag(startGameItem.itemId);

        final TextView textViewDesc = (TextView) view.findViewById(R.id.new_game_description);
        textViewDesc.setText(startGameItem.description);
        textViewDesc.setTag(startGameItem.itemId);

        final ImageView icon = (ImageView) view.findViewById(R.id.new_game_icon);
        icon.setImageResource(startGameItem.resourceIcon);

        return view;
    }

}