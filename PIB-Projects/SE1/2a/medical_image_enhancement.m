%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% medical_image_enhancement.m
% Makes transformations measures to improve readability

function medical_image_enhancement( I_name, contrast_in, contrast_out )

% close all figures
close all;

% clear console
clc

if nargin == 0
    I_name = 'PET1.tif';
    
    % default contrast limits
    contrast_in = [];
    contrast_out = [];
end
parts = strsplit(I_name, '.');

% read images grayscale
I_origin = imread(I_name);

I_adj_name = strcat(parts(1),'Adj.',parts(2));
% Adjust them in our own perspective
I_adj = imadjust(I_origin, contrast_in, contrast_out); 

% Save them on this directory
imwrite(I_adj,strjoin(I_adj_name));             

% apply images
figure(1); set(gcf,'Name', 'Original and transformed image');
subplot(121); imshow(I_origin); title(I_name);
subplot(122); imshow(I_adj); title(I_adj_name);
impixelinfo;
pause(3)

end

