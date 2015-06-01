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

import java.util.Iterator;

@SuppressWarnings("SameParameterValue")
public class LfsrIterator implements Iterator<Boolean> {
    private int lfsrValue = 0xca5d;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Boolean next() {
        int bit = (lfsrValue ^ (lfsrValue >> 2) ^ (lfsrValue >> 3) ^ (lfsrValue >> 5) ) & 1;
        lfsrValue =  (lfsrValue >> 1) | (bit << 15);
        return bit == 1;
    }

    public byte[] getBytes(int length) {
        byte[] bytes =  new byte[length];
        for (int i = 0; i < length; i++) {
            for (int j = 7; j >= 0; j--) {
                bytes[i] |= ((next()) ? 1 : 0) << j;
            }
        }
        return bytes;
    }

    @Override
    public void remove() {}
}
