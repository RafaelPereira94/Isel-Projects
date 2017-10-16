%%https://cw.fel.cvut.cz/wiki/_media/courses/a6m33bio/5.fingerprints.pdf
%%pagina 21
function fingerprint_enhancement_morph(image)

    I = imread(image); 
    
    level = graythresh(I)
    
    im = im2bw(I, level);
   
    im = ~im;
   
    imshow(im);
    
    se = [1,1,1]; %elemento estruturante matriz so com 1's
    
    % im1 = imerode(im,se); %A - se

    im2 = imdilate(im,se); %(A-se)+se
    
    im3 = imdilate(im2,se); %((A-se)+se)+se)

    im4 = imerode(im3,se); %(((A-se)+se)+se)-se)

    im4 = bwmorph(im,'skel');
    
    imshow(im4); 
    
    imwrite(im4,strcat('output',image));
end

