/** ZeroVisitor.java
 * Modified from Julio Vilmar Gesser's GenericVisitorAdapter
 * to compute dryness scores of nodes
 * 
 * @author J. Hassler Thurston
 * Modified from javaparser by Julio Vilmar Gesser
 * Modified from japa.parser.ast.visitor.GenericVisitorAdapter
 * 
 * CSC200H Research Project
 * Fall 2014
*/

// package dry.visitors;

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

/**
 * @author J. Hassler Thurston
 */
public class ZeroVisitor<A> implements GenericVisitor<Double, A> {

    public Double visit(AnnotationDeclaration n, A arg) {
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
        return new Double(0); // stub
    }

    public Double visit(AnnotationMemberDeclaration n, A arg) {
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
        return new Double(0); // stub
    }

    public Double visit(ArrayAccessExpr n, A arg) {
        n.getName().accept(this, arg);
        return n.getIndex().accept(this, arg);
    }

    public Double visit(ArrayCreationExpr n, A arg) {
        n.getType().accept(this, arg);
        if (n.getDimensions() != null) {
            for (Expression dim : n.getDimensions()) {
                dim.accept(this, arg);
            }
            return new Double(0); // stub
        } else {
            return n.getInitializer().accept(this, arg);
        }
    }

    public Double visit(ArrayInitializerExpr n, A arg) {
        Double dryValues = new Double(0);
        int i = 0;
        if (n.getValues() != null) {
            for (Expression expr : n.getValues()) {
                dryValues += expr.accept(this, arg);
                i++;
            }
        }
        return dryValues/i;
    }

    public Double visit(AssertStmt n, A arg) {
        Double check, message = new Double(0);
        check = n.getCheck().accept(this, arg);
        if (n.getMessage() != null) {
            message = n.getMessage().accept(this, arg);
        }
        return message; // stub
    }

    public Double visit(AssignExpr n, A arg) {
        n.getTarget().accept(this, arg);
        return n.getValue().accept(this, arg);
    }

    public Double visit(BinaryExpr n, A arg) {
        Double left = n.getLeft().accept(this, arg);
        Double right = n.getRight().accept(this, arg);
        return average(new Double[] {left, right});
    }

    public Double visit(BlockStmt n, A arg) {
        Double dryValues = new Double(0);
        int i = 0;
        if (n.getStmts() != null) {
            for (Statement s : n.getStmts()) {
                dryValues += s.accept(this, arg);
                i++;
            }
        }
        return dryValues/i;

    }

    public Double visit(BooleanLiteralExpr n, A arg) {
        return new Double(1); // stub
    }

    public Double visit(BreakStmt n, A arg) {
        return new Double(0);
    }

    public Double visit(CastExpr n, A arg) {
        n.getType().accept(this, arg);
        return n.getExpr().accept(this, arg);
    }

    public Double visit(CatchClause n, A arg) {
        Double except = n.getExcept().accept(this, arg);
        Double catchBlock = n.getCatchBlock().accept(this, arg);
        return average(new Double[] {except, catchBlock});

    }

    public Double visit(CharLiteralExpr n, A arg) {
        return new Double(0);
    }

    public Double visit(ClassExpr n, A arg) {
        return n.getType().accept(this, arg);
    }

    public Double visit(ClassOrInterfaceDeclaration n, A arg) {
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
            return dryValues/i;
        }
        return new Double(0); // stub
    }

    public Double visit(ClassOrInterfaceType n, A arg) {
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        return new Double(0); // stub
    }

    public Double visit(CompilationUnit n, A arg) {
        if (n.getPackage() != null) {
            n.getPackage().accept(this, arg);
        }
        if (n.getImports() != null) {
            for (ImportDeclaration i : n.getImports()) {
                i.accept(this, arg);
            }
        }
        if (n.getTypes() != null) {
            for (TypeDeclaration typeDeclaration : n.getTypes()) {
                typeDeclaration.accept(this, arg);
            }
        }
        return new Double(0); // stub
    }

    public Double visit(ConditionalExpr n, A arg) {
        Double condition = n.getCondition().accept(this, arg);
        Double thenExpr = n.getThenExpr().accept(this, arg);
        Double elseExpr = n.getElseExpr().accept(this, arg);
        return average(new Double[] {condition/2, thenExpr, elseExpr});
    }

    public Double visit(ConstructorDeclaration n, A arg) {
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
        return n.getBlock().accept(this, arg);
    }

    public Double visit(ContinueStmt n, A arg) {
        return new Double(0); // stub
    }

    public Double visit(DoStmt n, A arg) {
        Double body = n.getBody().accept(this, arg);
        Double condition = n.getCondition().accept(this, arg);
        return average(new Double[] {body, condition/2});
    }

    public Double visit(DoubleLiteralExpr n, A arg) {
        return new Double(0); // stub
    }

    public Double visit(EmptyMemberDeclaration n, A arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        return new Double(0); // stub
    }

    public Double visit(EmptyStmt n, A arg) {
        return new Double(0); // stub
    }

    public Double visit(EmptyTypeDeclaration n, A arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        return new Double(0); // stub
    }

    public Double visit(EnclosedExpr n, A arg) {
        return n.getInner().accept(this, arg);
    }

    public Double visit(EnumConstantDeclaration n, A arg) {
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
            return dryValues/i;
        }
        return new Double(0); // stub
    }

    public Double visit(EnumDeclaration n, A arg) {
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
            return dryValues/i;
        }
        return new Double(0);
    }

    public Double visit(ExplicitConstructorInvocationStmt n, A arg) {
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
            return dryValues/i;
        }
        return new Double(0);
    }

    public Double visit(ExpressionStmt n, A arg) {
        return n.getExpression().accept(this, arg);
    }

    public Double visit(FieldAccessExpr n, A arg) {
        return n.getScope().accept(this, arg);
    }

    public Double visit(FieldDeclaration n, A arg) {
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
        return dryValues/i;
    }

    public Double visit(ForeachStmt n, A arg) {
        Double var = n.getVariable().accept(this, arg);
        Double iter = n.getIterable().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        return average(new Double[] {var/3, iter/3, body});
    }

    public Double visit(ForStmt n, A arg) {
        Double init = new Double(0), comp = new Double(0), update = new Double(0), body = new Double(0);
        if (n.getInit() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getInit()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            init = dryValues/i;
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
            update = dryValues/i;
        }
        body = n.getBody().accept(this, arg);
        return average(new Double[] {init, comp, update, body});
    }

    public Double visit(IfStmt n, A arg) {
        Double cond = n.getCondition().accept(this, arg);
        Double thenStmt = n.getThenStmt().accept(this, arg);
        Double elseStmt = new Double(0);
        if (n.getElseStmt() != null) {
            elseStmt = n.getElseStmt().accept(this, arg);
        }
        return average(new Double[] {cond/2, thenStmt, elseStmt});
    }

    public Double visit(ImportDeclaration n, A arg) {
        return n.getName().accept(this, arg);
    }

    public Double visit(InitializerDeclaration n, A arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        return n.getBlock().accept(this, arg);
    }

    public Double visit(InstanceOfExpr n, A arg) {
        Double expr = n.getExpr().accept(this, arg);
        n.getType().accept(this, arg);
        return expr;
    }

    public Double visit(IntegerLiteralExpr n, A arg) {
        return new Double(1);
    }

    public Double visit(IntegerLiteralMinValueExpr n, A arg) {
        return new Double(1);
    }

    public Double visit(JavadocComment n, A arg) {
        return new Double(0);
    }

    public Double visit(LabeledStmt n, A arg) {
        return n.getStmt().accept(this, arg);
    }

    public Double visit(LongLiteralExpr n, A arg) {
        return new Double(1);
    }

    public Double visit(LongLiteralMinValueExpr n, A arg) {
        return new Double(1);
    }

    public Double visit(MarkerAnnotationExpr n, A arg) {
        return n.getName().accept(this, arg);
    }

    public Double visit(MemberValuePair n, A arg) {
        return n.getValue().accept(this, arg);
    }

    public Double visit(MethodCallExpr n, A arg) {
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
            tArgs = dryValues/i;
        }
        if (n.getArgs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (Expression e : n.getArgs()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            args = dryValues/i;
        }
        return average(new Double[] {scope/3, tArgs/2, args});
    }

    public Double visit(MethodDeclaration n, A arg) {
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
        return body;
    }

    public Double visit(NameExpr n, A arg) {
        return new Double(0); // stub
    }

    public Double visit(NormalAnnotationExpr n, A arg) {
        n.getName().accept(this, arg);
        if (n.getPairs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (MemberValuePair m : n.getPairs()) {
                dryValues += m.accept(this, arg);
            }
            return dryValues/i;
        }
        return new Double(0);
    }

    public Double visit(NullLiteralExpr n, A arg) {
        return new Double(0);
    }

    public Double visit(ObjectCreationExpr n, A arg) {
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
            args = dryValues/i;
        }
        if (n.getAnonymousClassBody() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (BodyDeclaration member : n.getAnonymousClassBody()) {
                dryValues += member.accept(this, arg);
                i++;
            }
            aClassBody = dryValues/i;
        }
        return average(new Double[] {type/3, args, aClassBody});
    }

    public Double visit(PackageDeclaration n, A arg) {
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        return n.getName().accept(this, arg);
    }

    public Double visit(Parameter n, A arg) {
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        Double type = n.getType().accept(this, arg);
        Double id = n.getId().accept(this, arg);
        return average(new Double[] {type, id});
    }

    public Double visit(PrimitiveType n, A arg) {
        return new Double(1);
    }

    public Double visit(QualifiedNameExpr n, A arg) {
        return n.getQualifier().accept(this, arg);
    }

    public Double visit(ReferenceType n, A arg) {
        return n.getType().accept(this, arg);
    }

    public Double visit(ReturnStmt n, A arg) {
        if (n.getExpr() != null) {
            return n.getExpr().accept(this, arg);
        }
        return new Double(0);
    }

    public Double visit(SingleMemberAnnotationExpr n, A arg) {
        n.getName().accept(this, arg);
        return n.getMemberValue().accept(this, arg);
    }

    public Double visit(StringLiteralExpr n, A arg) {
        return new Double(1);
    }

    public Double visit(SuperExpr n, A arg) {
        if (n.getClassExpr() != null) {
            return n.getClassExpr().accept(this, arg);
        }
        return new Double(0);
    }

    public Double visit(SwitchEntryStmt n, A arg) {
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
            return dryValues/i;
        }
        return new Double(0);
    }

    public Double visit(SwitchStmt n, A arg) {
        n.getSelector().accept(this, arg);
        if (n.getEntries() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (SwitchEntryStmt e : n.getEntries()) {
                dryValues += e.accept(this, arg);
                i++;
            }
            return dryValues/i;
        }
        return new Double(0);

    }

    public Double visit(SynchronizedStmt n, A arg) {
        Double expr = n.getExpr().accept(this, arg);
        Double block = n.getBlock().accept(this, arg);
        return average(new Double[] {expr, block});
    }

    public Double visit(ThisExpr n, A arg) {
        if (n.getClassExpr() != null) {
            return n.getClassExpr().accept(this, arg);
        }
        return new Double(0);
    }

    public Double visit(ThrowStmt n, A arg) {
        return n.getExpr().accept(this, arg);
    }

    public Double visit(TryStmt n, A arg) {
        Double tBlock = new Double(0), cBlock = new Double(0), fBlock = new Double(0);
        tBlock = n.getTryBlock().accept(this, arg);
        if (n.getCatchs() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (CatchClause c : n.getCatchs()) {
                dryValues += c.accept(this, arg);
                i++;
            }
            cBlock = dryValues/i;
        }
        if (n.getFinallyBlock() != null) {
            fBlock = n.getFinallyBlock().accept(this, arg);
        }
        return average(new Double[] {tBlock/5, cBlock, fBlock});
    }

    public Double visit(TypeDeclarationStmt n, A arg) {
        return n.getTypeDeclaration().accept(this, arg);
    }

    public Double visit(TypeParameter n, A arg) {
        if (n.getTypeBound() != null) {
            Double dryValues = new Double(0);
            int i = 0;
            for (ClassOrInterfaceType c : n.getTypeBound()) {
                dryValues += c.accept(this, arg);
                i++;
            }
            return dryValues/i;
        }
        return new Double(0);
    }

    public Double visit(UnaryExpr n, A arg) {
        return n.getExpr().accept(this, arg);
    }

    public Double visit(VariableDeclarationExpr n, A arg) {
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
        return dryValues/i;
    }

    public Double visit(VariableDeclarator n, A arg) {
        n.getId().accept(this, arg);
        if (n.getInit() != null) {
            return n.getInit().accept(this, arg);
        }
        return new Double(0);
    }

    public Double visit(VariableDeclaratorId n, A arg) {
        return new Double(0);
    }

    public Double visit(VoidType n, A arg) {
        return new Double(0);
    }

    public Double visit(WhileStmt n, A arg) {
        Double cond = n.getCondition().accept(this, arg);
        Double body = n.getBody().accept(this, arg);
        return average(new Double[] {cond/2, body});
    }

    public Double visit(WildcardType n, A arg) {
        if (n.getExtends() != null) {
            n.getExtends().accept(this, arg);
        }
        if (n.getSuper() != null) {
            n.getSuper().accept(this, arg);
        }
        return new Double(0);
    }

    public Double visit(BlockComment n, A arg) {
        return new Double(0);
    }

    public Double visit(LineComment n, A arg) {
        return new Double(0);
    }


    // computes average dryness scores
    public Double average(Double[] list) {
        Double sum = new Double(0);
        for(Double elem : list) {
            sum += elem;
        }
        return sum/list.length;
    }

}
