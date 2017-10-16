function trueColor = image_details(filename, img)

%close all %%closes all windows (may not be necessary!)

%clc %%clean console
if nargin == 0
    filename = 'bird.gif';
    img = imread('bird.gif'); %%leitura das várias imagens...
end

info = imfinfo(filename);

%%resolução espacial
reSpace = info(1).Width * info(1).Height;
fprintf('Resolução espacial é %d\n',reSpace);

%%resolução profundidade
resDeep = info.BitDepth;
fprintf('Resolução Profundidade é %d bits por pixel \n',resDeep);

trueColor = info(1).ColorType;
if(strcmp(trueColor,'truecolor'))
    fprintf('Valores para cor Red:\n');
    image_details_partial(filename,img(:,:,1))
    
    fprintf('Valores para cor Green:\n');
    image_details_partial(filename,img(:,:,2))
    
    fprintf('Valores para cor Blue:\n');
    image_details_partial(filename,img(:,:,3)) 
    
    %%apresentar imagem e seu histograma
    figure(1);
    subplot(2,3,[1,3]); imshow(img); colorbar; title(filename);
    subplot(2,3,4); imhist(img(:,:,1)); colorbar; title(' Hist R ' );
    subplot(2,3,5); imhist(img(:,:,2)); colorbar; title(' Hist G ' );
    subplot(2,3,6); imhist(img(:,:,3)); colorbar; title(' Hist B ' );
    impixelinfo;
    pause(2)
    
else
    image_details_partial(filename,img)
    
    %%apresentar imagem e seu histograma
    figure(1);
    subplot(211); imshow(img); colorbar; title(filename);
    subplot(212); imhist(img); colorbar; title(' Hist ' );
    impixelinfo;
    pause(2)
end

end

function image_details_partial(filename,array)
    minV= min(min(array));
    fprintf('Valor minimo é %d\n',minV);

    mean = mean2(array); %%falar do mean2 no relatorio
    fprintf('Valor médio é %.3f \n',mean);

    maxV= max(max(array));
    fprintf('Valor máximo é %d\n',maxV); 
    
    aux = double((maxV+1)/(minV+1));
    %%medida contraste 
    contrast = 20*log10(aux);
    fprintf('Valor contraste é %.3f\n',contrast);

    %%medida entropia
    entr = entropy(array);
    fprintf('Entropia da imagem %.3f\n\n',entr);
end
