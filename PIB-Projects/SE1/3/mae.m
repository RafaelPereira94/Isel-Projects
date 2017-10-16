function [mae] = mae(I1,I2)
A = imread(I1);
B = imread(I2);

absDiff = abs(double(A) - double(B));
mae = mean(absDiff(:));

fprintf('mean absolute error: %d',mae)

end

