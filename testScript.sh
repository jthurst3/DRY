#!/bin/bash
# Tests all heuristics on all labeled examples
# J. Hassler Thurston
# CSC200H Research Project
# Fall 2014


# Available heuristics for testing
heuristics="ZeroHeuristic Iteration1Heuristic AllPairsNaiveHeuristic AllPairsWeightedHeuristic ModifiedBakerHeuristic "
# Available files for testing
files="dry/samples/*.java dry/heuristics/AllPairsWeightedHeuristic.java dry/heuristics/AllPairsNaiveHeuristic.java dry/DRY.java dry/heuristics/Iteration1Heuristic.java dry/heuristics/ModifiedBakerHeuristic.java dry/heuristics/ZeroHeuristic.java "
# File to put all the data
resultfile="results.csv"

# compile Java files
javac -sourcepath dry:dry/heuristics dry/*.java dry/heuristics/*.java

# Print a header to the results file
echo "Heuristic,`echo $files | sed 's/ /,/'`" > "$resultfile"
# Loop through all the heuristics and files, and test the heuristics on the files
for h in $heuristics; do
	# Print the heuristic name
	echo -n "$h," >> "$resultfile"
	for f in $files; do
		# run the tests
		java dry.DRY "$h" "$f" >> "$resultfile"
	done
	# Print a newline
	echo >> "$resultfile"
done

