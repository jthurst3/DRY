/**
 * ModifiedBakerHeuristic.java
 * Modified from Julio Vilmar Gesser's GenericVisitorAdapter
 * to compute dryness scores of nodes
 *
 * For each type of Java expression, we compute the total number of
 * operators and operands. We then divide the number of unique operators
 * and operands by the total number of operators and operands. This will
 * compute a "reverse" DRYness score -- i.e. dry files will get a score
 * close to 1. Thus, taking "1 - score" will give us a valid
 * DRYness score.
 * 
 * @author J. Hassler Thurston
 * Modified from javaparser by Julio Vilmar Gesser
 * Modified from japa.parser.ast.visitor.GenericVisitorAdapter
 * Modified from "The Use of Software Science in Evaluating
 * Modularity Concepts" by Baker et. al. 1979
 * 
 * CSC200H Research Project
 * Fall 2014
*/

package dry.heuristics;

import japa.parser.ast.Node;
import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.GenericVisitor;

import java.util.List;
import java.util.ArrayList;

/**
 * @author J. Hassler Thurston
 */
public class ModifiedBakerHeuristic<A> implements GenericVisitor<Double, A> {

    // lists of expressions are stored here
    static int annotationDeclarationCount;
    static int annotationMemberDeclarationCount;
    static int arrayAccessExprCount;
    static int arrayCreationExprCount;
    static int arrayInitializerExprCount;
    static int assertStmtCount;
    static int assignExprCount;
    static int binaryExprCount;
    static int blockStmtCount;
    static int booleanLiteralExprCount;
    static int breakStmtCount;
    static int castExprCount;
    static int catchClauseCount;
    static int charLiteralExprCount;
    static int classExprCount;
    static int classOrInterfaceDeclarationCount;
    static int classOrInterfaceTypeCount;
    static int compilationUnitCount;
    static int conditionalExprCount;
    static int constructorDeclarationCount;
    static int continueStmtCount;
    static int doStmtCount;
    static int doubleLiteralExprCount;
    static int emptyMemberDeclarationCount;
    static int emptyStmtCount;
    static int emptyTypeDeclarationCount;
    static int enclosedExprCount;
    static int enumConstantDeclarationCount;
    static int enumDeclarationCount;
    static int explicitConstructorInvocationStmtCount;
    static int expressionStmtCount;
    static int fieldAccessExprCount;
    static int fieldDeclarationCount;
    static int foreachStmtCount;
    static int forStmtCount;
    static int ifStmtCount;
    static int importDeclarationCount;
    static int initializerDeclarationCount;
    static int instanceOfExprCount;
    static int integerLiteralExprCount;
    static int integerLiteralMinValueExprCount;
    static int javadocCommentCount;
    static int labeledStmtCount;
    static int longLiteralExprCount;
    static int longLiteralMinValueExprCount;
    static int markerAnnotationExprCount;
    static int memberValuePairCount;
    static int methodCallExprCount;
    static int methodDeclarationCount;
    static int nameExprCount;
    static int normalAnnotationExprCount;
    static int nullLiteralExprCount;
    static int objectCreationExprCount;
    static int packageDeclarationCount;
    static int parameterCount;
    static int primitiveTypeCount;
    static int qualifiedNameExprCount;
    static int referenceTypeCount;
    static int returnStmtCount;
    static int singleMemberAnnotationExprCount;
    static int stringLiteralExprCount;
    static int superExprCount;
    static int switchEntryStmtCount;
    static int switchStmtCount;
    static int synchronizedStmtCount;
    static int thisExprCount;
    static int throwStmtCount;
    static int tryStmtCount;
    static int typeDeclarationStmtCount;
    static int typeParameterCount;
    static int unaryExprCount;
    static int variableDeclarationExprCount;
    static int variableDeclaratorCount;
    static int variableDeclaratorIdCount;
    static int voidTypeCount;
    static int whileStmtCount;
    static int wildcardTypeCount;
    static int blockCommentCount;
    static int lineCommentCount;

    boolean comments = false;
    public ModifiedBakerHeuristic(boolean comments) {
        this.comments = comments;
        // initialize counts of each type of operator/operand
        annotationDeclarationCount = 0;
        annotationMemberDeclarationCount = 0;
        arrayAccessExprCount = 0;
        arrayCreationExprCount = 0;
        arrayInitializerExprCount = 0;
        assertStmtCount = 0;
        assignExprCount = 0;
        binaryExprCount = 0;
        blockStmtCount = 0;
        booleanLiteralExprCount = 0;
        breakStmtCount = 0;
        castExprCount = 0;
        catchClauseCount = 0;
        charLiteralExprCount = 0;
        classExprCount = 0;
        classOrInterfaceDeclarationCount = 0;
        classOrInterfaceTypeCount = 0;
        compilationUnitCount = 0;
        conditionalExprCount = 0;
        constructorDeclarationCount = 0;
        continueStmtCount = 0;
        doStmtCount = 0;
        doubleLiteralExprCount = 0;
        emptyMemberDeclarationCount = 0;
        emptyStmtCount = 0;
        emptyTypeDeclarationCount = 0;
        enclosedExprCount = 0;
        enumConstantDeclarationCount = 0;
        enumDeclarationCount = 0;
        explicitConstructorInvocationStmtCount = 0;
        expressionStmtCount = 0;
        fieldAccessExprCount = 0;
        fieldDeclarationCount = 0;
        foreachStmtCount = 0;
        forStmtCount = 0;
        ifStmtCount = 0;
        importDeclarationCount = 0;
        initializerDeclarationCount = 0;
        instanceOfExprCount = 0;
        integerLiteralExprCount = 0;
        integerLiteralMinValueExprCount = 0;
        javadocCommentCount = 0;
        labeledStmtCount = 0;
        longLiteralExprCount = 0;
        longLiteralMinValueExprCount = 0;
        markerAnnotationExprCount = 0;
        memberValuePairCount = 0;
        methodCallExprCount = 0;
        methodDeclarationCount = 0;
        nameExprCount = 0;
        normalAnnotationExprCount = 0;
        nullLiteralExprCount = 0;
        objectCreationExprCount = 0;
        packageDeclarationCount = 0;
        parameterCount = 0;
        primitiveTypeCount = 0;
        qualifiedNameExprCount = 0;
        referenceTypeCount = 0;
        returnStmtCount = 0;
        singleMemberAnnotationExprCount = 0;
        stringLiteralExprCount = 0;
        superExprCount = 0;
        switchEntryStmtCount = 0;
        switchStmtCount = 0;
        synchronizedStmtCount = 0;
        thisExprCount = 0;
        throwStmtCount = 0;
        tryStmtCount = 0;
        typeDeclarationStmtCount = 0;
        typeParameterCount = 0;
        unaryExprCount = 0;
        variableDeclarationExprCount = 0;
        variableDeclaratorCount = 0;
        variableDeclaratorIdCount = 0;
        voidTypeCount = 0;
        whileStmtCount = 0;
        wildcardTypeCount = 0;
        blockCommentCount = 0;
        lineCommentCount = 0;
    }
    public ModifiedBakerHeuristic() {
        this(false);
    }
    public void setComments(boolean val) {
        this.comments = val;
    }


    // computes the pairwise dryness score from the ArrayList variables
    public Double computeBakerDrynessScore() {
        // get the list of operator and operand nodes
        // NOTE: since each type of parse tree node either
        // represents one operator, one operand, or neither,
        // we can just split the parse tree nodes into those
        // that represent the operators, those that represent
        // the operands, and those that represent neither.
        int[] operatorNodes = new int[] {
            annotationDeclarationCount, arrayAccessExprCount, arrayCreationExprCount,
            arrayInitializerExprCount, assertStmtCount, assignExprCount,
            binaryExprCount, breakStmtCount, castExprCount, catchClauseCount,
            classExprCount, classOrInterfaceDeclarationCount, conditionalExprCount,
            constructorDeclarationCount, continueStmtCount, doStmtCount,
            enumConstantDeclarationCount, explicitConstructorInvocationStmtCount, 
            foreachStmtCount, forStmtCount, ifStmtCount, importDeclarationCount,
            initializerDeclarationCount, instanceOfExprCount, labeledStmtCount,
            markerAnnotationExprCount, memberValuePairCount, normalAnnotationExprCount,
            packageDeclarationCount, primitiveTypeCount, returnStmtCount,
            singleMemberAnnotationExprCount, superExprCount, switchStmtCount,
            switchEntryStmtCount, synchronizedStmtCount, throwStmtCount, thisExprCount,
            tryStmtCount, typeDeclarationStmtCount, typeParameterCount,
            variableDeclarationExprCount, voidTypeCount, whileStmtCount, wildcardTypeCount
        };
        int[] operandNodes = new int[] {
            booleanLiteralExprCount, charLiteralExprCount, classOrInterfaceTypeCount,
            doubleLiteralExprCount, integerLiteralExprCount, integerLiteralMinValueExprCount,
            longLiteralExprCount, longLiteralMinValueExprCount, methodCallExprCount, 
            methodDeclarationCount, nameExprCount, nullLiteralExprCount, objectCreationExprCount,
            parameterCount, qualifiedNameExprCount, referenceTypeCount, stringLiteralExprCount,
            unaryExprCount, variableDeclaratorCount, variableDeclaratorIdCount, 
        };
        // go through the list of counts
        // n1 = number of distinct operators
        int n1 = countNonZero(operatorNodes);
        // n2 = number of distinct operands
        int n2 = countNonZero(operandNodes);
        // N1 = number of operators
        int N1 = sum(operatorNodes);
        // N2 = number of operands
        int N2 = sum(operandNodes);
        // n = n1 + n2
        int n = n1 + n2;
        // N = N1 + N2
        int N = N1 + N2;
        return 1.0D - divide(n, N);
    }



    public Double visit(AnnotationDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "AnnotationDeclaration");
        annotationDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getMembers() != null) {
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, arg);
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(AnnotationMemberDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "AnnotationMemberDeclaration");
        annotationMemberDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        if (n.getDefaultValue() != null) {
            n.getDefaultValue().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(ArrayAccessExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ArrayAccessExpr");
        arrayAccessExprCount++;
        n.getName().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getIndex().accept(this, arg)));
        return n.getIndex().accept(this, arg);
    }

    public Double visit(ArrayCreationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ArrayCreationExpr");
        arrayCreationExprCount++;
        n.getType().accept(this, arg);
        if (n.getDimensions() != null) {
            for (Expression dim : n.getDimensions()) {
                dim.accept(this, arg);
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
            return new Double(0); // stub
        } else {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getInitializer().accept(this, arg)));
            return n.getInitializer().accept(this, arg);
        }
    }

    public Double visit(ArrayInitializerExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ArrayInitializerExpr");
        arrayInitializerExprCount++;
        Double dryValues = new Double(0);
        int i = 0;
        if (n.getValues() != null) {
            for (Expression expr : n.getValues()) {
                dryValues += expr.accept(this, arg);
                i++;
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
        return divide(dryValues,i);
    }

    public Double visit(AssertStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "AssertStmt");
        assertStmtCount++;
        Double check, message = new Double(0);
        check = n.getCheck().accept(this, arg);
        if (n.getMessage() != null) {
            message = n.getMessage().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (message));
        return message; // stub
    }

    public Double visit(AssignExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "AssignExpr");
        assignExprCount++;
        n.getTarget().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getValue().accept(this, arg)));
        return n.getValue().accept(this, arg);
    }

    public Double visit(BinaryExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BinaryExpr");
        binaryExprCount++;
        Double left = n.getLeft().accept(this, arg);
        Double right = n.getRight().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {left, right})));
        return average(new Double[] {left, right});
    }

    public Double visit(BlockStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BlockStmt");
        blockStmtCount++;
        Double equalityCounter = new Double(0);
        int pairs = 0;
        Double dryValues = new Double(0);
        int i = 0;
        if (n.getStmts() != null) {
            List<Statement> stmts = n.getStmts();
            for (int s1 = 0; s1 < stmts.size(); s1++) {
                dryValues += stmts.get(s1).accept(this, arg);
                i++;
                for (int s2 = 0; s2 < s1; s2++) {
                    if (stmts.get(s1).equals(stmts.get(s2))) {
                        equalityCounter = equalityCounter + 1;
                    }
                    pairs++;
                }
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {divide(dryValues,i), divide(equalityCounter,pairs)})));
        return average(new Double[] {divide(dryValues,i), divide(equalityCounter,pairs)});

    }

    public Double visit(BooleanLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BooleanLiteralExpr");
        booleanLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1); // stub
    }

    public Double visit(BreakStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BreakStmt");
        breakStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(CastExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CastExpr");
        castExprCount++;
        n.getType().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(CatchClause n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CatchClause");
        catchClauseCount++;
        Double except = n.getExcept().accept(this, arg);
        Double catchBlock = n.getCatchBlock().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {except, catchBlock})));
        return average(new Double[] {except, catchBlock});

    }

    public Double visit(CharLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CharLiteralExpr");
        charLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ClassExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ClassExpr");
        classExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getType().accept(this, arg)));
        return n.getType().accept(this, arg);
    }

    public Double visit(ClassOrInterfaceDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ClassOrInterfaceDeclaration");
        classOrInterfaceDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }
        if (n.getExtends() != null) {
            for (ClassOrInterfaceType c : n.getExtends()) {
                c.accept(this, arg);
            }
        }

        if (n.getImplements() != null) {
            for (ClassOrInterfaceType c : n.getImplements()) {
                c.accept(this, arg);
            }
        }
        if (n.getMembers() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (BodyDeclaration member : n.getMembers()) {
                dryValues += member.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(ClassOrInterfaceType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ClassOrInterfaceType");
        classOrInterfaceTypeCount++;
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(CompilationUnit n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CompilationUnit");
        compilationUnitCount++;
        Double packageValue = new Double(0), importsValue = new Double(0), typesValue = new Double(0);
        if (n.getPackage() != null) {
            packageValue = n.getPackage().accept(this, arg);
        }
        if (n.getImports() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (ImportDeclaration imp : n.getImports()) {
                dryValues += imp.accept(this, arg);
                i++;
            }
            importsValue = divide(dryValues, i);
        }
        if (n.getTypes() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (TypeDeclaration typeDeclaration : n.getTypes()) {
                dryValues += typeDeclaration.accept(this, arg);
                i++;
            }
            typesValue = divide(dryValues, i);
        }

        // note that this is the first call to visit, so we will call the compute function from here.
        Double dryValue = computeBakerDrynessScore();
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (dryValue));
        return dryValue; // stub
    }

    public Double visit(ConditionalExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ConditionalExpr");
        conditionalExprCount++;
        Double condition = n.getCondition().accept(this, arg);
        Double thenExpr = n.getThenExpr().accept(this, arg);
        Double elseExpr = n.getElseExpr().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {condition/2, thenExpr, elseExpr})));
        return average(new Double[] {condition/2, thenExpr, elseExpr});
    }

    public Double visit(ConstructorDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ConstructorDeclaration");
        constructorDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }
        if (n.getParameters() != null) {
            for (Parameter p : n.getParameters()) {
                p.accept(this, arg);
            }
        }
        if (n.getThrows() != null) {
            for (NameExpr name : n.getThrows()) {
                name.accept(this, arg);
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getBlock().accept(this, arg)));
        return n.getBlock().accept(this, arg);
    }

    public Double visit(ContinueStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ContinueStmt");
        continueStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(DoStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "DoStmt");
        doStmtCount++;
        Double body = n.getBody().accept(this, arg);
        Double condition = n.getCondition().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {body, condition/2})));
        return average(new Double[] {body, condition/2});
    }

    public Double visit(DoubleLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "DoubleLiteralExpr");
        doubleLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyMemberDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyMemberDeclaration");
        emptyMemberDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyStmt");
        emptyStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyTypeDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyTypeDeclaration");
        emptyTypeDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EnclosedExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EnclosedExpr");
        enclosedExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getInner().accept(this, arg)));
        return n.getInner().accept(this, arg);
    }

    public Double visit(EnumConstantDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EnumConstantDeclaration");
        enumConstantDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, arg);
            }
        }
        if (n.getClassBody() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (BodyDeclaration member : n.getClassBody()) {
                dryValues += member.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EnumDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EnumDeclaration");
        enumDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getImplements() != null) {
            for (ClassOrInterfaceType c : n.getImplements()) {
                c.accept(this, arg);
            }
        }
        if (n.getEntries() != null) {
            for (EnumConstantDeclaration e : n.getEntries()) {
                e.accept(this, arg);
            }
        }
        if (n.getMembers() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (BodyDeclaration member : n.getMembers()) {
                dryValues += member.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ExplicitConstructorInvocationStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ExplicitConstructorInvocationStmt");
        explicitConstructorInvocationStmtCount++;
        Double thisExpr = new Double(0);
        if (!n.isThis()) {
            if (n.getExpr() != null) {
                thisExpr = n.getExpr().accept(this, arg);
            }
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        if (n.getArgs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getArgs()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ExpressionStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ExpressionStmt");
        expressionStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpression().accept(this, arg)));
        return n.getExpression().accept(this, arg);
    }

    public Double visit(FieldAccessExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "FieldAccessExpr");
        fieldAccessExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getScope().accept(this, arg)));
        return n.getScope().accept(this, arg);
    }

    public Double visit(FieldDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "FieldDeclaration");
        fieldDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                Double x = a.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        Double dryValues = new Double(0);
        int i = 0;
        for (VariableDeclarator var : n.getVariables()) {
            dryValues += var.accept(this, arg);
            i++;
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
        return divide(dryValues,i);
    }

    public Double visit(ForeachStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ForeachStmt");
        foreachStmtCount++;
        Double var = n.getVariable().accept(this, arg);
        Double iter = n.getIterable().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {var/3, iter/3, body})));
        return average(new Double[] {var/3, iter/3, body});
    }

    public Double visit(ForStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ForStmt");
        forStmtCount++;
        Double init = new Double(0), comp = new Double(0), update = new Double(0), body = new Double(0);
        if (n.getInit() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getInit()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            init = divide(dryValues, i);
        }
        if (n.getCompare() != null) {
            comp = n.getCompare().accept(this, arg);
        }
        if (n.getUpdate() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getUpdate()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            update = divide(dryValues, i);
        }
        body = n.getBody().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {init, comp, update, body})));
        return average(new Double[] {init, comp, update, body});
    }

    public Double visit(IfStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "IfStmt");
        ifStmtCount++;
        Double cond = n.getCondition().accept(this, arg);
        Double thenStmt = n.getThenStmt().accept(this, arg);
        Double elseStmt = new Double(0);
        if (n.getElseStmt() != null) {
            elseStmt = n.getElseStmt().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {cond/2, thenStmt, elseStmt})));
        return average(new Double[] {cond/2, thenStmt, elseStmt});
    }

    public Double visit(ImportDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ImportDeclaration");
        importDeclarationCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getName().accept(this, arg)));
        return n.getName().accept(this, arg);
    }

    public Double visit(InitializerDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "InitializerDeclaration");
        initializerDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getBlock().accept(this, arg)));
        return n.getBlock().accept(this, arg);
    }

    public Double visit(InstanceOfExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "InstanceOfExpr");
        instanceOfExprCount++;
        Double expr = n.getExpr().accept(this, arg);
        n.getType().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (expr));
        return expr;
    }

    public Double visit(IntegerLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "IntegerLiteralExpr");
        integerLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(IntegerLiteralMinValueExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "IntegerLiteralMinValueExpr");
        integerLiteralMinValueExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(JavadocComment n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "JavadocComment");
        javadocCommentCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(LabeledStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LabeledStmt");
        labeledStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getStmt().accept(this, arg)));
        return n.getStmt().accept(this, arg);
    }

    public Double visit(LongLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LongLiteralExpr");
        longLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(LongLiteralMinValueExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LongLiteralMinValueExpr");
        longLiteralMinValueExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(MarkerAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MarkerAnnotationExpr");
        markerAnnotationExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getName().accept(this, arg)));
        return n.getName().accept(this, arg);
    }

    public Double visit(MemberValuePair n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MemberValuePair");
        memberValuePairCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getValue().accept(this, arg)));
        return n.getValue().accept(this, arg);
    }

    public Double visit(MethodCallExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MethodCallExpr");
        methodCallExprCount++;
        Double scope = new Double(0), tArgs = new Double(0), args = new Double(0);
        if (n.getScope() != null) {
            scope = n.getScope().accept(this, arg);
        }
        if (n.getTypeArgs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Type t : n.getTypeArgs()) {
                dryValues += t.accept(this, arg);
                i++;
            }
            tArgs = divide(dryValues, i);
        }
        if (n.getArgs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getArgs()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            args = divide(dryValues, i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {scope/3, tArgs/2, args})));
        return average(new Double[] {scope/3, tArgs/2, args});
    }

    public Double visit(MethodDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MethodDeclaration");
        methodDeclarationCount++;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        if (n.getParameters() != null) {
            for (Parameter p : n.getParameters()) {
                p.accept(this, arg);
            }
        }
        if (n.getThrows() != null) {
            for (NameExpr name : n.getThrows()) {
                name.accept(this, arg);
            }
        }
        Double body = new Double(0);
        if (n.getBody() != null) {
            body = n.getBody().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (body));
        return body;
    }

    public Double visit(NameExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "NameExpr");
        nameExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(NormalAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "NormalAnnotationExpr");
        normalAnnotationExprCount++;
        n.getName().accept(this, arg);
        if (n.getPairs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (MemberValuePair m : n.getPairs()) {
                dryValues += m.accept(this, arg);
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(NullLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "NullLiteralExpr");
        nullLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ObjectCreationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ObjectCreationExpr");
        objectCreationExprCount++;
        Double scope = new Double(0), tArgs = new Double(0), type = new Double(0), args = new Double(0), aClassBody = new Double(0);
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        type = n.getType().accept(this, arg);
        if (n.getArgs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getArgs()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            args = divide(dryValues, i);
        }
        if (n.getAnonymousClassBody() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (BodyDeclaration member : n.getAnonymousClassBody()) {
                dryValues += member.accept(this, arg);
                i++;
            }
            aClassBody = divide(dryValues, i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {type/3, args, aClassBody})));
        return average(new Double[] {type/3, args, aClassBody});
    }

    public Double visit(PackageDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "PackageDeclaration");
        packageDeclarationCount++;
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getName().accept(this, arg)));
        return n.getName().accept(this, arg);
    }

    public Double visit(Parameter n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "Parameter");
        parameterCount++;
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        Double type = n.getType().accept(this, arg);
        Double id = n.getId().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {type, id})));
        return average(new Double[] {type, id});
    }

    public Double visit(PrimitiveType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "PrimitiveType");
        primitiveTypeCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(QualifiedNameExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "QualifiedNameExpr");
        qualifiedNameExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getQualifier().accept(this, arg)));
        return n.getQualifier().accept(this, arg);
    }

    public Double visit(ReferenceType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ReferenceType");
        referenceTypeCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getType().accept(this, arg)));
        return n.getType().accept(this, arg);
    }

    public Double visit(ReturnStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ReturnStmt");
        returnStmtCount++;
        if (n.getExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
            return n.getExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(SingleMemberAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SingleMemberAnnotationExpr");
        singleMemberAnnotationExprCount++;
        n.getName().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getMemberValue().accept(this, arg)));
        return n.getMemberValue().accept(this, arg);
    }

    public Double visit(StringLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "StringLiteralExpr");
        stringLiteralExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(SuperExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SuperExpr");
        superExprCount++;
        if (n.getClassExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getClassExpr().accept(this, arg)));
            return n.getClassExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(SwitchEntryStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SwitchEntryStmt");
        switchEntryStmtCount++;
        if (n.getLabel() != null) {
            n.getLabel().accept(this, arg);
        }
        if (n.getStmts() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Statement s : n.getStmts()) {
                dryValues += s.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(SwitchStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SwitchStmt");
        switchStmtCount++;
        n.getSelector().accept(this, arg);
        if (n.getEntries() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (SwitchEntryStmt e : n.getEntries()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);

    }

    public Double visit(SynchronizedStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SynchronizedStmt");
        synchronizedStmtCount++;
        Double expr = n.getExpr().accept(this, arg);
        Double block = n.getBlock().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {expr, block})));
        return average(new Double[] {expr, block});
    }

    public Double visit(ThisExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ThisExpr");
        thisExprCount++;
        if (n.getClassExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getClassExpr().accept(this, arg)));
            return n.getClassExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ThrowStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ThrowStmt");
        throwStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(TryStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "TryStmt");
        tryStmtCount++;
        Double tBlock = new Double(0), cBlock = new Double(0), fBlock = new Double(0);
        tBlock = n.getTryBlock().accept(this, arg);
        if (n.getCatchs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (CatchClause c : n.getCatchs()) {
                dryValues += c.accept(this, arg);
                i++;
            }
            cBlock = divide(dryValues, i);
        }
        if (n.getFinallyBlock() != null) {
            fBlock = n.getFinallyBlock().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {tBlock/5, cBlock, fBlock})));
        return average(new Double[] {tBlock/5, cBlock, fBlock});
    }

    public Double visit(TypeDeclarationStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "TypeDeclarationStmt");
        typeDeclarationStmtCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getTypeDeclaration().accept(this, arg)));
        return n.getTypeDeclaration().accept(this, arg);
    }

    public Double visit(TypeParameter n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "TypeParameter");
        typeParameterCount++;
        if (n.getTypeBound() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (ClassOrInterfaceType c : n.getTypeBound()) {
                dryValues += c.accept(this, arg);
                i++;
            }
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
            return divide(dryValues,i);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(UnaryExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "UnaryExpr");
        unaryExprCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(VariableDeclarationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VariableDeclarationExpr");
        variableDeclarationExprCount++;
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        Double type = n.getType().accept(this, arg);
        Double dryValues = new Double(0);
        int i = 0;
        for (VariableDeclarator v : n.getVars()) {
            dryValues += v.accept(this, arg);
            i++;
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (divide(dryValues,i)));
        return divide(dryValues,i);
    }

    public Double visit(VariableDeclarator n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VariableDeclarator");
        variableDeclaratorCount++;
        n.getId().accept(this, arg);
        if (n.getInit() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getInit().accept(this, arg)));
            return n.getInit().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(VariableDeclaratorId n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VariableDeclaratorId");
        variableDeclaratorIdCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(VoidType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VoidType");
        voidTypeCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(WhileStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "WhileStmt");
        whileStmtCount++;
        Double cond = n.getCondition().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {cond/2, body})));
        return average(new Double[] {cond/2, body});
    }

    public Double visit(WildcardType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "WildcardType");
        wildcardTypeCount++;
        if (n.getExtends() != null) {
            n.getExtends().accept(this, arg);
        }
        if (n.getSuper() != null) {
            n.getSuper().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(BlockComment n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BlockComment");
        blockCommentCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(LineComment n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LineComment");
        lineCommentCount++;
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }


    // computes average dryness scores
    public Double average(Double[] list) {
        Double sum = new Double(0);
        for(Double elem : list) {
            sum += elem;
        }
        return divide(sum, list.length);
    }

    // computes the division of two Doubles, but if the second Double is 0, we return 0
    public Double divide(Double sum, Double i) {
        // roundabout way to check whether i is 0
        if (i < 0.0001 && i > -0.0001) {
            return new Double(0);
        }
        return sum/i;
    }
    // computes the division of two numbers, but if there are 0 numbers, we return 0
    public Double divide(Double sum, int i) {
        if (i == 0) {
            return new Double(0);
        }
        return sum/i;
    }
    // computes the division of two integers, but if the second int is 0, we return 0
    public Double divide(int sum, int i) {
        if (i == 0) {
            return new Double(0);
        }
        return (Double) ((double) sum)/((double) i);
    }

    // computes the sum of an integer array
    public int sum(int[] arr) {
        int s = 0;
        for (int i = 0; i < arr.length; i++) {
            s += arr[i];
        }
        return s;
    }
    // computes the number of nonzero elements in an integer array
    public int countNonZero(int[] arr) {
        int c = 0;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] != 0) c++;
        }
        return c;
    }

}
