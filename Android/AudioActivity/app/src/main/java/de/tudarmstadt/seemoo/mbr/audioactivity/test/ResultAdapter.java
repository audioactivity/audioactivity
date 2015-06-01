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

package de.tudarmstadt.seemoo.mbr.audioactivity.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.Arrays;
import java.util.HashMap;

class ResultAdapter extends BaseAdapter {
    private final HashMap<Integer, Boolean> results;
    private final Integer[] frequencies;

    public ResultAdapter(HashMap<Integer, Boolean> results) {
        this.results     = results;
        this.frequencies = results.keySet().toArray(new Integer[results.size()]);
        Arrays.sort(this.frequencies);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public String getItem(int position) {
        return frequencies[position].toString();
    }

    Boolean isItemChecked(int position) {
        return results.get(frequencies[position]);
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
            result = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);

            CheckedTextView tv = (CheckedTextView) result.findViewById(android.R.id.text1);
            result.setTag(tv);
        } else {
            result = convertView;
        }

        CheckedTextView tv = (CheckedTextView) result.getTag();
        tv.setText(getItem(position));
        tv.setChecked(isItemChecked(position));

        return result;
    }
}
