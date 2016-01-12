package com.undyingideas.thor.skafottet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created on 12-01-2016, 07:14.
 * Project : skafottet
 * To display the word lists as intended we need to expand a bit on the previous used methods.
 * @author rudz
 */
public class NewHangedWordAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private final String[] countries;
    private final LayoutInflater inflater;

    public NewHangedWordAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
        countries = new String[] {"abe", "beta", "banan", "test", "ost" };
//        countries = context.getResources().getStringArray(R.array.countries);
    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @Override
    public Object getItem(final int position) {
        return countries[position];
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.hang_man_adapter_row_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(countries[position]);

        return convertView;
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    public View getHeaderView(final int position, View convertView, final ViewGroup parent) {
        final HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.word_list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        final String headerText = String.valueOf(countries[position].subSequence(0, 1).charAt(0));
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(final int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return countries[position].subSequence(0, 1).charAt(0);
    }

    static class HeaderViewHolder {
        TextView text;
    }

    static class ViewHolder {
        TextView text;
    }

}