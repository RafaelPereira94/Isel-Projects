%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% image_sharpen.m
% Função que ilustra a aplicação de operações sobre imagens com níveis de cinzento.

function image_sharpen(filename, amount)

% Fechar todas as janelas de figuras.
close all; 

% Limpar a consola.
clc
I    = imread(filename);
L   = 7;
w  = ones(L,L) / L^2;
Ib  = filter2(w,I);
Ib  = uint8(Ib);
imwrite(Ib,'bird_blurred.png','png');

% Sharpen image using unsharp masking
Iu1 = imsharpen(Ib);
Iu2 = imsharpen(Ib,'radius',2.5,'Amount',amount);

figure(1);
subplot(221); imshow(I); title('Original');
subplot(222); imshow(Ib); title('Iblurred');
subplot(223); imshow(Iu1); title('Isharpened');
subplot(224); imshow(Iu2); title('Isharpened 2');

imwrite(Iu2,'ImagemFinal.bmp'); 
% image_details('ImagemFinal.bmp', Iu2);

end

function y = T1(x)
x = x.data;
y = fft2(x);
y(1,1) = 0;
y = uint8(real(ifft2(y)));
end

function y = T2(x)
x = x.data;
y = fft2(x);
y( 2:6, 2:6 ) = 0;
y = uint8(real(ifft2(y)));
end

function y = T3(x)
x = x.data;
y = dct2(x);
y(2:8,2:8) = 0;
y = uint8(idct2(y));
end

