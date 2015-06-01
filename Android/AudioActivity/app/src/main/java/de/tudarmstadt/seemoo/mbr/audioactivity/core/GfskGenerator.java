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

public class GfskGenerator extends BaseAudioGenerator {
    private final int samplesPerSymbol   = 2;
    private final double[] gaussian      = Gnuradio.generateGaussianTaps(1.0, 2, 0.35,
            4 * samplesPerSymbol);
    private final FilterInterface filter = new FirFilter(gaussian);
    private double centerFrequency       = 18500;
    private double deviation             = 10;
    private double phase                 = 0;
    private float[] modulator            = {};
    private int modulatorPos             = 0;

    public void setBytes(byte[] bytes, int interpolation) {
        float[] temp1 = ModulationTools.bitsToNRZ(bytes, 2);
        float[] temp2 = filter.filter(temp1);
        float[] temp3 = ModulationTools.repeat(temp2, interpolation / 2);

        this.modulator = filter.filter(temp3);
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    public void setCenterFrequency(double centerFrequency) {
        this.centerFrequency = centerFrequency;
    }

    @Override
    public void start() {
        modulatorPos = 0;
        super.start();
    }

    @Override
    protected float[] generateSamples() {
        float[] sample;

        if (modulator.length - modulatorPos <= 0) {
            return null;
        } else if (modulator.length - modulatorPos < bufferSize) {
            sample = new float[modulator.length - modulatorPos];
        } else {
            sample = new float[bufferSize];
        }

        for (int i = 0; i < sample.length; i++) {
            double cycleInc = (centerFrequency + deviation * modulator[modulatorPos++]) /
                              sampleRate;
            phase += cycleInc;

            if (phase > 1) {
                phase -= 1;
            }

            sample[i] = (float) Math.sin(2 * Math.PI * phase);
        }

        if (phase > 1) {
            phase -= Math.round(phase - 0.5f);
        }

        return sample;
    }
}
