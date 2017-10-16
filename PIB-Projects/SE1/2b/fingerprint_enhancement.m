%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% fingerprint_enhancement.m
% 
% Function that illustrates the application of operations on images with gray levels.
% Spatial processing.

function fingerprint_enhancement( I_name )

% close all figures
close all;

% clear console
clc

if nargin == 0
    I_name = 'finger1.tif';
end
parts = strsplit(I_name, '.');

% read image from a file
I = imread(I_name);

% Calculate the optimal threshold to transform the image
% in its binary version (Otsu method).
level = graythresh(I);

% Convert to binary image
IBW = im2bw(I, level);

% Launch new picture window and show images in gray levels
% and binary.
figure(1); set(gcf, 'Name', ['Original e binária - ' num2str(255*level)] );
subplot(121); imshow(I);   colorbar; title(' Imagem ' );
subplot(122); imshow(IBW); colorbar; title(' Imagem Binária' );
impixelinfo;
imwrite(IBW,strjoin(strcat(parts(1),'BW.', parts(2))));

% Negative version and respective binarization.
I = 255 - I;
level = graythresh(I);
IBW = im2bw(I, level);

% Launch new picture window and show images in gray levels
% and binary.
figure(2); set(gcf,'Name', 'Negativa e binária');
subplot(121); imshow(I);   colorbar; title(' Imagem ' );
subplot(122); imshow(IBW); colorbar; title(' Imagem Binária' );
impixelinfo;
imwrite(IBW,strjoin(strcat(parts(1),'BW2.', parts(2))));
pause(3)

end

