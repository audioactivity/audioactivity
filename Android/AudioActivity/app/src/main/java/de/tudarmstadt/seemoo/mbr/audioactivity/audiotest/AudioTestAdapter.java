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

package de.tudarmstadt.seemoo.mbr.audioactivity.audiotest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.tudarmstadt.seemoo.mbr.audioactivity.core.BaseAudioGenerator;

public class AudioTestAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private final List<AudioTestEntry> audioTestEntries;
    private OnAudioStartListener listener;

    public AudioTestAdapter(List<AudioTestEntry> audioTestEntries) {
        this.audioTestEntries = audioTestEntries;
    }

    @Override
    public int getCount() {
        return audioTestEntries.size();
    }

    @Override
    public AudioTestEntry getItem(int position) {
        return audioTestEntries.get(position);
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

        AudioTestEntry entry = getItem(position);

        ((TextView)result.getTag()).setText(entry.getDescription());

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioTestEntry entry = audioTestEntries.get(position);
        stopAll();
        entry.getGenerator().start();

        if (listener != null) {
            listener.onAudioStart();
        }
    }

    public void setOnAudioStartListener(OnAudioStartListener listener) {
        this.listener = listener;
    }

    public void stopAll() {
        for (AudioTestEntry generator : audioTestEntries) {
            generator.getGenerator().stop();
        }
    }

    public static class AudioTestEntry {
        private final String description;
        private final BaseAudioGenerator generator;

        public AudioTestEntry(String description, BaseAudioGenerator generator) {
            this.description = description;
            this.generator   = generator;
        }

        public String getDescription() {
            return description;
        }

        public BaseAudioGenerator getGenerator() {
            return generator;
        }
    }

    public interface OnAudioStartListener {
        public void onAudioStart();
    }
}
