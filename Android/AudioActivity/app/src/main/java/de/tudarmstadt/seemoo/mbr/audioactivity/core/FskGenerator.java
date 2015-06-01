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
public class FskGenerator extends SineGenerator {
    private BitstreamIterator bitstreamIterator;
    private int centerFrequency = 18500;
    private int deviation = 500;
    private int currentSign = 1;
    private FilterInterface filterInterface;

    public void setBytes(byte[] bytes) {
        bitstreamIterator = new BitstreamIterator(bytes);
    }

    @Override
    protected void setup() {
        super.setup();
        filterInterface = new FirFilter(FirCoefficients.ULTRASONIC);
    }


    @Override
    protected float[] process(float[] in) {
        return filterInterface.filter(in);
    }

    @Override
    protected float[] generateSamples() {
        if (!bitstreamIterator.hasNext()) {
            return null;
        }

        if (bitstreamIterator.next() > 0) {
            setSineFrequency(centerFrequency + deviation);
        } else {
            setSineFrequency(centerFrequency - deviation);
        }

        float[] samples = super.generateSamples();

        currentSign *= -1;

        return samples;
    }

    private void setSineFrequency(int freq) {
        super.setSineFrequency(freq);
    }

    public void setCenterFrequency(int centerFrequency) {
        this.centerFrequency = centerFrequency;
    }

    public void setDeviation(int deviation) {
        this.deviation = deviation;
    }
}
