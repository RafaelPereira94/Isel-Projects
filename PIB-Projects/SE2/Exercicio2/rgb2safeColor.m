function res = rgb2safeColor(criteria,image)

    close all;
    clc;
    
    img = imread(image);
    
    s=size(img);

    webimg=zeros([s(1) s(2) s(3)]);
    
    %default websafest color
    if(strcmp(criteria,'safest'))
        
        red =[0,0,0,0,0,0,0,51,51,51,51,102,102,102,204,255,255,255,255,255,255,255];

        green =[0,0,0,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,255,255,255,255];

        blue =[0,51,255,0,102,204,255,51,102,204,255,0,51,255,102,0,51,255,0,51,102,255];
        
        % distancia eucliadiana  d=sqrt((r2-r1)^2+(g2-g1)^2+(b2-b1)^2)
        for i=1: size(img,1) 
            for j=1:size(img,2) 
                % get pixel value
                pixelR=img(i,j,1);
                pixelG=img(i,j,2);
                pixelB=img(i,j,3);

                aux = [1:1:22]; 
                % calculate all distance
                for c=1:length(red)
                    r= sqrt(double(double((pixelR-red(c)))^2+double((pixelG-green(c)))^2+double((pixelB-blue(c))^2)));
                    aux(c) = r;
                end
                [M,I] = min(aux);
                
                % put pixel on output image
                webimg(i,j,1) = red(I);
                webimg(i,j,2) = green(I);
                webimg(i,j,3) = blue(I);
                
            end
        end
    elseif (strcmp(criteria,'safe'))
        
        webimg(:,:,:)= and(double(img(:,:,:)>=42),double(img(:,:,:)<=84))*51.0; % se tiver valor maior que 42 e menor que 84 fico valor 51

        webimg(:,:,:)= and(double(img(:,:,:)>=85),double(img(:,:,:)<=127))*102.0+webimg;

        webimg(:,:,:)= and(double(img(:,:,:)>=128),double(img(:,:,:)<=171))*153.0+webimg;

        webimg(:,:,:)= and(double(img(:,:,:)>=172),double(img(:,:,:)<=212))*204.0+webimg;

        webimg(:,:,:)= double(img(:,:,:)>=213)*255.0+webimg;

    else
       error('Unknown criteria!!! try to use safe or safest') 
    end
    
    colorsR = [];
    b=0;
    % calculate different colors on output image
    for i=1: size(webimg,1) 
        for j=1: size(webimg,2)
            pixelR=webimg(i,j,1);
            pixelG=webimg(i,j,2);
            pixelB=webimg(i,j,3);
            [l,c] = size(colorsR);
            if(l>0)
                for aux=1:l
                    [l,c] = size(colorsR);
                    for rgb=1:l
                        if(colorsR(rgb,1) == pixelR & colorsR(rgb,2) == pixelG & colorsR(rgb,3) == pixelB)
                            b = 1; % existe esta cor
                            break;
                        end
                    end
                    if(~b)
                        colorsR = [colorsR; [pixelR pixelG pixelB] ];
                    end
                    b=0;
                end
            else
                colorsR = [colorsR; [pixelR pixelG pixelB] ];
            end
        end
    end

    [res,c] = size(colorsR);
    
    webimg=uint8(webimg);
    imshowpair(img, webimg, 'montage')
end