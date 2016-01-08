/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2015.
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.undyingideas.thor.skafottet.multiplayer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Project : skafottet<br>
 * Adapter for showing multiplayer players from firebase..
 * @author rudz
 */
public class MultiplayerPlayersAdapter extends ArrayAdapter<PlayerDTO> {

    private final Context mContext;
    private final int layoutResourceId;
    private final ArrayList<PlayerDTO> data;
    private static final ViewHolder viewHolder = new ViewHolder();

    public MultiplayerPlayersAdapter(final Context mContext, final int layoutResourceId, final ArrayList<PlayerDTO> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    static class ViewHolder {
        TextView textViewName;
        TextView textViewScore;
        ImageView icon; // not used atm
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            viewHolder.textViewName = (TextView) view.findViewById(R.id.new_game_item);
            viewHolder.textViewScore = (TextView) view.findViewById(R.id.new_game_description);
            viewHolder.icon = (ImageView) view.findViewById(R.id.new_game_icon);
            viewHolder.icon.setImageResource(R.drawable.game_end_won);
        }

        final PlayerDTO playerDTO = data.get(position);

        viewHolder.textViewName.setText(playerDTO.getName());
        //viewHolder.textViewName.setTag(playerDTO.hashCode());

        viewHolder.textViewScore.setText(Integer.toString(playerDTO.getScore()));
        //viewHolder.textViewScore.setTag(playerDTO.hashCode());


        return view;
    }

}