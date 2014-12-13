README.txt
J. Hassler Thurston
CSC 200 Research Project
Fall 2014

Overview of files in dry/ directory:
heuristics/ (overview of these heuristics given in the Report)
	AllPairsNaiveHeuristic.java -- implements the AllPairsNaiveHeuristic
	AllPairsWeightedHeuristic.java -- implements the AllPairsWeightedHeuristic
	Iteration1Heuristic.java -- implements the Iteration1Heuristic
	ModifiedBakerHeuristic.java -- implements the ModifiedBakerHeuristic
	ZeroHeuristic.java -- implements the ZeroHeuristic
samples/ (sample test Java files)
	DivideByZero.java -- an empty Java class, used to test whether our heuristics are well-defined
	DoubleFor.java -- a simple double for loop in a Java class
	DoubleForWet.java -- DoubleFor.java but with the inner for loop unrolled
	Factorial.java -- implementation of the factorial function in Java
	FactorialWet.java -- implementation of the factorial function, with random duplicated expressions
	HelloWorld.java -- contains a for loop that prints out "Hello World!" 10 times
	HelloWorldWet.java -- HelloWorld.java with the for loop unrolled
DRY.java -- main file. Contains a "harness" for testing a heuristic on a file. The heuristic
	is given as the first command-line argument, and the Java file to test is given as the second argument.

Other directories:
japa/: contains implementation code of Javaparser library. This library can be found at https://code.google.com/p/javaparser/.
proposal/: contains a LaTeX and PDF of my proposal for this project
report/: contains a LaTeX and PDF of the final report for this project

Other files in this directory:
HonorsFinalProject.txt -- your description of this project
README.txt -- this file
dryPlot.pdf -- Figure 2 in the Report
testPlot.pdf -- Figure 1 in the Report
first_proposal.txt -- a draft of my first proposal for this project
generate_plots.m -- a MATLAB script to automatically generate PDFs of Figures, given the results in CSV format.
javaparser-1.0.9.jar -- a JAR of the Javaparser library. Must be added to your CLASSPATH in order to use.
results.csv -- a CSV file with labeled results of testing the heuristics on the sample files.
results_nonames.csv -- results.csv but with names of heuristics and files stripped out.
testScript.sh -- runs the dry.DRY program on all heuristics and all test files, and outputs a CSV file (currently set up to output to results_nonames.csv)

Compiling and running instructions:
$ cd ~
$ echo "export CLASSPATH=$CLASSPATH:~/path/to/the/jarfile/javaparser-1.0.9.jar" >> .bashrc
$ cd /path/to/this/directory
$ ./testScript.sh

Please contact me by email at jthurst3@u.rochester.edu if you have trouble compiling and running my code.
