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

package de.tudarmstadt.seemoo.mbr.audioactivity.core;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.util.ArrayMap;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;

public class HardwareInformation {
    private static final int INPUT_ENCODING    = AudioFormat.ENCODING_PCM_16BIT;
    private static final int OUTPUT_ENCODING   = AudioFormat.ENCODING_PCM_FLOAT;
    private static final int INPUT_CHANNELS    = AudioFormat.CHANNEL_IN_MONO;
    private static final int OUTPUT_CHANNELS   = AudioFormat.CHANNEL_OUT_MONO;
    private static final int[] TESTRATES       =
            { 96000, 88200, 48000, 44100, 22050, 16000, 11025, 8000 };

    private final ArrayMap<String, String> values = new ArrayMap<>();
    private final Context context;

    public HardwareInformation(Context context) {
        this.context = context;
        getBasicInfo();
        getAudioInfo();
    }

    private void getBasicInfo() {
        values.put(context.getString(R.string.text_hw_model), Build.MODEL);
        values.put(context.getString(R.string.text_hw_manufacturer), Build.MANUFACTURER);
        values.put(context.getString(R.string.text_hw_product_name), Build.PRODUCT);
    }

    private void getAudioInfo() {
        for (int sampleRate : TESTRATES) {
            if (checkInputSampleRate(sampleRate)) {
                values.put(context.getString(R.string.text_hw_max_input_sample_rate), sampleRate + " Hz");
                break;
            }
        }

        for (int sampleRate: TESTRATES) {
            if (checkOutputSampleRate(sampleRate)) {
                values.put(context.getString(R.string.text_hw_max_output_sample_rate), sampleRate + " Hz");
                break;
            }
        }
    }

    private boolean checkInputSampleRate(int samplerate) {
        try {
            int size = AudioRecord.getMinBufferSize(samplerate, INPUT_CHANNELS, INPUT_ENCODING);
            return size > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkOutputSampleRate(int samplerate) {
        try {
            int size = AudioTrack.getMinBufferSize(samplerate, OUTPUT_CHANNELS, OUTPUT_ENCODING);
            return size > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayMap<String, String> getMap() {
        return values;
    }
}