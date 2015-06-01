def char_errors(payload, compare):
    errors = 0

    if len(payload) > len(compare):
        payload = payload[:len(compare)]
    elif len(payload) < len(compare):
        errors = len(compare) - len(payload)
        compare = compare[:len(payload)]

    return errors + sum([int(i[0] != i[1]) for i in zip(payload, compare)])


def bit_errors(payload, compare):
    errors = 0

    if len(payload) > len(compare):
        payload = payload[:len(compare)]
    elif len(payload) < len(compare):
        errors = (len(compare) - len(payload)) * 8
        compare = compare[:len(payload)]

    return errors + sum([bin(ord(i[0]) ^ ord(i[1])).count('1')
                         for i in zip(payload, compare)])
