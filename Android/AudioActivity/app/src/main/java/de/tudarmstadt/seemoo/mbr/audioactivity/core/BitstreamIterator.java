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

public class BitstreamIterator implements Iterator<Byte> {
    private final byte[] bytes;
    private final int length;
    private int bytePosition = 0;
    private int currentBit = 7;

    public BitstreamIterator(byte[] bytes) {
        this.length = bytes.length;
        this.bytes  = bytes;
    }

    public int getLength() {
        return bytes.length * 8;
    }

    @Override
    public boolean hasNext() {
        return bytePosition < length;
    }

    @Override
    public Byte next() {
        Byte ret = 1;


        if (bytePosition >= length) {
            return null;
        }

        if ((bytes[bytePosition] & (0x1 << currentBit--)) == 0) {
            ret = 0;
        }

        if (currentBit < 0) {
            currentBit = 7;
            bytePosition++;
        }

        return ret;
    }

    @Override
    public void remove() {}
}
