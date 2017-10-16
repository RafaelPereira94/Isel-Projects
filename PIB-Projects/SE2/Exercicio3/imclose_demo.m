%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% imclose_demo.m
% Demonstra��o da opera��o de fecho (closing) sobre imagens com n�veis
% de cinzento.
%

function imclose_demo(filename)

% Apagar o texto da consola.
clc

% Ler a imagem.
%originalI = imread('snowflakes.png');
originalI = imread(filename); 

% Definir o elemento estruturante 'disk' (c�rculo).
radius = 2;
se = strel('disk',radius);        

% Mostrar o elemento estruturante.
se.disp 
  
% Aumentar progressivamente o raio do elemento estrurutante.
for radius=2 : 1 : 15
    
    % Aplicar o fecho com o elemento estruturante atual.
    closeI= imclose(originalI,se);
    
    % Mostrar imagem original e ap�s eros�o.
    figure(1);
    subplot(121); imshow(originalI); title('Original');
    subplot(122); imshow(closeI); title(['Closing (Radius = ', num2str(radius) ' )']);
    pause(2)
    
    % Atualizar o elemento estruturante para maior raio.
    se = strel('disk',radius);    
end
