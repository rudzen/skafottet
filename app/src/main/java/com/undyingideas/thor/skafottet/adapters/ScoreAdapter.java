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

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.highscore.local.Score;
import com.undyingideas.thor.skafottet.views.AutoScaleTextView;

import java.util.ArrayList;

/**
 * Created on 27-12-2015, 09:59.<br>
 * Adapter for showing the highscore list
 * @author rudz
 */
public class ScoreAdapter extends ArrayAdapter<Score> {

    private final Context mContext;
    private final int layoutResourceId;
    private final ArrayList<Score> mData;

    public ScoreAdapter(final Context context, final int layoutResourceId, final ArrayList<Score> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        mContext = context;
        mData = data;
    }

    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static final class ViewHolder {
        public static AutoScaleTextView s_Name;
        public static AutoScaleTextView s_Word;
        public static AutoScaleTextView s_Date;
        public static AutoScaleTextView s_Points;
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder.s_Name = (AutoScaleTextView) view.findViewById(R.id.highscore_name);
            ViewHolder.s_Word = (AutoScaleTextView) view.findViewById(R.id.highscore_word);
            ViewHolder.s_Date = (AutoScaleTextView) view.findViewById(R.id.highscore_date);
            ViewHolder.s_Points = (AutoScaleTextView) view.findViewById(R.id.highscore_points);
        }

        final Score score = mData.get(position);
        ViewHolder.s_Name.setText(score.getmName());
        ViewHolder.s_Word.setText(score.getmWord());
        ViewHolder.s_Date.setText(score.getDateString());
        ViewHolder.s_Points.setText(Integer.toString(score.getmScore()));
        view.setTag(ViewHolder.s_Name);
        return view;
    }

}