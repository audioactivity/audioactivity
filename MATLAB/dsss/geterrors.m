function [ cerr, berr, cerrp, berrp ] = geterrors( a, b )
%GETERRORS Summary of this function goes here
%   Detailed explanation goes here

    alen = length(a);
    blen = length(b);
    cerr = max(abs(blen - alen), 0);
    berr = 8 * cerr;
    len = min(alen, blen);
    for i=1:len
        if a(i) == b(i)
            continue;
        end
        
        cerr  = cerr + 1;
        berr  = berr + length(strfind(dec2bin(bitxor(uint8(a(i)), uint8(b(i)))), '1'));
    end
    cerrp = (cerr * 100) / blen;
    berrp = (berr * 100) / (blen * 8);
end

