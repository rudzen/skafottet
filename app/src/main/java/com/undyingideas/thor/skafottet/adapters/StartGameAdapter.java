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