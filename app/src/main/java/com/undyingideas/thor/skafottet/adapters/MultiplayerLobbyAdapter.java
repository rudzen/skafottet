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
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Project : skafottet<br>
 * Adapter for showing multiplayer players from firebase..
 * @author rudz
 */
@SuppressWarnings("StaticVariableOfConcreteClass")
public class MultiplayerLobbyAdapter extends ArrayAdapter<LobbyDTO> {

    private final Context mContext;
    private final int layoutResourceId;
    private final ArrayList<LobbyDTO> data;
    private static final ViewHolder viewHolder = new ViewHolder();
    private final String activePlayer;

    public MultiplayerLobbyAdapter(final String player, final Context mContext, final int layoutResourceId, final ArrayList<LobbyDTO> data) {
        super(mContext, layoutResourceId, data);
        activePlayer = player;
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



        final LobbyDTO dto = data.get(position);

        StringBuilder sb = new StringBuilder(100);

//        String names = "";
        for (final LobbyPlayerStatus lobbyPlayerStatus : dto.getPlayerList() ){
            if (!lobbyPlayerStatus.getName().equals(activePlayer))
                sb.append(lobbyPlayerStatus.getName()).append(" , ");
//                names += lobbyPlayerStatus.getName() + " , ";
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 3, sb.length());
        }
//        names = names.substring(0, names.length()-3);
        viewHolder.textViewName.setText(sb.toString());

        sb = new StringBuilder(100);
        for (final LobbyPlayerStatus status: dto.getPlayerList()) {
            sb.append(status.getScore()).append(" , ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 3, sb.length());
        }
        viewHolder.textViewScore.setText(sb.toString());

//        names = "";
//        for (final WordStatus s : dto.getPlayerList().get(0).getWordList())
//            names += s.getScore() + " , ";
//        names = names.substring(0,names.length()-3);
//        viewHolder.textViewScore.setText(names);

        return view;
    }

}