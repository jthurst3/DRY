/**
 * DRY.java
 * Computes the DRY score (a measure of how much
 * a programmer follows "don't repeat yourself")
 * of sample files using the DRYVisitor heuristics
 * 
 * @author J. Hassler Thurston
 * Modified from javaparser by Julio Vilmar Gesser
 * Modified from "How To Use This Parser" at https://code.google.com/p/javaparser/wiki/UsingThisParser
 * 
 * CSC200H Research Project
 * Fall 2014
*/

package dry;

import japa.parser.ast.CompilationUnit;
import japa.parser.JavaParser;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ASTHelper;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import japa.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;

import dry.heuristics.*;




public class DRY {

    // command-line arguments
    static boolean comments = false;
    static boolean verbose = false;
    static String filename;
    static String heuristic;
    
    // available heuristics to use
    static String[] heuristics = new String[] {"ZeroHeuristic", "Iteration1Heuristic", "AllPairsNaiveHeuristic",
        "AllPairsWeightedHeuristic", "ModifiedBakerHeuristic"};
    
    /** 
     * Main method
     * Parses command-line arguments and tests the given DRYness heuristic on a given file
     * DRYness score should be a double in the range [0.0, 1.0].
     *      A DRYness score of 0.0 means the heuristic thinks the file is very dry/modular
     *      A DRYness score of 1.0 means the heuristic thinks the file is very wet/repetitive
     *      A DRYness score not in the given range means there was an error computing DRYness.
     * 
     * @author J. Hassler Thurston
     * CSC200H Research Project
     * Fall 2014
    */
    public static void main(String[] args) throws Exception {
        // parse command line arguments
        parseCommandArgs(args);

        // test the heuristic on the file
        Double dryScore = test(heuristic, filename);
        if (verbose) {
            System.out.println("DRY Score for " + filename + " using heuristic " + heuristic + " is: " + dryScore);
        } else {
            System.out.print(dryScore + ",");
        }
    }

    /**
     * Parses command line arguments
     * Arguments must be of this form: <heuristic> <filename> [-c]
     * @author J. Hassler Thurston
     * CSC200H Research Project
     * Fall 2014
    */
    public static void parseCommandArgs(String[] args) {
        if(args.length < 2) {
            printUsage();
            System.exit(0);
        }
        // check if we have a valid heuristic
        String baseHeuristic = args[0];
        if(!Arrays.asList(heuristics).contains(baseHeuristic)) {
            System.err.println("invalid heuristic... " + baseHeuristic);
            System.exit(1);
        }
        heuristic = "dry.heuristics." + baseHeuristic;
        // check if we have a valid filename
        filename = args[1];
        // parse the other arguments
        for (int i = 2; i < args.length; i++) {
            switch(args[i]) {
                case "-c":
                case "--comments":
                comments = true;
                break;

                case "-v":
                case "--verbose":
                verbose = true;
                break;

                default:
                break;
            }
        }
    }

    /**
     * Prints usage information
     * @author J. Hassler Thurston
     * CSC200H Research Project
     * Fall 2014
    */
    public static void printUsage() {
        String usage = "";
        usage += "DRY usage: java DRY <heuristic> <filename> [-c, --comments] [-v, --verbose]\n";
        usage += "\t-c, --comments: print out debugging comments (currently unused)\n";
        usage += "\t-v, --verbose: print out the result in sentence form. ";
        usage += "Default is to print out the number followed by a comma.";
        System.out.println(usage);
    }
    

    /**
     * Runs the DRYness test on a specific Java file, using the given DRYness metric specified in a given Java class
     * @author J. Hassler Thurston
     * CSC200H Research Project
     * Fall 2014
    */
    public static Double test(String className, String file) {
        Double result = -1D;

        // prepare the heuristic
        GenericVisitor<Double, Object> visitor = null;
        try {
            // get the class name from the string
            // http://stackoverflow.com/questions/4767088/creating-an-instance-from-string-in-java
            Class c = Class.forName(className);

            // create a new instance of the class and cast it to a GenericVisitor
            // http://stackoverflow.com/questions/4386870/creating-new-instance-from-class-with-constructor-parameter
            visitor = (GenericVisitor<Double, Object>) c.newInstance();

            // Set comments to the command-line variable
            // Will be implemented if we can sub-interface GenericVisitor
            // visitor.setComments(comments);

        } catch(Exception e) {
            System.err.println("Error: " + className + " is not a valid DRYness metric.");
            return -1D;
        }
        // prepare the file
        try {
            // open the file for reading
            FileInputStream in = new FileInputStream(file);

            // parse the file
            CompilationUnit cu = JavaParser.parse(in);

            // run the test and print out the result
            result = visitor.visit(cu, null);

            // close the file
            in.close();
        } catch (IOException io) {
            System.err.println("Error reading file: " + file);
            return -1D;
        } catch (ParseException io) {
            System.err.println("Error: " + file + " could not be parsed. Please make sure there are no syntax errors " +
                "by compiling " + file + " with javac.");
            return -1D;
        }
        return result;
    }
}
