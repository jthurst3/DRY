

Our Java program is defined in \texttt{DRY.java}, and takes command-line arguments specifying which DRYness heuristic to use
and which Java file to test. It also takes an optional argument that allows one to print out the result in verbose mode.

Our heuristics are defined in the files \texttt{*Heuristic.java}, and implement the \texttt{GenericVisitor} class of Javaparser, 
which visits each subtree of the test file's Java parse tree in pre-order. We use the Bash script \texttt{testScript.sh} to run
\texttt{DRY.java} on each test file and each heuristic. Our results are contained within the \texttt{results.csv} file.

The seven different test files are located in the \texttt{samples/} directory. Of these seven files, one contains an empty Java class, which helps to test whether our heuristics are well-defined. The other six are written in pairs,
such that one Java file of the pair is a wetter version of the other. For example, \texttt{HelloWorld.java} contains a for loop
that prints ``Hello world!'' ten times, and \texttt{HelloWorldWet.java} contains ten successive statements, each printing
``Hello World!''.