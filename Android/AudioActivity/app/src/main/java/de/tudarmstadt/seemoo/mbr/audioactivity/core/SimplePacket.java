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

public class SimplePacket {
    public static byte[] makePacket(byte[] inbytes) {
        int preambleSize = 3;
        int length = inbytes.length + preambleSize;
        byte[] outbytes = new byte[length];
        outbytes[0] = 0b01010101;
        outbytes[1] = 0b00110011;
        outbytes[2] = (byte) inbytes.length;

        System.arraycopy(inbytes, 0, outbytes, preambleSize, length - preambleSize);

        return outbytes;
    }

    public static byte[] prepend(byte[] bytes, byte[] pad) {
        byte[] outbytes = new byte[bytes.length + pad.length];

        System.arraycopy(pad, 0, outbytes, 0, pad.length);
        System.arraycopy(bytes, 0, outbytes, pad.length, bytes.length);

        return outbytes;
    }

    public static byte[] limit(byte[] bytes, int length) {
        return limit(bytes, length, 0);
    }

    public static byte[] limit(byte[] bytes, int length, int offset) {
        byte[] outbytes = new byte[length];

        System.arraycopy(bytes, offset, outbytes, 0, length);

        return outbytes;
    }
}
