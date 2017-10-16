function intensity_to_RGB_transform(gim, map, n)

if nargin==0
    gim = 'bird.gif';
    map = 'jet';
    n = 256;
end

if ( ~IsValidColormap(map) )
    disp('Error in ImGray2Pseudocolor: unknown colormap!');
elseif (~(round(n) == n) || (n < 0))
    disp('Error in ImGray2Pseudocolor: non-integer or non-positive colormap size');
else
    %READ AN INPUT IMAGE
    A = imread(gim);
    
    %PRE-ALLOCATE A MATRIX
    Output = zeros([size(A,1) size(A,2) 3]);

    %Define a colormap
    fh = str2func(ExactMapName(map));
    map = colormap(fh(n));

    %Assign the columns to 1-D RED,GREEN and BLUE
    Red = map(:,1);
    Green = map(:,2);
    Blue = map(:,3);

    %MAP THE COLORS BASED ON THE INTENSITY OF THE IMAGE
    Output(:,:,1) = Red(A);
    Output(:,:,2) = Green(A);
    Output(:,:,3) = Blue(A);

    Output = im2uint8(Output);
    %DISPLAY THE IMAGE
    imshow(Output);

    %Save the image in PNG or JPEG format
    imwrite(Output,'pseudo_color.jpg');
end

function y = IsValidColormap(map)

y = strncmpi(map,'jet',length(map)) | strncmpi(map,'hsv',length(map)) |...
    strncmpi(map,'hot',length(map)) | strncmpi(map,'cool',length(map)) |...
    strncmpi(map,'spring',length(map)) | strncmpi(map,'summer',length(map)) |...
    strncmpi(map,'autumn',length(map)) | strncmpi(map,'winter',length(map)) |...
    strncmpi(map,'gray',length(map)) | strncmpi(map,'bone',length(map)) |...
    strncmpi(map,'copper',length(map)) | strncmpi(map,'pink',length(map)) |...
    strncmpi(map,'lines',length(map));

function emapname = ExactMapName(map)

if strncmpi(map,'jet',length(map))
    emapname = 'jet';
elseif strncmpi(map,'hsv',length(map))
    emapname = 'hsv';
elseif strncmpi(map,'hot',length(map))
    emapname = 'hot';
elseif strncmpi(map,'cool',length(map))
    emapname = 'cool';
elseif strncmpi(map,'spring',length(map))
    emapname = 'spring';
elseif strncmpi(map,'summer',length(map))
    emapname = 'summer';
elseif strncmpi(map,'autumn',length(map))
    emapname = 'autumn';
elseif strncmpi(map,'winter',length(map))
    emapname = 'winter';
elseif strncmpi(map,'gray',length(map))
    emapname = 'gray';
elseif strncmpi(map,'bone',length(map))
    emapname = 'bone';
elseif strncmpi(map,'copper',length(map))
    emapname = 'copper';
elseif strncmpi(map,'pink',length(map))
    emapname = 'pink';
elseif strncmpi(map,'lines',length(map))
    emapname = 'lines';
end
