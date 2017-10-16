%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% image_spatial_filtering.m
% Função que ilustra a aplicação de operações sobre imagens com níveis de cinzento.
% Análise de exemplos de filtragem espacial passa-baixo e passa-alto.

function image_spatial_filtering(filename)

% Fechar todas as janelas de figuras.
close all;

% Limpar a consola.
clc

if nargin==0
    
end

% Ler a imagem a partir do ficheiro.
I = imread(filename);
%I = imread('bird.gif');
%I = imread('lena.gif');
%I = imread('f-18.bmp');
%I = imread('flowers.bmp');

% Obter as dimensões (resolução da imagem).
[M, N] = size(I);

% Imprimir mensagem com as dimensões e resolução da imagem.
fprintf('Image with %d x %d = %d pixels\n', M, N, M*N);

% Filtro 1
% Filtro passa-baixo de 3x3
k1 = (1/9) * ones(3,3);
I1 = filter2(k1, I);
figure(1); set(gcf,'Name', 'Filter 1');
subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
subplot(122); imagesc(I1); axis tight; title(' Image 1 (3x3)' );
impixelinfo;

% Filtro 2
% Filtro passa-baixo de 5x5
k2 = (1/25) * ones(5,5);
I2 = filter2(k2, I);
figure(2); set(gcf,'Name', 'Filter 2');
subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
subplot(122); imagesc(I2); axis tight; title(' Image 2 (5x5)' );
impixelinfo;

% Filtro 3
% Filtro passa-baixo de 7x7
k3 = (1/49) * ones(7,7);
I3 = filter2(k3, I);
figure(3); set(gcf,'Name', 'Filter 3');
subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
subplot(122); imagesc(I3); axis tight; title(' Image 3 (7x7)' );
impixelinfo;

% I = imread('squares.gif');
% % Filtro 4
% % Filtro de deteção vertical.
% k4 = [1  -1];
% I4 = filter2(k4, I);
% figure(4); set(gcf,'Name', 'Filter 4');
% subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
% subplot(122); imagesc(I4); axis tight; title(' Image 4' );
% impixelinfo;
% 
% % Filtro 5
% % Filtro de deteção horizontal.
% k5 = [1  -1]';
% I5 = filter2(k5, I);
% figure(5); set(gcf,'Name', 'Filter 5');
% subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
% subplot(122); imagesc(I5); axis tight; title(' Image 5' ); colorbar;
% impixelinfo;
% 
% % Filtro 6
% % Filtro de deteção vertical e horizontal.
% k6 = [1  -1;
%          -1   1];
% I6 = filter2(k6, I);
% figure(6); set(gcf,'Name', 'Filter 6');
% subplot(121); imagesc(I); title(' Image ' ); colormap('gray');
% subplot(122); imagesc(I6); axis tight; title(' Image 6' ); %colorbar;
% impixelinfo; 
end

