/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
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
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Project : skafottet<br>
 * Adapter for showing multiplayer players from firebase..
 * @author rudz
 */
public class MultiplayerPlayersAdapter extends ArrayAdapter<PlayerDTO> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private final ArrayList<PlayerDTO> mData;
    private final ViewHolder mViewHolder = new ViewHolder();

    public MultiplayerPlayersAdapter(final Context mContext, final int mLayoutResourceId, final ArrayList<PlayerDTO> data) {
        super(mContext, mLayoutResourceId, data);

        this.mLayoutResourceId = mLayoutResourceId;
        this.mContext = mContext;
        mData = data;
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
            view = inflater.inflate(mLayoutResourceId, parent, false);
            mViewHolder.textViewName = (TextView) view.findViewById(R.id.multiplayer_player_title);
            mViewHolder.textViewScore = (TextView) view.findViewById(R.id.multiplayer_player_desc);
            mViewHolder.icon = (ImageView) view.findViewById(R.id.multiplayer_player_icon);
            mViewHolder.icon.setImageResource(R.drawable.game_end_won);
        }

        final PlayerDTO playerDTO = mData.get(position);

        mViewHolder.textViewName.setText(playerDTO.getName());
        //mViewHolder.textViewName.setTag(playerDTO.hashCode());

        mViewHolder.textViewScore.setText(Integer.toString(playerDTO.getScore()));
        //mViewHolder.textViewScore.setTag(playerDTO.hashCode());


        return view;
    }

}