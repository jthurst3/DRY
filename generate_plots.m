% generate_plots.m
% given a CSV file with output from our DRYness heuristics,
% generate a bunch of multi-bar graphs,
% sorted by the expected general output of the heuristics
% 
% J. Hassler Thurston
% CSC200 Research Project
% Fall 2014

% load the CSV data from the file
data = csvread('results_nonames.csv');

% swap the rows of the matrix to sort by heuristic
dataSwapArray = [1,5,2,3,4]; % (put the Baker heuristic second)
data = data(dataSwapArray,:);

% separate the data into the actual test files
% and our own dryness files
numTestFiles = 7;

testData = data(:,1:numTestFiles);
dryData = data(:,numTestFiles+1:end);
dryData = dryData(:,1:length(dryData)-1);


% swap the columns of the matrices so that
% the test and dry files are sorted by the 
% expected general output of a DRYness heuristic
testSwapArray = [1,2,4,6,3,5,7];
drySwapArray = [3,1,2,4,5,6];

% swap the elements column-wise
testData = testData(:,testSwapArray);
dryData = dryData(:,drySwapArray);

% PLOTTING
% make the test bar plot
testPlot = bar(testData);
% set the title
title('Test Data Results');
xlabel('Java files');
ylabel('DRYness score');
saveas(testPlot, 'testPlot', 'pdf');

% make the dry bar plot
dryPlot = bar(dryData);
% set the title
title('Results on Our Implementation');
xlabel('Java files');
ylabel('DRYness score');
saveas(dryPlot, 'dryPlot', 'pdf');


