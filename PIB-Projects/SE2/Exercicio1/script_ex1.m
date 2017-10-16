%--------------------------------------------------------------------------
% Nome do programa/função: PIB - test
%
% 1.
%
% Autores: 
% Rafael Pereira, 40681
% Renato Junior, 40683
%          
% Data: 10 de Julho de 2017
%--------------------------------------------------------------------------

% medical_image_enhancement( 'CT1.jpg', [0.1 0.8] , [0.1 1])
% intensity_to_RGB_transform('CT1Adj.jpg','hsv',256);

% medical_image_enhancement( 'XRay1.tif', [0.1 0.8] , [0.05 0.9])
% intensity_to_RGB_transform('XRay1Adj.tif','jet',256);

% medical_image_enhancement( 'MR1.jpg', [0.1 0.8] , [0.1 1])
% intensity_to_RGB_transform('MR1Adj.jpg','jet',256);

% medical_image_enhancement( 'PET1.tif', [0.1 0.9] , [0.1 1])
% intensity_to_RGB_transform('PET1Adj.tif','jet',256);

medical_image_enhancement( 'US1.tif', [0 0.8] , [0.2 1])
intensity_to_RGB_transform('US1Adj.tif','jet',256);

% medical_image_enhancement( 'XRay2.tif', [0.1 1] , []);
% intensity_to_RGB_transform('XRay2Adj.tif','jet',256);
