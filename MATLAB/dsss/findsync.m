function [ pos ] = findsync( data, sync )
%FINDSYNC Summary of this function goes here
%   Detailed explanation goes here

    x = xcorr(data, sync);
    x = x(length(data):end);
    x(x<max(x)) = 0;
    [val, pos] = findpeaks(x);
end

