%% init
addpath('lib');
Fs   = 48000;
f    = 18000;
text = sprintf('abcdefghijklmnopqrstuvwxyz\n');

%% setup internals
sign   = 1;
slen   = Fs/31.25;
window = cos(pi*linspace(0, 1, slen));
out    = [];

%% synchronisation sequence
for i=1:10
    out  = [out (sign * window)];
    sign = sign * -1;
end

%% build envelope
for c=text % for each letter
    for b=varicode(c) % for each bit
        if b == '0'
            % phase change
            out  = [out (sign * window)];
            sign = sign * -1;
        else
            % no phase change
            out = [out (sign * ones(1, slen))];
        end
    end
    % append to 00 to mark character end
    out  = [out (sign * window) (-sign * window)];
end

%% apply envelope to signal
signal = sin(2*pi*f*(0:(length(out)-1))/Fs) .* out;

%% write to file
audiowrite(strcat('psk31-', num2str(f), '.wav'), signal, Fs);