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
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Adapter for local word lists.
 * @author rudz
 */
public class WordTitleLocalAdapter extends ArrayAdapter<WordItem> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private final ArrayList<WordItem> mData;

    public WordTitleLocalAdapter(final Context context, final int mLayoutResourceId, final ArrayList<WordItem> data) {
        super(context, mLayoutResourceId, data);
        this.mLayoutResourceId = mLayoutResourceId;
        mContext = context;
        mData = data;
    }

    private static final class ViewHolder {
        public static TextView s_textViewItemTitle;
        public static TextView s_textViewDesc;
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(mLayoutResourceId, parent, false);
            ViewHolder.s_textViewItemTitle = (TextView) view.findViewById(R.id.word_title_item_title);
            ViewHolder.s_textViewDesc = (TextView) view.findViewById(R.id.word_title_item_desc);
        }
        final WordItem wordItem = mData.get(position);
        ViewHolder.s_textViewItemTitle.setText(wordItem.getTitle());
        ViewHolder.s_textViewDesc.setText(wordItem.getUrl());
        view.setTag(position);
        return view;
    }

}