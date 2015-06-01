%% init (UU is the preamble)
clear all
message  = 'SEEMOO';
message  = 'abcdefghijklmnopqrstuvwxyz';
preamble = 'U';
eom      = char(bitxor(uint8(preamble), 255));
f        = 8000;
f        = 0;
Fs       = 48000;
chipsize = 4096/32;
repeat   = 1;
chipsize = chipsize / repeat;
message  = ['U3' uint8(length(message)) message];
queue    = [repmat(preamble, 1, length(message));...
            message;...
            repmat(eom, 1, length(message))];
            

%% init code - ascii to dec [-1 1]
code = csvread('whitecode.csv');
code = (code - 128) / 128;
code = 2 * (code > 0) - 1;
code = [code code code code];
codeseq = code(1:3*8*chipsize);

%% convert message to bits and spread symbols to chipsize
out = [];
for i=1:length(queue)
    out = [out ((randn(1, 8*chipsize) > 0) * 2) - 1];
    bits = reshape(dec2bin(queue(:, i)', 8)', 1, 3 * 8);
    bits = kron(((bits - 48) * 2) - 1, ones(1, chipsize));
    out = [out (bits .* codeseq)];
end
out = [zeros(1, 10 * chipsize) kron(out, ones(1, repeat)) zeros(1, 10 * chipsize)];

%% generate code
if repeat > 1
    [b,a] = butter(6, 1/repeat);
    out  = filter(b, a, out);
end

if f > 0
    %outc = complex(out, -imag(hilbert(out)));
    outc = conj(hilbert(out));
    t    = (0:(length(outc)-1)) / Fs;

    outc = outc .* complex(cos(2*pi*f*t), -sin(2*pi*f*t));
    out  = real(outc);

    %plot(abs(fftshift(fft(outc))));
end

audiowrite(strcat('edsss/edsss-', num2str(chipsize),...
                  '-', num2str(repeat), '-', num2str(f),...
                  '.wav'), out / 10, Fs);