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

public class FirFilter implements FilterInterface {
    private final int length;
    private final double[] impulseResponse;
    private final double[] delayLine;
    private int count = 0;

    public FirFilter(double[] impulseResponse) {
        this.impulseResponse = impulseResponse;
        this.length          = impulseResponse.length;
        this.delayLine       = new double[length];
    }

    public float[] filter(float[] in) {
        float[] out = new float[in.length];

        for (int i = 0; i < in.length; i++) {

            delayLine[count] = in[i];
            int index = count;

            for (int n = 0; n < length; n++) {
                out[i] += impulseResponse[n] * delayLine[index--];
                if (index < 0) {
                    index += length;
                }
            }
            if (++count >= length) {
                count = 0;
            }
        }

        return out;
    }
}
