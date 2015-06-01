from gnuradio.digital.packet_utils import \
        conv_packed_binary_string_to_1_0_string, \
        conv_1_0_string_to_packed_binary_string


def repeat_encode(payload, repeat):
    return ''.join(map(lambda x: x * repeat, payload))


def repeat_decode(payload, repeat):
    bits = []
    for p in [payload[i:i + repeat] for i in range(0, len(payload), repeat)]:
        bitstring = conv_packed_binary_string_to_1_0_string(p)
        for bit in range(8):
            positions = [i+bit for i in range(0, len(bitstring), 8)]
            repeated_bits = map(lambda pos: int(bitstring[pos]), positions)
            bits += [str(int(round(sum(repeated_bits) / float(repeat))))]
    return conv_1_0_string_to_packed_binary_string(''.join(bits))[0]


def shuffle_encode(payload):
    p = list(payload)
    for i in range(len(payload) / 2):
        x, y = i**3 % len(payload), ((len(payload) - 1) - i)**3 % len(payload)
        a = p[x]
        p[x] = p[y]
        p[y] = a

    return ''.join(p)


def shuffle_decode(payload):
    p = list(payload)
    for i in range(len(payload) / 2):
        i = len(payload) / 2 - i - 1
        x, y = i**3 % len(payload), ((len(payload) - 1) - i)**3 % len(payload)
        a = p[x]
        p[x] = p[y]
        p[y] = a

    return ''.join(p)


def interleave(payload, repeat):
    return ''.join([''.join([payload[i*repeat + j]
                             for i in range(int(len(payload) / repeat))])
                    for j in range(repeat)])


def deinterleave(payload, repeat):
    return interleave(payload, int(len(payload) / repeat))
