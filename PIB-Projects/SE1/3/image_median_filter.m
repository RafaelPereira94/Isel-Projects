%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% image_median_filter.m
% Função que ilustra a aplicação de operações sobre imagens com níveis de cinzento.
% Comparação do uso do filtro de média com o filtro de mediana.

function image_median_filter(filename)

% Fechar todas as janelas de figuras.
close all;

% Limpar a consola.
clc

if nargin == 0
    filename = 'face1_5.bmp'
end

I   = imread(filename);
In = imnoise(I,'salt & pepper',0.05);
%In = imnoise(I,'gaussian',0.02);

% Filtro de média de 3x3
L = 3;
k1 = (1/L^2) * ones(L,L);
I_mean = filter2(k1, In);

% Filtro de mediana numa janela de 3x3
%B = medfilt2(A) performs median filtering of the matrix A using the default 3-by-3 neighborhood.
I_median = medfilt2(In);
figure(1);
subplot(221); imshow(I); title('Original');
subplot(222); imshow(In); title('With noise (salt & pepper');
subplot(223); imshow(uint8(I_mean)); title('Average (mean) (3x3)');
subplot(224); imshow(I_median); title('Median (3x3)');

imwrite(I_median,'ImagemFinal.bmp'); 
% image_details('ImagemFinal.bmp', I_median);

end



