function codeCardGenerator( M, N )

A = floor(rand(M,N)*10)
colors = ['y','m', 'c', 'r', 'g', 'b', 'k']

blankimage = ones(M*30,N*30);
blankimage(:,:,1) = 1;
imshow(blankimage)

for i=1:M
    for j=1:N
        t=text(j*30-20,i*30-20,num2str(A(i,j)));
        t.FontSize = 10;
        t.Color = colors(randi(length(colors),1,1))
    end
end

end

