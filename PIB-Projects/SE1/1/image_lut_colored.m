%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% image_lut.m
% Função que ilustra a aplicação de operações sobre 
% imagens com níveis de cinzento.
% Processamento espacial com tabela de lookup

function [I1, I2] = image_lut_colored( filename )

% Fechar todas as janelas de figuras.
%close all;

% Limpar a consola.
%clc 

% Ler a imagem a partir do ficheiro.
%I = imread('camera.gif');
I = imread(filename);

% Lançar nova janela de figura e mostrar a imagem em níveis de cinzento
% e o respetivo histograma.

% Versão negativa da imagem - tabela de lookup.
LUT1 = uint8(255 : -1 : 0);

% Aplicar a tabela de lookup.
I1 = intlut(I, LUT1);

figure(2); set(gcf,'Name', 'Imagem negativa');
subplot(2,6,[1,3]); imshow(I); colorbar; title(' Imagem ' );
subplot(2,6,[4,6]); imshow(I1); colorbar; title(' Imagem negativa' );
subplot(2,6,7); imhist(I(:,:,1));  title('R' );
subplot(2,6,8); imhist(I1(:,:,1)); title('R' );
subplot(2,6,9); imhist(I(:,:,2));  title('G' );
subplot(2,6,10); imhist(I1(:,:,2)); title('G' );
subplot(2,6,11); imhist(I(:,:,3));  title('B' );
subplot(2,6,12); imhist(I1(:,:,3)); title('B' );
impixelinfo;

% Lançar nova janela de figura e mostrar a imagem 
% em níveis de cinzento e o respetivo histograma.

% Converter a imagem numa versão de 3 níveis.
LUT2 = uint8([ 100*ones(1,100), 200*ones(1,100), 255*ones(1,56) ]);

% Aplicar a tabela de lookup.
I2 = intlut(I, LUT2);
figure(3); set(gcf,'Name', 'Imagem 2');
subplot(2,6,[1,3]); imshow(I);  colorbar; title(' Imagem ');
subplot(2,6,[4,6]); imshow(I2); colorbar; title(' Imagem 3 níveis');
subplot(2,6,7); imhist(I(:,:,1));  title('R' );
subplot(2,6,8); imhist(I2(:,:,1)); title('R' );
subplot(2,6,9); imhist(I(:,:,2));  title('G' );
subplot(2,6,10); imhist(I2(:,:,2)); title('G' );
subplot(2,6,11); imhist(I(:,:,3));  title('B' );
subplot(2,6,12); imhist(I2(:,:,3)); title('B' );
impixelinfo;

end