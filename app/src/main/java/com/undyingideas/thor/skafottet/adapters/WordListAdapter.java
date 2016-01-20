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

/**
 * Created on 12-01-2016, 07:52.
 * Project : skafottet
 * Modofied from the stickylistheaders example
 * @author rudz
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class WordListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    protected static final int[] INTS = new int[0];
    protected static final Character[] CHARACTERS = new Character[0];
//    private final Context mContext;
    private ArrayList<String> mItems;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private final LayoutInflater mInflater;

    public WordListAdapter(final Context mContext, final ArrayList<String> mItems) {
//        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.mItems = mItems;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    public WordListAdapter(final Context context) {
//        mContext = context;
        mInflater = LayoutInflater.from(context);
//        mItems = context.getResources().getStringArray(R.array.countries);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        final ArrayList<Integer> sectionIndices = new ArrayList<>();
        char lastFirstChar = mItems.get(0).charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < mItems.size(); i++) {
            if (mItems.get(i).charAt(0) != lastFirstChar) {
                lastFirstChar = mItems.get(i).charAt(0);
                sectionIndices.add(i);
            }
        }
        final int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {
        final Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mItems.get(mSectionIndices[i]).charAt(0);
        }
        return letters;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.word_list_row_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(mItems.get(position));

        return convertView;
    }

    @Override
    public View getHeaderView(final int position, View convertView, final ViewGroup parent) {
        final HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.word_list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        holder.text.setText(mItems.get(position).subSequence(0, 1));

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(final int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mItems.get(position).subSequence(0, 1).charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(final int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    public void clear() {
        mItems.clear();
        mSectionIndices = INTS;
        mSectionLetters = CHARACTERS;
        notifyDataSetChanged();
    }

    public void restore(final ArrayList<String> list) {
        mItems = list;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

//    @Override
//    public void setProgressBar(final boolean visible) {
//        // TODO : handle future progressbar linked with the toolbar
//    }

    static class HeaderViewHolder {
        TextView text;
    }

    static class ViewHolder {
        TextView text;
    }

}