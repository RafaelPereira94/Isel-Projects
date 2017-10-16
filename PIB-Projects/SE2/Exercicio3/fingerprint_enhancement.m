%
% ISEL - Instituto Superior de Engenharia de Lisboa.
%
% LEIC - Licenciatura em Engenharia Informatica e de Computadores.
% MEIC - Mestrado em Engenharia Informatica e de Computadores.
%
% PIB - Processamento de Imagem e Biometria.
%
% fingerprint_enhancement.m
% 
% Function that illustrates the application of operations on images with gray levels.
% Spatial processing.

function J = fingerprint_enhancement( I_name , c)

    I=imread(I_name);
    
%     % Adjust them in our own perspective
%     I_adj = imadjust(I, contrast_in, contrast_out);
    imshow(I)
%     imshowpair(I,I_adj, 'montage');
    
    set(gcf,'position',[1 1 600 600]);
    
    J=I(:,:,1)>c;
    imshow(J);
    set(gcf,'position',[1 1 600 600])

end

