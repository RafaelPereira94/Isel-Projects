function face_detection( filename )

if nargin == 0
    filename = 'face1.bmp'
end

% Detect objects using Viola-Jones Algorithm
% to detect Face
FDetect = vision.CascadeObjectDetector;

% Read the input image
I = imread(filename);

% Returns Bounding Box values based on number of objects
BB = step(FDetect,I);

figure,
imshow(I); hold on
for i = 1:size(BB,1)
    rectangle('Position',BB(i,:),'LineWidth',5,'LineStyle','-','EdgeColor','r');
end
title('Face Detection');
hold off;
pause(3)

end