/**
 * AllPairsNaiveHeuristic.java
 * Modified from Julio Vilmar Gesser's GenericVisitorAdapter
 * to compute dryness scores of nodes
 *
 * For each type of Java expression, we make a list of statements
 * of that expression type that's found in the input file.
 * We then loop through each type of statement and compare the
 * expressions pairwise for equality. If there are pairs of equal
 * statements, the dryness score gets penalized.
 * 
 * @author J. Hassler Thurston
 * Modified from javaparser by Julio Vilmar Gesser
 * Modified from japa.parser.ast.visitor.GenericVisitorAdapter
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
public class AllPairsNaiveHeuristic<A> implements GenericVisitor<Double, A> {

    // lists of expressions are stored here
    static ArrayList<AnnotationDeclaration> annotationDeclarations;
    static ArrayList<AnnotationMemberDeclaration> annotationMemberDeclarations;
    static ArrayList<ArrayAccessExpr> arrayAccessExprs;
    static ArrayList<ArrayCreationExpr> arrayCreationExprs;
    static ArrayList<ArrayInitializerExpr> arrayInitializerExprs;
    static ArrayList<AssertStmt> assertStmts;
    static ArrayList<AssignExpr> assignExprs;
    static ArrayList<BinaryExpr> binaryExprs;
    static ArrayList<BlockStmt> blockStmts;
    static ArrayList<BooleanLiteralExpr> booleanLiteralExprs;
    static ArrayList<BreakStmt> breakStmts;
    static ArrayList<CastExpr> castExprs;
    static ArrayList<CatchClause> catchClauses;
    static ArrayList<CharLiteralExpr> charLiteralExprs;
    static ArrayList<ClassExpr> classExprs;
    static ArrayList<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations;
    static ArrayList<ClassOrInterfaceType> classOrInterfaceTypes;
    static ArrayList<CompilationUnit> compilationUnits;
    static ArrayList<ConditionalExpr> conditionalExprs;
    static ArrayList<ConstructorDeclaration> constructorDeclarations;
    static ArrayList<ContinueStmt> continueStmts;
    static ArrayList<DoStmt> doStmts;
    static ArrayList<DoubleLiteralExpr> doubleLiteralExprs;
    static ArrayList<EmptyMemberDeclaration> emptyMemberDeclarations;
    static ArrayList<EmptyStmt> emptyStmts;
    static ArrayList<EmptyTypeDeclaration> emptyTypeDeclarations;
    static ArrayList<EnclosedExpr> enclosedExprs;
    static ArrayList<EnumConstantDeclaration> enumConstantDeclarations;
    static ArrayList<EnumDeclaration> enumDeclarations;
    static ArrayList<ExplicitConstructorInvocationStmt> explicitConstructorInvocationStmts;
    static ArrayList<ExpressionStmt> expressionStmts;
    static ArrayList<FieldAccessExpr> fieldAccessExprs;
    static ArrayList<FieldDeclaration> fieldDeclarations;
    static ArrayList<ForeachStmt> foreachStmts;
    static ArrayList<ForStmt> forStmts;
    static ArrayList<IfStmt> ifStmts;
    static ArrayList<ImportDeclaration> importDeclarations;
    static ArrayList<InitializerDeclaration> initializerDeclarations;
    static ArrayList<InstanceOfExpr> instanceOfExprs;
    static ArrayList<IntegerLiteralExpr> integerLiteralExprs;
    static ArrayList<IntegerLiteralMinValueExpr> integerLiteralMinValueExprs;
    static ArrayList<JavadocComment> javadocComments;
    static ArrayList<LabeledStmt> labeledStmts;
    static ArrayList<LongLiteralExpr> longLiteralExprs;
    static ArrayList<LongLiteralMinValueExpr> longLiteralMinValueExprs;
    static ArrayList<MarkerAnnotationExpr> markerAnnotationExprs;
    static ArrayList<MemberValuePair> memberValuePairs;
    static ArrayList<MethodCallExpr> methodCallExprs;
    static ArrayList<MethodDeclaration> methodDeclarations;
    static ArrayList<NameExpr> nameExprs;
    static ArrayList<NormalAnnotationExpr> normalAnnotationExprs;
    static ArrayList<NullLiteralExpr> nullLiteralExprs;
    static ArrayList<ObjectCreationExpr> objectCreationExprs;
    static ArrayList<PackageDeclaration> packageDeclarations;
    static ArrayList<Parameter> parameters;
    static ArrayList<PrimitiveType> primitiveTypes;
    static ArrayList<QualifiedNameExpr> qualifiedNameExprs;
    static ArrayList<ReferenceType> referenceTypes;
    static ArrayList<ReturnStmt> returnStmts;
    static ArrayList<SingleMemberAnnotationExpr> singleMemberAnnotationExprs;
    static ArrayList<StringLiteralExpr> stringLiteralExprs;
    static ArrayList<SuperExpr> superExprs;
    static ArrayList<SwitchEntryStmt> switchEntryStmts;
    static ArrayList<SwitchStmt> switchStmts;
    static ArrayList<SynchronizedStmt> synchronizedStmts;
    static ArrayList<ThisExpr> thisExprs;
    static ArrayList<ThrowStmt> throwStmts;
    static ArrayList<TryStmt> tryStmts;
    static ArrayList<TypeDeclarationStmt> typeDeclarationStmts;
    static ArrayList<TypeParameter> typeParameters;
    static ArrayList<UnaryExpr> unaryExprs;
    static ArrayList<VariableDeclarationExpr> variableDeclarationExprs;
    static ArrayList<VariableDeclarator> variableDeclarators;
    static ArrayList<VariableDeclaratorId> variableDeclaratorIds;
    static ArrayList<VoidType> voidTypes;
    static ArrayList<WhileStmt> whileStmts;
    static ArrayList<WildcardType> wildcardTypes;
    static ArrayList<BlockComment> blockComments;
    static ArrayList<LineComment> lineComments;

    boolean comments = false;
    public AllPairsNaiveHeuristic(boolean comments) {
        this.comments = comments;
        // initialize pairwise variables
        annotationDeclarations = new ArrayList<AnnotationDeclaration>();
        annotationMemberDeclarations = new ArrayList<AnnotationMemberDeclaration>();
        arrayAccessExprs = new ArrayList<ArrayAccessExpr>();
        arrayCreationExprs = new ArrayList<ArrayCreationExpr>();
        arrayInitializerExprs = new ArrayList<ArrayInitializerExpr>();
        assertStmts = new ArrayList<AssertStmt>();
        assignExprs = new ArrayList<AssignExpr>();
        binaryExprs = new ArrayList<BinaryExpr>();
        blockStmts = new ArrayList<BlockStmt>();
        booleanLiteralExprs = new ArrayList<BooleanLiteralExpr>();
        breakStmts = new ArrayList<BreakStmt>();
        castExprs = new ArrayList<CastExpr>();
        catchClauses = new ArrayList<CatchClause>();
        charLiteralExprs = new ArrayList<CharLiteralExpr>();
        classExprs = new ArrayList<ClassExpr>();
        classOrInterfaceDeclarations = new ArrayList<ClassOrInterfaceDeclaration>();
        classOrInterfaceTypes = new ArrayList<ClassOrInterfaceType>();
        compilationUnits = new ArrayList<CompilationUnit>();
        conditionalExprs = new ArrayList<ConditionalExpr>();
        constructorDeclarations = new ArrayList<ConstructorDeclaration>();
        continueStmts = new ArrayList<ContinueStmt>();
        doStmts = new ArrayList<DoStmt>();
        doubleLiteralExprs = new ArrayList<DoubleLiteralExpr>();
        emptyMemberDeclarations = new ArrayList<EmptyMemberDeclaration>();
        emptyStmts = new ArrayList<EmptyStmt>();
        emptyTypeDeclarations = new ArrayList<EmptyTypeDeclaration>();
        enclosedExprs = new ArrayList<EnclosedExpr>();
        enumConstantDeclarations = new ArrayList<EnumConstantDeclaration>();
        enumDeclarations = new ArrayList<EnumDeclaration>();
        explicitConstructorInvocationStmts = new ArrayList<ExplicitConstructorInvocationStmt>();
        expressionStmts = new ArrayList<ExpressionStmt>();
        fieldAccessExprs = new ArrayList<FieldAccessExpr>();
        fieldDeclarations = new ArrayList<FieldDeclaration>();
        foreachStmts = new ArrayList<ForeachStmt>();
        forStmts = new ArrayList<ForStmt>();
        ifStmts = new ArrayList<IfStmt>();
        importDeclarations = new ArrayList<ImportDeclaration>();
        initializerDeclarations = new ArrayList<InitializerDeclaration>();
        instanceOfExprs = new ArrayList<InstanceOfExpr>();
        integerLiteralExprs = new ArrayList<IntegerLiteralExpr>();
        integerLiteralMinValueExprs = new ArrayList<IntegerLiteralMinValueExpr>();
        javadocComments = new ArrayList<JavadocComment>();
        labeledStmts = new ArrayList<LabeledStmt>();
        longLiteralExprs = new ArrayList<LongLiteralExpr>();
        longLiteralMinValueExprs = new ArrayList<LongLiteralMinValueExpr>();
        markerAnnotationExprs = new ArrayList<MarkerAnnotationExpr>();
        memberValuePairs = new ArrayList<MemberValuePair>();
        methodCallExprs = new ArrayList<MethodCallExpr>();
        methodDeclarations = new ArrayList<MethodDeclaration>();
        nameExprs = new ArrayList<NameExpr>();
        normalAnnotationExprs = new ArrayList<NormalAnnotationExpr>();
        nullLiteralExprs = new ArrayList<NullLiteralExpr>();
        objectCreationExprs = new ArrayList<ObjectCreationExpr>();
        packageDeclarations = new ArrayList<PackageDeclaration>();
        parameters = new ArrayList<Parameter>();
        primitiveTypes = new ArrayList<PrimitiveType>();
        qualifiedNameExprs = new ArrayList<QualifiedNameExpr>();
        referenceTypes = new ArrayList<ReferenceType>();
        returnStmts = new ArrayList<ReturnStmt>();
        singleMemberAnnotationExprs = new ArrayList<SingleMemberAnnotationExpr>();
        stringLiteralExprs = new ArrayList<StringLiteralExpr>();
        superExprs = new ArrayList<SuperExpr>();
        switchEntryStmts = new ArrayList<SwitchEntryStmt>();
        switchStmts = new ArrayList<SwitchStmt>();
        synchronizedStmts = new ArrayList<SynchronizedStmt>();
        thisExprs = new ArrayList<ThisExpr>();
        throwStmts = new ArrayList<ThrowStmt>();
        tryStmts = new ArrayList<TryStmt>();
        typeDeclarationStmts = new ArrayList<TypeDeclarationStmt>();
        typeParameters = new ArrayList<TypeParameter>();
        unaryExprs = new ArrayList<UnaryExpr>();
        variableDeclarationExprs = new ArrayList<VariableDeclarationExpr>();
        variableDeclarators = new ArrayList<VariableDeclarator>();
        variableDeclaratorIds = new ArrayList<VariableDeclaratorId>();
        voidTypes = new ArrayList<VoidType>();
        whileStmts = new ArrayList<WhileStmt>();
        wildcardTypes = new ArrayList<WildcardType>();
        blockComments = new ArrayList<BlockComment>();
        lineComments = new ArrayList<LineComment>();
    }
    public AllPairsNaiveHeuristic() {
        this(false);
    }
    public void setComments(boolean val) {
        this.comments = val;
    }


    // computes the pairwise dryness score from the ArrayList variables
    public Double computePairwiseDrynessScore() {
        Double result = 0D;
        // go through each array list, and compute pairwise equalities
        ArrayList[] arrayLists = new ArrayList[] {
            annotationDeclarations,
            annotationMemberDeclarations,
            arrayAccessExprs,
            arrayCreationExprs,
            arrayInitializerExprs,
            assertStmts,
            assignExprs,
            binaryExprs,
            blockStmts,
            booleanLiteralExprs,
            breakStmts,
            castExprs,
            catchClauses,
            charLiteralExprs,
            classExprs,
            classOrInterfaceDeclarations,
            classOrInterfaceTypes,
            compilationUnits,
            conditionalExprs,
            constructorDeclarations,
            continueStmts,
            doStmts,
            doubleLiteralExprs,
            emptyMemberDeclarations,
            emptyStmts,
            emptyTypeDeclarations,
            enclosedExprs,
            enumConstantDeclarations,
            enumDeclarations,
            explicitConstructorInvocationStmts,
            expressionStmts,
            fieldAccessExprs,
            fieldDeclarations,
            foreachStmts,
            forStmts,
            ifStmts,
            importDeclarations,
            initializerDeclarations,
            instanceOfExprs,
            integerLiteralExprs,
            integerLiteralMinValueExprs,
            javadocComments,
            labeledStmts,
            longLiteralExprs,
            longLiteralMinValueExprs,
            markerAnnotationExprs,
            memberValuePairs,
            methodCallExprs,
            methodDeclarations,
            nameExprs,
            normalAnnotationExprs,
            nullLiteralExprs,
            objectCreationExprs,
            packageDeclarations,
            parameters,
            primitiveTypes,
            qualifiedNameExprs,
            referenceTypes,
            returnStmts,
            singleMemberAnnotationExprs,
            stringLiteralExprs,
            superExprs,
            switchEntryStmts,
            switchStmts,
            synchronizedStmts,
            thisExprs,
            throwStmts,
            tryStmts,
            typeDeclarationStmts,
            typeParameters,
            unaryExprs,
            variableDeclarationExprs,
            variableDeclarators,
            variableDeclaratorIds,
            voidTypes,
            whileStmts,
            wildcardTypes,
            blockComments,
            lineComments};
        Double[] results = new Double[arrayLists.length];
        for (int ls = 0; ls < arrayLists.length; ls++) {
            // convert the list to an array
            Node[] stmts = new Node[arrayLists[ls].size()];
            arrayLists[ls].toArray(stmts);
            // compare elements pairwise
            Double equalityCounter = 0D;
            int pairs = 0;
            // Double dryValues = 0D;
            int i = 0;
            if (stmts != null) {
                for (int s1 = 0; s1 < stmts.length; s1++) {
                    // dryValues += stmts[s1].accept(this, null);
                    i++;
                    for (int s2 = 0; s2 < s1; s2++) {
                        if (stmts[s1].equals(stmts[s2])) {
                            equalityCounter = equalityCounter + 1;
                        }
                        pairs++;
                    }
                }
            }
            results[ls] = divide(equalityCounter,pairs);
        }
        result = average(results);
        return result;
    }



    public Double visit(AnnotationDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "AnnotationDeclaration");
        annotationDeclarations.add(n);
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
        annotationMemberDeclarations.add(n);
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
        arrayAccessExprs.add(n);
        n.getName().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getIndex().accept(this, arg)));
        return n.getIndex().accept(this, arg);
    }

    public Double visit(ArrayCreationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ArrayCreationExpr");
        arrayCreationExprs.add(n);
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
        arrayInitializerExprs.add(n);
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
        assertStmts.add(n);
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
        assignExprs.add(n);
        n.getTarget().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getValue().accept(this, arg)));
        return n.getValue().accept(this, arg);
    }

    public Double visit(BinaryExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BinaryExpr");
        binaryExprs.add(n);
        Double left = n.getLeft().accept(this, arg);
        Double right = n.getRight().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {left, right})));
        return average(new Double[] {left, right});
    }

    public Double visit(BlockStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BlockStmt");
        blockStmts.add(n);
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
        booleanLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1); // stub
    }

    public Double visit(BreakStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "BreakStmt");
        breakStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(CastExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CastExpr");
        castExprs.add(n);
        n.getType().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(CatchClause n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CatchClause");
        catchClauses.add(n);
        Double except = n.getExcept().accept(this, arg);
        Double catchBlock = n.getCatchBlock().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {except, catchBlock})));
        return average(new Double[] {except, catchBlock});

    }

    public Double visit(CharLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "CharLiteralExpr");
        charLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ClassExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ClassExpr");
        classExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getType().accept(this, arg)));
        return n.getType().accept(this, arg);
    }

    public Double visit(ClassOrInterfaceDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ClassOrInterfaceDeclaration");
        classOrInterfaceDeclarations.add(n);
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
        classOrInterfaceTypes.add(n);
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
        compilationUnits.add(n);
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
        Double dryValue = computePairwiseDrynessScore();
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (dryValue));
        return dryValue; // stub
    }

    public Double visit(ConditionalExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ConditionalExpr");
        conditionalExprs.add(n);
        Double condition = n.getCondition().accept(this, arg);
        Double thenExpr = n.getThenExpr().accept(this, arg);
        Double elseExpr = n.getElseExpr().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {condition/2, thenExpr, elseExpr})));
        return average(new Double[] {condition/2, thenExpr, elseExpr});
    }

    public Double visit(ConstructorDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ConstructorDeclaration");
        constructorDeclarations.add(n);
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
        continueStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(DoStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "DoStmt");
        doStmts.add(n);
        Double body = n.getBody().accept(this, arg);
        Double condition = n.getCondition().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {body, condition/2})));
        return average(new Double[] {body, condition/2});
    }

    public Double visit(DoubleLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "DoubleLiteralExpr");
        doubleLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyMemberDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyMemberDeclaration");
        emptyMemberDeclarations.add(n);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyStmt");
        emptyStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EmptyTypeDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EmptyTypeDeclaration");
        emptyTypeDeclarations.add(n);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(EnclosedExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EnclosedExpr");
        enclosedExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getInner().accept(this, arg)));
        return n.getInner().accept(this, arg);
    }

    public Double visit(EnumConstantDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "EnumConstantDeclaration");
        enumConstantDeclarations.add(n);
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
        enumDeclarations.add(n);
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
        explicitConstructorInvocationStmts.add(n);
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
        expressionStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpression().accept(this, arg)));
        return n.getExpression().accept(this, arg);
    }

    public Double visit(FieldAccessExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "FieldAccessExpr");
        fieldAccessExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getScope().accept(this, arg)));
        return n.getScope().accept(this, arg);
    }

    public Double visit(FieldDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "FieldDeclaration");
        fieldDeclarations.add(n);
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
        foreachStmts.add(n);
        Double var = n.getVariable().accept(this, arg);
        Double iter = n.getIterable().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {var/3, iter/3, body})));
        return average(new Double[] {var/3, iter/3, body});
    }

    public Double visit(ForStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ForStmt");
        forStmts.add(n);
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
        ifStmts.add(n);
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
        importDeclarations.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getName().accept(this, arg)));
        return n.getName().accept(this, arg);
    }

    public Double visit(InitializerDeclaration n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "InitializerDeclaration");
        initializerDeclarations.add(n);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getBlock().accept(this, arg)));
        return n.getBlock().accept(this, arg);
    }

    public Double visit(InstanceOfExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "InstanceOfExpr");
        instanceOfExprs.add(n);
        Double expr = n.getExpr().accept(this, arg);
        n.getType().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (expr));
        return expr;
    }

    public Double visit(IntegerLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "IntegerLiteralExpr");
        integerLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(IntegerLiteralMinValueExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "IntegerLiteralMinValueExpr");
        integerLiteralMinValueExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(JavadocComment n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "JavadocComment");
        javadocComments.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(LabeledStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LabeledStmt");
        labeledStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getStmt().accept(this, arg)));
        return n.getStmt().accept(this, arg);
    }

    public Double visit(LongLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LongLiteralExpr");
        longLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(LongLiteralMinValueExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LongLiteralMinValueExpr");
        longLiteralMinValueExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(MarkerAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MarkerAnnotationExpr");
        markerAnnotationExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getName().accept(this, arg)));
        return n.getName().accept(this, arg);
    }

    public Double visit(MemberValuePair n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MemberValuePair");
        memberValuePairs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getValue().accept(this, arg)));
        return n.getValue().accept(this, arg);
    }

    public Double visit(MethodCallExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "MethodCallExpr");
        methodCallExprs.add(n);
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
        methodDeclarations.add(n);
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
        nameExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0); // stub
    }

    public Double visit(NormalAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "NormalAnnotationExpr");
        normalAnnotationExprs.add(n);
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
        nullLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ObjectCreationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ObjectCreationExpr");
        objectCreationExprs.add(n);
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
        packageDeclarations.add(n);
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
        parameters.add(n);
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
        primitiveTypes.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(QualifiedNameExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "QualifiedNameExpr");
        qualifiedNameExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getQualifier().accept(this, arg)));
        return n.getQualifier().accept(this, arg);
    }

    public Double visit(ReferenceType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ReferenceType");
        referenceTypes.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getType().accept(this, arg)));
        return n.getType().accept(this, arg);
    }

    public Double visit(ReturnStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ReturnStmt");
        returnStmts.add(n);
        if (n.getExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
            return n.getExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(SingleMemberAnnotationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SingleMemberAnnotationExpr");
        singleMemberAnnotationExprs.add(n);
        n.getName().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getMemberValue().accept(this, arg)));
        return n.getMemberValue().accept(this, arg);
    }

    public Double visit(StringLiteralExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "StringLiteralExpr");
        stringLiteralExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(1)));
        return new Double(1);
    }

    public Double visit(SuperExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SuperExpr");
        superExprs.add(n);
        if (n.getClassExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getClassExpr().accept(this, arg)));
            return n.getClassExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(SwitchEntryStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "SwitchEntryStmt");
        switchEntryStmts.add(n);
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
        switchStmts.add(n);
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
        synchronizedStmts.add(n);
        Double expr = n.getExpr().accept(this, arg);
        Double block = n.getBlock().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {expr, block})));
        return average(new Double[] {expr, block});
    }

    public Double visit(ThisExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ThisExpr");
        thisExprs.add(n);
        if (n.getClassExpr() != null) {
            if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getClassExpr().accept(this, arg)));
            return n.getClassExpr().accept(this, arg);
        }
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(ThrowStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "ThrowStmt");
        throwStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(TryStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "TryStmt");
        tryStmts.add(n);
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
        typeDeclarationStmts.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getTypeDeclaration().accept(this, arg)));
        return n.getTypeDeclaration().accept(this, arg);
    }

    public Double visit(TypeParameter n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "TypeParameter");
        typeParameters.add(n);
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
        unaryExprs.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (n.getExpr().accept(this, arg)));
        return n.getExpr().accept(this, arg);
    }

    public Double visit(VariableDeclarationExpr n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VariableDeclarationExpr");
        variableDeclarationExprs.add(n);
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
        variableDeclarators.add(n);
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
        variableDeclaratorIds.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(VoidType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "VoidType");
        voidTypes.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(WhileStmt n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "WhileStmt");
        whileStmts.add(n);
        Double cond = n.getCondition().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (average(new Double[] {cond/2, body})));
        return average(new Double[] {cond/2, body});
    }

    public Double visit(WildcardType n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "WildcardType");
        wildcardTypes.add(n);
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
        blockComments.add(n);
        if(comments) System.out.println("dryness score for node " + n + " of type " + n.getClass() + " is: " + (new Double(0)));
        return new Double(0);
    }

    public Double visit(LineComment n, A arg) {
        if(comments) System.out.println("Visiting node " + n + " of type " + "LineComment");
        lineComments.add(n);
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

    // computes the division of two numbers, but if there are 0 numbers, we return 0
    public Double divide(Double sum, int i) {
        if (i == 0) {
            return new Double(0);
        }
        return sum/i;
    }

}
