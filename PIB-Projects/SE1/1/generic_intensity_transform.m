% Apresenta as imagens I e It, bem como os respetivos indicadores produzidos
%pela função image_details.m, com It[m, n] = T[I[m, n]], sendo T uma transformação de intensidade
%genérica. A transformação T é realizada através de tabela de lookup

function generic_intensity_transform(filename)
   
close all %%closes all windows (may not be necessary!)

clc %%clean console

if nargin == 0
    filename = 'bird.gif'
end

img = imread(filename);

trueColor = image_details(filename,img)
if(strcmp(trueColor,'truecolor'))
    [I1, I2] = image_lut_colored(filename);
else
    [I1, I2] = image_lut(filename);
end
disp('I1 \n')
image_details(filename,I1);
disp('I2 \n')
image_details(filename,I2);

end