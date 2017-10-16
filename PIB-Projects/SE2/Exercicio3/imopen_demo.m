%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% imopen_demo.m
% Demonstração da operação de abertura (opening) sobre imagens com níveis
% de cinzento.
%

function imopen_demo(filename)

% Apagar o texto da consola.
clc

% Ler a imagem.
%originalI = imread('snowflakes.png');
originalI = imread(filename); 

% Definir o elemento estruturante 'disk' (círculo).
radius = 2;
se = strel('disk',radius);        

% Mostrar o elemento estruturante.
se.disp 

% Aumentar progressivamente o raio do elemento estrurutante.
for radius=2 : 1 : 15 
    
    % Aplicar a abertura com o elemento estruturante atual.
    openI = imopen(originalI,se);
    
    % Mostrar imagem original e após erosão.
    figure(1);
    subplot(121); imshow(originalI); title('Original');
    subplot(122); imshow(openI); title(['Opening (Radius = ', num2str(radius) ' )']);
    pause(2)
    
    % Atualizar o elemento estruturante para maior raio.
    se = strel('disk',radius);    
end
