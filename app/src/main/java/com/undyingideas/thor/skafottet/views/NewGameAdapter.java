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

/**
 * Created on 27-12-2015, 09:59.<br>
 * Project : R-TicTacToe<br>
 * Adapter for showing the relevant information about new game selection.
 * @author rudz
 */
public class NewGameAdapter extends ArrayAdapter<NewGameItem> {

    private final Context mContext;
    private final int layoutResourceId;
    private final NewGameItem[] data;

    public NewGameAdapter(final Context mContext, final int layoutResourceId, final NewGameItem[] data) {

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

        final NewGameItem newGameItem = data[position];

        final TextView textViewItem = (TextView) view.findViewById(R.id.new_game_item);
        textViewItem.setText(newGameItem.itemName);
        textViewItem.setTag(newGameItem.itemId);

        final TextView textViewDesc = (TextView) view.findViewById(R.id.new_game_description);
        textViewDesc.setText(newGameItem.description);
        textViewDesc.setTag(newGameItem.itemId);

        final ImageView icon = (ImageView) view.findViewById(R.id.new_game_icon);
        icon.setImageResource(newGameItem.resourceIcon);

        return view;
    }

}