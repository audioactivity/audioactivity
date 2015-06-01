%% init (UU is the preamble)
clear all
message  = 'SEEMOO';
message  = 'abcdefghijklmnopqrstuvwxyz';
preamble = 'U3';
eom      = 'U'; % 0x55
Fs       = 48000;
chipsize = 4096/16;
%code     = '0100010010011011010010010111011000010101011100111010101010100110';
message  = strcat(preamble, char(length(message)), message, eom);
bitlen   = length(message) * chipsize * 8;

%% init code - ascii to dec [-1 1]
%code = ((code - 48) * 2) - 1;
%randn('seed', 0);
%code = randn(1, 2^16);
%code = code / max(abs(code));
code = csvread('whitecode.csv');
code = (code - 128) / 128;
code = 2 * (code > 0) - 1;
%code = kron(ones(1, 2), code);
%code = kron(ones(1, 2^16), [1 -1]);

%% convert message to bits and spread symbols to chipsize
bits = reshape(dec2bin(message, 8)', 1, length(message) * 8);
bits = kron(((bits - 48) * 2) - 1, ones(1, chipsize));

%% repeat and fit spreading sequence to match length
codeseq = kron(ones(1, ceil(bitlen / length(code))), code);
codeseq = codeseq(1:bitlen);

%% generate code
tx = [zeros(1, 10 * chipsize) (bits .* codeseq) zeros(1, 10 * chipsize)];
audiowrite(strcat('dsss/dsss-', num2str(chipsize), '.wav'), tx / 40, Fs);