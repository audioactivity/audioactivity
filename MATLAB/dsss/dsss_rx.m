%% init
clear all
Fs       = 48000;
chipsize = 4096/16;
expected = 'SEEMOO';
expected = 'abcdefghijklmnopqrstuvwxyz';
preamble = 'U3';
eom      = 'U'; % 0x55

%% setup code
%code     = '0100010010011011010010010111011000010101011100111010101010100110';
%code     = ((code - 48) * 2) - 1;
%randn('seed', 0);
%code = randn(1, 2^16);
%code = code / max(abs(code));
code = csvread('whitecode.csv');
code = (code - 128) / 128;
code = 2 * (code > 0) - 1;
%code = kron(ones(1, 2), code);
%code = kron(ones(1, 2^16), [1 -1]);
%code =  code > 0;
%invcode = 1 ./ code;
%invcode(abs(invcode)==Inf) = 0;
invcode = code;
%code = 1 ./ code;

%% setup
bitlen   = length(preamble) * 8 * chipsize;

codeseq  = kron(ones(1, ceil(bitlen / length(code))), code);
codeseq  = codeseq(1:bitlen);
preambleseq = reshape(dec2bin(preamble, 8)', 1, length(preamble) * 8);
preambleseq = ((preambleseq - 48) * 2) -1;
preambleseq = kron(preambleseq, ones(1, chipsize));
sync        = preambleseq .* codeseq;

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
while true
    y  = step(rec)';
    
    if plen > 0
        last = [last y];

        if length(last) < (chipsize * (plen + length(preamble) + 2) * 8)
            continue
        end
        
%         break
        
        data = last;
        blocks = floor(length(data) / chipsize);
        rx = data(1:blocks * chipsize);
        repeats = ceil((blocks * chipsize) / length(code));
        codeseq = kron(ones(1, repeats), code);
        codeseq = codeseq(1:length(rx));

        z = reshape(rx .* codeseq, chipsize, blocks);
        %z = round((mean(z) + 1) / 2);
        z =  mean(z) > 0;
        chars = floor(length(z)/8);
        chars = reshape(z(1:chars*8), 8, chars);
        chars = chars(:,length(preamble) + 2:end-1);
        
         
        message = char(bi2de(chars', 'left-msb'))';
        [cer, ber, cerp, berp] = geterrors(message, expected);
        fprintf('%s [BER: %d (%0.2f%%) CER: %d (%0.2f%%)]\ndone.\n',...
                message, ber, berp, cer, cerp);
        last = [];
        plen = 0;
    end
    
    if length(last) < (chipsize * (length(preamble) + 2) * 16)
        last = [last y];
        continue;
    end 
    
    data = [last y];
    last = data(length(y) + 1:end);

    pos = findsync(data, sync);

    %plot(x);
    %drawnow;
    
    blocks = floor((length(data) - pos) / chipsize);
    rx = data(pos+1:pos+blocks*chipsize);

    if ~isempty(rx)
        repeats = ceil((blocks * chipsize) / length(code));
        codeseq = kron(ones(1, repeats), code);
        codeseq = codeseq(1:length(rx));

        z = reshape(rx .* codeseq, chipsize, blocks);
        %z = round((mean(z) + 1) / 2);
        z = mean(z) > 0;
        chars = floor(length(z)/8);
        chars = reshape(z(1:chars*8), 8, chars);

        if ~isempty(chars)
            message = char(bi2de(chars', 'left-msb'))';
            if length(message) >= length(preamble) + 2 && ...
                strcmp(message(1:length(preamble)), preamble)
                plen = uint32(message(length(preamble) + 1));
                fprintf('\nlength: %d\n', plen);
                last = data(pos+1:end);
%                 break
            end
        end
    end
 end