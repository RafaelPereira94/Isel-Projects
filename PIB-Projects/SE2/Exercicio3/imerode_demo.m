%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% imerode_demo.m
% Demonstra��o da opera��o de eros�o sobre imagens bin�rias.
%

function imerode_demo(filename)

% Apagar o texto da consola.
clc

% Ler a imagem bin�ria.
originalBW = imread(filename);  

% Definir o elemento estruturante 'disk' (c�rculo).
radius = 2;
se = strel('disk',radius);        

% Mostrar o elemento estruturante.
se.disp 

% Aumentar progressivamente o raio do elemento estrurutante.
for radius=2 : 1 : 15
     
    % Aplicar a eros�o com o elemento estruturante atual.
    erodedBW = imerode(originalBW,se);
    
    % Mostrar imagem original e ap�s eros�o.
    figure(1);
    subplot(121); imshow(originalBW); title('Original');
    subplot(122); imshow(erodedBW); title(['Erosion (Radius = ', num2str(radius) ' )']);
    pause(2)
    
    % Atualizar o elemento estruturante para maior raio.
    se = strel('disk',radius);    
end
