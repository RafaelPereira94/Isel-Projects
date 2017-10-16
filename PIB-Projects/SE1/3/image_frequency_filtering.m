%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% image_frequency_filtering.m
% Frequency filtering of images.

function image_frequency_filtering(Ofilename, filename, filter)
 
close all

% Read test image
%I = imread('bird.gif');
Io = imread(Ofilename);
I = imread(filename);


if strcmp(filter,'low-pass') == 1
    
    % Apply a low-pass filter with different radius
    %for r = 10 : 10 : 140
        If = frequency_filtering_low_pass( Io, I, 40 );
    %    pause(0.5);
    %end
else
    % Apply a high-pass filter with different radius
    %for r = 10 : 10 : 140
        If = frequency_filtering_high_pass( Io, I, 40 );
    %    pause(0.5);
    %end
end
    imwrite(If,'ImagemFinal.bmp'); 
    image_details('ImagemFinal.bmp', If);
end

function If = frequency_filtering_low_pass( Io, I, radius )

% Step 1 - Zero-padded image to resolution P=2M and Q=2N.
Iop = [ Io, zeros(size(Io,1), size(Io,2));
       zeros(size(Io,1), size(Io,2)), zeros(size(Io,1), size(Io,2))];
Iop = uint8(Iop);
Po = size(Iop,1);
Qo = size(Iop,2);
    
% Step 1 - Zero-padded image to resolution P=2M and Q=2N.
Ip = [ I, zeros(size(I,1), size(I,2));
       zeros(size(I,1), size(I,2)), zeros(size(I,1), size(I,2))];
Ip = uint8(Ip);
P = size(Ip,1);
Q = size(Ip,2);

% Step 2 - Compute the DFT of the padded image and center its spectrum.
Fop = fftshift(fft2(Iop));
Fp = fftshift(fft2(Ip));

% % Step 3 - Set the filter mask H, on the frequency domain.
% % In this case, it is a low-pass filter.
H = Fp - Fop;
H = uint8(real(H));
for m=1:P
    for n=1:Q
        if(H(m,n) <= radius)
            H(m,n) = 1;
        else
            H(m,n) = 0;
        end
    end
end

% Step 4 - Multiply the spectrum of the padded image
% with the filter mask H.
G = Fp .* double(H);

% Step 5 - Perform the inverse DFT (with centered spectrum).
% Remove residual imaginary parts and convert to unsigned integer.
If = ifft2 ( ifftshift(G) );
If = uint8(real(If));


% Analysis of the main steps of the algorithm.
figure(1); 
subplot(221); imshow(Ip); title(' Padded image');
subplot(222); imagesc(abs(Fp)); title(' Spectrum of the padded image');
subplot(223); imshow(H); title(' Spectrum of H ');
subplot(224); imshow(If); title(' Padded filtered image');

If = uint8(If( 1:size(I,1), 1:size(I,2)));
figure(2); 
subplot(121); imshow(I);  title(' Original ');
subplot(122); imshow(If); title( sprintf(' Filtered (radius=%d)',radius) );
end



function If = frequency_filtering_high_pass( Io, I, radius )

% Step 1 - Zero-padded image to resolution P=2M and Q=2N.
Iop = [ Io, zeros(size(Io,1), size(Io,2));
       zeros(size(Io,1), size(Io,2)), zeros(size(Io,1), size(Io,2))];
Iop = uint8(Iop);
Po = size(Iop,1);
Qo = size(Iop,2);

Ip = [ I, zeros(size(I,1), size(I,2));
       zeros(size(I,1), size(I,2)), zeros(size(I,1), size(I,2))];
Ip = uint8(Ip);
P = size(Ip,1);
Q = size(Ip,2);

% Step 2 - Compute the DFT of the padded image and center its spectrum.
Fop = fftshift(fft2(Iop));
Fp = fftshift(fft2(Ip));

% Step 3 - Set the filter mask H, on the frequency domain.
% In this case, it is a high-pass filter.
H = Fp - Fop;
H = uint8(real(H));
for m=1 : P
    for n=1 : Q
        if(H(m,n) > radius)
            H(m,n) = 1;
        else
            H(m,n) = 0;
        end
    end
end

% Step 4 - Multiply the spectrum of the padded image
% with the filter mask H.
G = Fp .* double(H);

% Step 5 - Perform the inverse DFT (with centered spectrum).
% Remove residual imaginary parts and convert to unsigned integer.
If = ifft2 ( ifftshift(G) ) ;
If = uint8(real(If));


% Analysis of the main steps of the algorithm.
figure(1); 
subplot(221); imshow(Ip); title(' Padded image');
subplot(222); imagesc(abs(Fp)); title(' Spectrum of the padded image');
subplot(223); imshow(H); title(' Spectrum of H ');
subplot(224); imshow(If); title(' Padded filtered image');

If = uint8(If( 1:size(I,1), 1:size(I,2)));
figure(2); 
subplot(121); imshow(I);  title(' Original ');
subplot(122); imshow(If); title( sprintf(' Filtered (radius=%d)',radius) );
end