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

package de.tudarmstadt.seemoo.mbr.audioactivity.info;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class InfoAdapter extends BaseAdapter {
    private final ArrayMap<String, String> informationMap;


    public InfoAdapter(ArrayMap<String, String> informationMap) {
        this.informationMap = informationMap;
    }

    @Override
    public int getCount() {
        return informationMap.size();
    }

    @Override
    public Object getItem(int position) {
        return informationMap.get(informationMap.keyAt(position));
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
            result = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) result.findViewById(android.R.id.text1);
            holder.text2 = (TextView) result.findViewById(android.R.id.text2);
            result.setTag(holder);
        } else {
            result = convertView;
        }

        ViewHolder holder = (ViewHolder) result.getTag();
        String key        = informationMap.keyAt(position);

        holder.text1.setText(key);
        holder.text2.setText(informationMap.get(key));

        return result;
    }

    private static class ViewHolder {
        public TextView text1;
        public TextView text2;
    }
}
