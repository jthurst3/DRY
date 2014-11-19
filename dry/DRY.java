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
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.util.List;

// import dry.visitors.*;




public class DRY {

    static boolean comments = false;
	 public static void main(String[] args) throws Exception {
        // check for print statements
        if(args.length >= 1 && args[0].equals("-c")) {
            comments = true;
        }

        // creates an input stream for the file to be parsed
        String prefix = "samples/";
        String extension = ".java";
        String[] files = new String[] {"HelloWorld", "HelloWorldWet", "DoubleFor", "DoubleForWet", "Factorial", "FactorialWet", "DivideByZero"};

        for (int f = 0; f < files.length; f++) {
            FileInputStream in = new FileInputStream(prefix + files[f] + extension);

            CompilationUnit cu;
            try {
                // parse the file
                cu = JavaParser.parse(in);
            } finally {
                in.close();
            }

            // run the DRY heuristic
            Double dryScore = new Iteration1Visitor(comments).visit(cu, null);
            System.out.println("DRY Score for " + files[f] + " is: " + dryScore);
            
        }


        // changeMethods(cu);
        // System.out.println(cu.getData());

        // prints the resulting compilation unit to default system output
        // System.out.println(cu.toString());

        // visit and print the methods names
        // new MethodVisitor().visit(cu, null);
    }




}