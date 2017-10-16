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

function [I1, I2] = image_lut( filename )

% Fechar todas as janelas de figuras.
%close all;

% Limpar a consola.
%clc 

% Ler a imagem a partir do ficheiro.
%I = imread('camera.gif');
I = imread(filename);

%I = imread('bird.gif');
%I = imread('lena.gif');
%I = imread('f-18.bmp');
%I = imread('flowers.bmp');

% Lançar nova janela de figura e mostrar a imagem em níveis de cinzento
% e o respetivo histograma.

% Versão negativa da imagem - tabela de lookup.
LUT1 = uint8(255 : -1 : 0);

% Aplicar a tabela de lookup.
I1 = intlut(I, LUT1);

figure(2); set(gcf,'Name', 'Imagem negativa');
subplot(221); imshow(I); colorbar; title(' Imagem ' );
subplot(222); imshow(I1); colorbar; title(' Imagem negativa' );
subplot(223); imhist(I); title(' Histograma da Imagem ' );
subplot(224); imhist(I1); title(' Histograma da Imagem Negativa' );
impixelinfo;

% Lançar nova janela de figura e mostrar a imagem 
% em níveis de cinzento e o respetivo histograma.

% Converter a imagem numa versão de 3 níveis.
LUT2 = uint8([ 100*ones(1,100), 200*ones(1,100), 255*ones(1,56) ]);

% Aplicar a tabela de lookup.
I2 = intlut(I, LUT2);
figure(3); set(gcf,'Name', 'Imagem 2');
subplot(221); imshow(I);  colorbar; title(' Imagem ' );
subplot(222); imshow(I2); colorbar; title(' Imagem 3 níveis' );
subplot(223); imhist(I);  title(' Histograma da Imagem de 3 níveis' );
subplot(224); imhist(I2); title(' Histograma da Imagem de 3 níveis' );
impixelinfo;

% % Converter a imagem numa versão binária.
% for Th = 10 : 10 : 250
%     LUT3 = uint8([ zeros(1,Th), 255*ones(1,256-Th) ]);
% 
%     % Aplicar a tabela de lookup.
%     I3 = intlut(I, LUT3);
%     figure(3); set(gcf,'Name', 'Imagem 3');
%     subplot(221); imshow(I);  colorbar; title(' Imagem ' );
%     subplot(222); imshow(I3); colorbar; 
%     title( [' Imagem binária, Th= ', num2str(Th)] );
%     subplot(223); imhist(I);  title(' Histograma da Imagem binária' );
%     subplot(224); imhist(I3); title(' Histograma da Imagem binária' );
%     impixelinfo;
%     pause(2)
% end

end

