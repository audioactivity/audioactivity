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

import java.util.Random;

public class DsssEncoder {
    private static final int CHIP_BUFFER_SIZE = 65535;

    public static byte[] encode(byte[] bytes, byte[] spreadcode, int chipsize) {
        byte[] input    = ModulationTools.bitsToBytes(bytes, chipsize);
        byte[] sequence = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            sequence[i] = (byte) ((input[i] ^ spreadcode[i % CHIP_BUFFER_SIZE]) & 0xFF);
        }

        return sequence;
    }

    public static byte[] encodeEdsss(byte[] bytes, byte[] spreadcode, int chipsize) {
        byte[] escort   = new byte[4];
        byte[] sequence = new byte[bytes.length * 8 * chipsize * 4];
        Random random   = new Random();

        escort[0] = (byte) 0b01010101;
        escort[2] = (byte) 0b10101010;

        for (int i = 0; i < bytes.length; i++) {
            escort[1] = bytes[i];
            escort[3] = (byte) random.nextInt();
            byte[] tempSequence = encode(escort, spreadcode, chipsize);

            System.arraycopy(tempSequence, 0, sequence, i * chipsize * 8 * 4, tempSequence.length);
        }

        return sequence;
    }
}
