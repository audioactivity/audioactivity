%% init
clear all
Fs       = 48000;
chipsize = 4096/32;
preamble = 'U';
expected = 'SEEMOO';
expected = 'abcdefghijklmnopqrstuvwxyz';
f        = 18000;
f        = 0;
repeat   = 1;
chipsize = chipsize / repeat;
eom      = char(bitxor(uint8(preamble), 255));

%% init code - ascii to dec [-1 1]
code    = csvread('whitecode.csv');
code    = (code - 128) / 128;
code    = 2 * (code > 0) - 1;
code    = kron(code, ones(1, repeat));
code    = [code code code code];
codeseq = code(1:4*8*chipsize*repeat);
syncseq = [((dec2bin(preamble, 8) - 48) * 2 - 1) zeros(1, 16) ((dec2bin(eom) - 48) * 2 - 1)];
syncseq = kron(syncseq, ones(1, repeat));
syncseq = codeseq .* kron(syncseq, ones(1, chipsize));

%% setup audio
rec = dsp.AudioRecorder('QueueDuration', 8,...
                        'NumChannels', 1, ...
                        'SampleRate', Fs, ...
                        'SamplesPerFrame', chipsize * 8);
 
%% plot
%myplot = plot(nan);
                    
%% main loop
last = [];
plen = 0;
buffer = '...';
msg = [];
t   = (0:(chipsize * 8 - 1)) / Fs;
car = complex(cos(2*pi*f*t), -sin(2*pi*f*t));
while true
    y  = step(rec)';
    %y  = complex(y, -imag(hilbert(y)));
    y  = conj(hilbert(y));
    y  = y .* car;
    y  = real(y);
    
    if length(last) < (chipsize * 4 * 8 * repeat)
        last = [last y];
        continue;
    end 
    
    data = [last y];
    last = data(length(y) + 1:end);

    pos = findsync(data, syncseq);

    %plot(x);
    %drawnow;
    
    blocks = floor((length(data) - pos) / (chipsize * repeat));
    rx = data(pos+1:pos+blocks*chipsize*repeat);

    if ~isempty(rx)
        repeats = ceil((blocks * chipsize * repeat) / length(code));
        codeseq = kron(codeseq, ones(1, repeat));
        codeseq = kron(ones(1, repeats), code);
        codeseq = codeseq(1:length(rx));

        z = reshape(rx .* codeseq, chipsize * repeat, blocks);
        %z = round((mean(z) + 1) / 2);
        z = mean(z) > 0;
        chars = floor(length(z)/8);
        chars = reshape(z(1:chars*8), 8, chars);

        if ~isempty(chars)
            message = char(bi2de(chars', 'left-msb'))';
            eom = char(bitxor(uint8(message(1)), 255));
            if length(message) >= 3 && (message(3) == eom)
                fprintf('%c', message(2));
                if plen == 0
                   buffer = [buffer message(2)];
                   buffer = buffer(end-2:end);
                   if buffer(1) == 'U' && buffer(2) == '3'
                       plen = uint8(buffer(3));
                       %buffer = '...';
                       fprintf('\ndetected start. length = %d\n', plen);
                   end
                else
                    msg = [msg char(message(2))];
                    if length(msg) >= plen
                        plen = 0;
                        [cer, ber, cerp, berp] = geterrors(msg, expected);
                        fprintf(' [BER: %d (%0.2f%%) CER: %d (%0.2f%%)]\ndone.\n',...
                                ber, berp, cer, cerp);
                        msg = [];
                    end
                end
                %fprintf('%c', message(2));
                %plen = uint32(message(length(preamble) + 1));
                %fprintf('\nlength: %d\n', plen);
                last = data(pos+chipsize*8:end);
%                 break
            end
        end
    end
 end