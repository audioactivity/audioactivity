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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

@SuppressWarnings("SameParameterValue")
public abstract class BaseAudioGenerator implements Runnable {
    private static final int AUDIO_MODE = AudioTrack.MODE_STREAM;
    private static final int AUDIO_TYPE = AudioManager.STREAM_MUSIC;
    private static final int ENCODING   = AudioFormat.ENCODING_PCM_FLOAT;
    private static final int CHANNELS   = AudioFormat.CHANNEL_OUT_MONO;

    private final AudioTrack audioTrack;
    private Thread thread;
    private boolean padWithSilence = false;
    private boolean isPlaying = false;
    private boolean suppressHandler = false;
    private float amplitude = 1.0f;
    private OnGeneratorStopListener onGeneratorStopListener;

    private static class MyHandler extends Handler {
        private final WeakReference<BaseAudioGenerator> weakReferenceGenerator;

        MyHandler(BaseAudioGenerator generator) {
            weakReferenceGenerator = new WeakReference<>(generator);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseAudioGenerator generator          = weakReferenceGenerator.get();
            if (generator != null && generator.onGeneratorStopListener != null &&
                !generator.suppressHandler) {
                generator.onGeneratorStopListener.onGeneratorStop();
            }
        }
    }

    private final Handler handler = new MyHandler(this);

    final int sampleRate;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    int bufferSize = 64;

    BaseAudioGenerator() {
        this(96000);
    }

    @SuppressWarnings("WeakerAccess")
    BaseAudioGenerator(int sampleRate) {
        this.sampleRate = sampleRate;
        int minSize = AudioTrack.getMinBufferSize(sampleRate, CHANNELS, ENCODING);
        audioTrack = new AudioTrack(AUDIO_TYPE, sampleRate, CHANNELS, ENCODING, minSize, AUDIO_MODE);
        setup();
    }

    public interface OnGeneratorStopListener {
        public void onGeneratorStop();
    }

    abstract protected float[] generateSamples();

    void setup() {}

    float[] process(float[] in) {
        return in;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public float getAmplitudeDb() {
        return (float) (20 * Math.log10(amplitude));
    }

    private float[] applyAmplitude(float[] samples) {
        float[] out = new float[samples.length];

        for (int i = 0; i < samples.length; i++) {
            out[i] = amplitude * samples[i];
        }

        return out;
    }

    public void setPadWithSilence(boolean pad) {
        this.padWithSilence = pad;
    }

    @Override
    public void run() {
        float[] samples;

        isPlaying = true;
        suppressHandler = false;
        audioTrack.play();

        if (padWithSilence) {
            samples = process(new float[bufferSize * 16]);
            audioTrack.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
        }

        while (isPlaying) {
            samples = generateSamples();
            if (samples != null) {
                samples = applyAmplitude(process(samples));
                audioTrack.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
            } else {
                isPlaying = false;
            }
        }

        if (padWithSilence) {
            samples = process(new float[bufferSize * 16]);
            audioTrack.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
        }

        audioTrack.stop();
        thread = null;
        handler.sendEmptyMessage(0);
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        audioTrack.pause();
        isPlaying = false;
    }

    public void stopImmediate() {
        suppressHandler = true;
        stop();
    }

    public void setOnGeneratorStopListener(OnGeneratorStopListener stopListener) {
        this.onGeneratorStopListener = stopListener;
    }
}
