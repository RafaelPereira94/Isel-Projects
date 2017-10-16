function intensity_slicing(filename)

if nargin == 0
    filename = 'bird.gif';
end

grayImage=imread(filename); % should be gray lavel image or byt=1

% get a map of where the pixels with gray level 50 are.
pixels50 = grayImage == 50;
% Create r, g, and b channels.
redImage = grayImage;
greenImage = grayImage;
blueImage = grayImage;
% Change the colors for the pixels that are gray level 50.
redImage(pixels50) = 255;
greenImage(pixels50) = 0;
blueImage(pixels50) = 0;
% Combine into a new RGB image.
rgbImage = cat(3, redImage, greenImage, blueImage);

imshow(rgbImage);

end

