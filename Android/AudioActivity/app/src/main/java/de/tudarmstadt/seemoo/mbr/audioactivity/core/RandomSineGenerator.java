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

@SuppressWarnings("SameParameterValue")
public class RandomSineGenerator extends SineGenerator {
    private int lowestFrequency  = 50;
    private int highestFrequency = 20000;
    private int currentSign      = 1;
    private float[] fadeTable;

    @Override
    protected void setup() {
        super.setup();
        changeSamples(bufferSize * 10);
    }

    void changeSamples(int samples) {
        this.bufferSize = samples;
        fadeTable       = new float[samples];

        for (int i = 0; i < samples; i++) {
            fadeTable[i] = (float) Math.sin(i * (Math.PI/samples));
        }
    }

    @Override
    protected float[] generateSamples() {
        double freq = lowestFrequency + Math.random() * (highestFrequency - lowestFrequency);
        setSineFrequency(freq);

        float[] samples = super.generateSamples();
        int length      = samples.length;

        for (int i = 0; i < length; i++) {
            samples[i] *= fadeTable[i] * currentSign;
        }

        currentSign *= -1;

        return samples;
    }

    public void setLowestFrequency(int lowestFrequency) {
        this.lowestFrequency = lowestFrequency;
    }

    public void setHighestFrequency(int highestFrequency) {
        this.highestFrequency = highestFrequency;
    }
}
