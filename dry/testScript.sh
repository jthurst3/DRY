#!/bin/bash
# Tests all heuristics on all labeled examples
# J. Hassler Thurston
# CSC200H Research Project
# Fall 2014

# Available heuristics for testing
heuristics="ZeroVisitor Iteration1Visitor "
# Available files for testing
files="samples/*.java"
# File to put all the data
resultfile="results.csv"


# Print a header to the results file
echo "Heuristic,`echo $files | sed 's/ /,/'`" > "$resultfile"
# Loop through all the heuristics and files, and test the heuristics on the files
for h in $heuristics; do
	# Print the heuristic name
	echo -n "$h," >> "$resultfile"
	for f in $files; do
		# run the tests
		java DRY "$h" "$f" >> "$resultfile"
	done
	# Print a newline
	echo >> "$resultfile"
done
