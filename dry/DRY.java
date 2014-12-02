/** DRY.java
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

// package dry;

import japa.parser.ast.CompilationUnit;
import japa.parser.JavaParser;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ASTHelper;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.util.List;
import java.util.Arrays;

// import dry.visitors.*;




public class DRY {

    static boolean comments = false;
    static String filename;
    static String heuristic;
    
    static String[] files = new String[] {"HelloWorld", "HelloWorldWet", "DoubleFor", "DoubleForWet", "Factorial", "FactorialWet", "DivideByZero"};
    static String[] heuristics = new String[] {"ZeroVisitor", "Iteration1Visitor"};
    
    public static void main(String[] args) throws Exception {
        // check for print statements
        if(args.length >= 1 && args[0].equals("-c")) {
            comments = true;
        }
        // parse command line arguments
        parseCommandArgs(args);

        // creates an input stream for the file to be parsed
        String prefix = "samples/";
        String extension = ".java";

        // test the heuristic on the file
        Double dryScore = test(heuristic, filename);
        System.out.println("DRY Score for " + filename + " using heuristic " + heuristic + " is: " + dryScore);



    };

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
        heuristic = args[0];
        if(!Arrays.asList(heuristics).contains(heuristic)) {
            System.err.println("invalid heuristic... " + heuristic);
            System.exit(1);
        }
        // check if we have a valid filename
        filename = args[1];
        if(args.length >= 3 && args[2].equals("-c")) {
            comments = true;
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
        usage += "DRY usage: java DRY <heuristic> <filename> [-c]";
        System.out.println(usage);
    }
    

    /**
     * Runs the DRYness test on a specific Java file, using the given DRYness metric specified in a given Java class
     * @author J. Hassler Thurston
     * CSC200H Research Project
     * Fall 2014
    */
    public static Double test(String className, String file) {
        try {
            // open the file for reading
            FileInputStream in = new FileInputStream(file);

            CompilationUnit cu;
            try {
                // parse the file
                cu = JavaParser.parse(in);
            } finally {
                in.close();
            }

            // if the above has successfully completed, try to evaluate the DRYness of file using the given heuristic.

            // get the class name from the string
            // http://stackoverflow.com/questions/4767088/creating-an-instance-from-string-in-java
            Class c = Class.forName(className);
            // http://stackoverflow.com/questions/4386870/creating-new-instance-from-class-with-constructor-parameter
            GenericVisitor<Double, Object> visitor = (GenericVisitor<Double, Object>) c.newInstance();
            // visitor.setComments(comments);
            // run the test and print out the result
            return visitor.visit(cu, null);
        } catch(Exception e) {
            System.err.println("Error: " + className + " is not a valid DRYness metric.");
            // System.exit(0);
        }
        return -1D;
    }
}
