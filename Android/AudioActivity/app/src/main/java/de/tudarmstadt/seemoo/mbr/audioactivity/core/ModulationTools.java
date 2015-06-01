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

public class ModulationTools {
    public static byte[] bitsToBytes(byte[] bytes) {
        return bitsToBytes(bytes, 1);
    }

    public static byte[] bitsToBytes(byte[] bytes, int repeat) {
        BitstreamIterator bits = new BitstreamIterator(bytes);
        byte[] interpolated = new byte[bytes.length * repeat * 8];

        for (int i = 0; i < interpolated.length;) {
            byte currentByte = (byte) (bits.next() * 0xFF);
            for (int j = 0; j < repeat; j++, i++) {
                interpolated[i] = currentByte;
            }
        }

        return interpolated;
    }

    public static float[] bitsToNRZ(byte[] bytes) {
        return bitsToNRZ(bytes, 1);
    }

    public static float[] bitsToNRZ(byte[] bytes, int interpolate) {
        BitstreamIterator bits = new BitstreamIterator(bytes);
        float[] interpolated = new float[bytes.length * interpolate * 8];

        for (int i = 0; i < interpolated.length;) {
            float currentByte = (bits.next() == 1) ? 1.0f : -1.0f;
            for (int j = 0; j < interpolate; j++, i++) {
                interpolated[i] = currentByte;
            }
        }

        return interpolated;
    }

    public static float[] repeat(float[] stream, int interpolation) {
        float[] out = new float[stream.length * interpolation];
        int       k = 0;

        for (float aStream : stream) {
            for (int j = 0; j < interpolation; j++) {
                out[k++] = aStream;
            }
        }

        return out;
    }

    public static byte[] repeat(byte[] stream, int interpolation) {
        byte[] out = new byte[stream.length * interpolation];
        int       k = 0;

        for (byte aStream : stream) {
            for (int j = 0; j < interpolation; j++) {
                out[k++] = aStream;
            }
        }

        return out;
    }

    public static byte[] shuffleEncode(byte[] payload) {
        byte[] out = new byte[payload.length];

        System.arraycopy(payload, 0, out, 0, payload.length);

        int steps = (int) Math.floor((double)out.length / 2.0);
        for (int i = 0; i < steps; i++) {
            int x = (i*i*i) % out.length;
            int y = ((out.length - 1) - i);
            y = (y * y * y) % out.length;
            byte a = out[x];
            out[x] = out[y];
            out[y] = a;
        }

        return out;
    }

    /*
    def interleave(payload, repeat):
    return ''.join([''.join([payload[i*repeat + j]
                             for i in range(int(len(payload) / repeat))])
                    for j in range(repeat)])
     */
    public static byte[] interleave(byte[] payload, int repeat) {
        byte[] out = new byte[payload.length];

        for (int i = 0; i < payload.length; i++) {
            out[i] = payload[(i * repeat) % payload.length];
        }

        return out;
    }

    @SuppressWarnings("SameParameterValue")
    public static byte[] repeatBits(byte[] bytes, int repeat) {
        BitstreamIterator bits = new BitstreamIterator(bytes);
        byte[] interpolated = new byte[bytes.length * repeat];

        int bytePosition = 0;
        int bitPosition = 0;

        while (bits.hasNext()) {
            byte currentBit = bits.next();
            for (int i = 0; i < repeat; i++) {
                interpolated[bytePosition] = (byte) ((interpolated[bytePosition] << 1) & 0xFF);
                interpolated[bytePosition] |= currentBit;
                if (++bitPosition > 8) {
                    bitPosition = 0;
                    bytePosition++;
                }
            }
        }

        return interpolated;
    }

    public static double[] convolute(double[] taps, int sps) {
        double[] padded = new double[taps.length + 2 * (sps - 1)];
        double[] out    = new double[taps.length + sps - 1];

        System.arraycopy(taps, 0, padded, sps - 1, taps.length);

        for (int i = 0; i < padded.length - sps + 1; i++) {
            for (int j = 0; j < sps ; j++) {
                out[i] += padded[i + j];
            }
        }

        return out;
    }
}
