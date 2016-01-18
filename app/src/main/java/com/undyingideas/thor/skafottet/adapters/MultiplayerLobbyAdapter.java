/*
 * Copyright 2016 Rudy Alex Kohn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Project : skafottet<br>
 * Adapter for showing multiplayer players from firebase..
 *
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
            viewHolder.textViewName = (TextView) view.findViewById(R.id.multiplayer_player_title);
            viewHolder.textViewScore = (TextView) view.findViewById(R.id.multiplayer_player_desc);
            viewHolder.icon = (ImageView) view.findViewById(R.id.multiplayer_player_icon);
            viewHolder.icon.setImageResource(R.drawable.game_end_won);
        }
        final LobbyDTO dto = data.get(position);
        StringBuilder sb = new StringBuilder(100);

        for (final LobbyPlayerStatus lobbyPlayerStatus : dto.getPlayerList()) {
            if (!lobbyPlayerStatus.getName().equals(activePlayer))
                sb.append(lobbyPlayerStatus.getName()).append(" , ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 3, sb.length());
        }
        viewHolder.textViewName.setText(sb.toString());

        sb = new StringBuilder(100);
        for (final LobbyPlayerStatus status : dto.getPlayerList()) {
            sb.append(status.getScore()).append(" , ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 3, sb.length());
        }
        viewHolder.textViewScore.setText(sb.toString());

        return view;
    }
}