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
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Adapter for showing the relevant information about game selection.
 * Tweaked to support custom tag for item selection based on current availble game modes.
 * @author rudz
 */
public class WordTitleListAdapter extends ArrayAdapter<WordItem> {

    private final Context context;
    private final int layoutResourceId;
    private final ArrayList<WordItem> data;

    public WordTitleListAdapter(final Context context, final int layoutResourceId, final ArrayList<WordItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
            final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder.s_textViewItemTitle = (TextView) view.findViewById(R.id.word_title_item_title);
            ViewHolder.s_textViewDesc = (TextView) view.findViewById(R.id.word_title_item_desc);
        }
        final WordItem wordItem = data.get(position);
        ViewHolder.s_textViewItemTitle.setText(wordItem.getTitle());
        ViewHolder.s_textViewDesc.setText(wordItem.getUrl());
        view.setTag(position); // tag the bastard
        return view;
    }

}