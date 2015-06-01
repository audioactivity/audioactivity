/*
 * Copyright (c) 2015, Markus Brandt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.tudarmstadt.seemoo.mbr.audioactivity.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;

public class ActivityAdapter extends BaseAdapter implements ListView.OnItemClickListener {
    private ArrayList<ActivityEntry> activityEntries = new ArrayList<>();

    public ActivityAdapter(ArrayList<ActivityEntry> activityEntries) {
        this.activityEntries = activityEntries;
    }

    public ActivityAdapter(String[] activities, String[] descriptions) {
        int size = Math.min(activities.length, descriptions.length);

        this.activityEntries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            activityEntries.add(new ActivityEntry(activities[i], descriptions[i]));
        }
    }

    @Override
    public int getCount() {
        return activityEntries.size();
    }

    @Override
    public ActivityEntry getItem(int position) {
        return activityEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            result = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView tv = (TextView)result.findViewById(android.R.id.text1);
            result.setTag(tv);
        } else {
            result = convertView;
        }

        ActivityEntry entry = getItem(position);

        ((TextView)result.getTag()).setText(entry.getDescription());

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = parent.getContext();
        try {
            ActivityEntry entry = activityEntries.get(position);
            Class<?> name       = Class.forName(entry.getActivity());
            Intent intent       = new Intent(context, name);

            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.not_yet_implemented, Toast.LENGTH_SHORT).show();
        }
    }

    public class ActivityEntry {
        private final String activity;
        private final String description;

        public ActivityEntry(String activity, String description) {
            this.activity    = activity;
            this.description = description;
        }

        public String getActivity() {
            return activity;
        }

        public String getDescription() {
            return description;
        }
    }
}
