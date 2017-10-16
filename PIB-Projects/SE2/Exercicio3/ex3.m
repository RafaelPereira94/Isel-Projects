%--------------------------------------------------------------------------
% Nome do programa/função: PIB - test
%
% 3.
%
% Autores: 
% Rafael Pereira, 40681
% Renato Junior, 40683
%          
% Data: 10 de Julho de 2017
%--------------------------------------------------------------------------

% 3.a)
% fingerprint_enhancement_morph('finger1.tif');

% 3.c)
J = fingerprint_enhancement( 'finger1.tif', 200);
minutiae_detection( imread('finger1.tif'), J );
% 
% J = fingerprint_enhancement( 'finger2.tif', 160);
% minutiae_detection( imread('finger2.tif'), J );
% 
% J = fingerprint_enhancement( 'finger3.tif', 220);
% minutiae_detection( imread('finger3.tif'), J);
% 
% J = fingerprint_enhancement( 'finger4.tif', 160, [], []);
% minutiae_detection( imread('finger4.tif'), J );
% 
% J = fingerprint_enhancement( 'finger5.bmp', 135, [] , []);
% minutiae_detection( imread('finger5.bmp'), J );
