%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% imerode_demo.m
% Demonstração da operação de erosão sobre imagens binárias.
%

function imerode_demo(filename)

% Apagar o texto da consola.
clc

% Ler a imagem binária.
originalBW = imread(filename);  

% Definir o elemento estruturante 'disk' (círculo).
radius = 2;
se = strel('disk',radius);        

% Mostrar o elemento estruturante.
se.disp 

% Aumentar progressivamente o raio do elemento estrurutante.
for radius=2 : 1 : 15
     
    % Aplicar a erosão com o elemento estruturante atual.
    erodedBW = imerode(originalBW,se);
    
    % Mostrar imagem original e após erosão.
    figure(1);
    subplot(121); imshow(originalBW); title('Original');
    subplot(122); imshow(erodedBW); title(['Erosion (Radius = ', num2str(radius) ' )']);
    pause(2)
    
    % Atualizar o elemento estruturante para maior raio.
    se = strel('disk',radius);    
end
