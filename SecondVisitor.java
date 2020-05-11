import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;


public class SecondVisitor extends GJDepthFirst<String, ClassInfo> {
    final String INT = "int";
    final String BOOLEAN = "boolean";
    final String INT_ARRAY = "int[]";
    final String BOOLEAN_ARRAY = "boolean[]";
    final String THIS = "this";


    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, ClassInfo info) throws Exception {
        n.f0.accept(this, info);

        for (int i=0; i<n.f1.size(); i++) {
            n.f1.elementAt(i).accept(this,info);
        }

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, ClassInfo info) throws Exception {

        ClassInfo Main = info.getVariable(n.f1.accept(this, info)).getFunction("main");

        for (int i=0; i<n.f14.size(); i++) {
            n.f14.elementAt(i).accept(this, Main);
        }

        for (int i=0; i<n.f15.size(); i++) {
            n.f15.elementAt(i).accept(this, Main);
        }

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, ClassInfo info) throws Exception {

        ClassInfo Class = info.getVariable(n.f1.accept(this, info));

        for ( int i=0; i<n.f3.size(); i++ ) {
            n.f3.elementAt(i).accept(this, Class);
        }
        for ( int i=0; i<n.f4.size(); i++ ) {
            n.f4.elementAt(i).accept(this, Class);
        }

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, ClassInfo info) throws Exception {

        ClassInfo Class = info.getVariable(n.f1.accept(this, info));

        for ( int i=0; i<n.f5.size(); i++ ) {
            n.f5.elementAt(i).accept(this, Class);
        }
        for ( int i=0; i<n.f6.size(); i++ ) {
            n.f6.elementAt(i).accept(this, Class);
        }

        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, ClassInfo info) throws Exception {

        String type = n.f0.accept(this, info);

        if ( !info.isValidType(type) ) {
            throw new Exception("Type <" + type + "> is invalid.");
        }

        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration n, ClassInfo info) throws Exception {

        String type = n.f1.accept(this, info);
        String name = n.f2.accept(this, info);

        if ( !info.isValidType(type) ) {
            throw new Exception("Method <" + name + ">  has invalid return type <" + type + ">.");
        }

        ClassInfo Method = info.getFunction(name);

        if (n.f4.present()) {
            n.f4.accept(this, Method);
        }

        for ( int i=0; i < n.f7.size(); i++ ) {
            n.f7.elementAt(i).accept(this, Method);
        }

        for ( int i = 0; i < n.f8.size(); i++ ) {
            n.f8.elementAt(i).accept(this, Method);
        }

        String exp = n.f10.accept(this, Method);

        if ( exp == null ) {
            throw new Exception("Method overload is not allowed. Return type can't be null.. Check  method <" + name + ">.");
        }

        else if ( Method.notEqualTypes(exp, Method.type) ) {
            throw new Exception("Method overload is not allowed. Defined and actual return types are different. Check  method <" + name + ">.");
        }

        return null;
    }


    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList n, ClassInfo info) throws Exception {

        n.f0.accept(this, info);
        n.f1.accept(this, info);

        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, ClassInfo info) throws Exception {

        String type = n.f0.accept(this, info);
        String name = n.f1.accept(this, info);

        if ( info.isValidType(name) ) {
            throw new Exception("Type <" + type + "> is invalid.");
        }

        return null;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, ClassInfo info) throws Exception {

        for ( int i=0; i<n.f0.size(); i++ ) {
            n.f0.elementAt(i).accept(this,info);
        }

        return null;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, ClassInfo info) throws Exception {

        n.f1.accept(this, info);

        return null;
    }


    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, ClassInfo info) throws Exception {

        return n.f0.accept(this, info);
    }

    /**
     * f0 -> BooleanArrayType()
     *       | IntegerArrayType()
     */
    public String visit(ArrayType n, ClassInfo info) throws Exception {

        return n.f0.accept(this, info);
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(BooleanArrayType n, ClassInfo info) throws Exception {

        return BOOLEAN_ARRAY;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, ClassInfo info) throws Exception {

        return INT_ARRAY;
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, ClassInfo info) throws Exception {

        return BOOLEAN;
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, ClassInfo info) throws Exception {

        return INT;
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, ClassInfo info) throws Exception {

        n.f0.accept(this, info);

        return null;
    }


    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, ClassInfo info) throws Exception {

        for ( int i=0; i<n.f1.size(); i++ ) {
            n.f1.elementAt(i).accept(this, info);
        }

        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, ClassInfo info) throws Exception {
        String id = n.f0.accept(this, info);
        String exp = n.f2.accept(this, info);
        String expType = info.isThis(exp) ? info.parent.name :  info.getVariableType(exp);

        if ( expType == null ) {
           if ( !info.isPrimitiveType(exp) ) {
               throw new Exception("Invalid right side of assignment.");
           } else {
               expType = exp;
           }
        }

        if ( info.notEqualTypes(id, expType) && !info.inheritanceCheck(info, expType, info.getVariableType(id)) ) {
            throw new Exception("Type of <" + id + "> is " + info.getVariableType(id) + " and <" + exp + "> are not the same.");
        }

        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, ClassInfo info) throws Exception {

        String id = n.f0.accept(this, info);
        String type = info.getVariableType(id);

        if (!info.isIntArray(type) && !info.isBooleanArray(type) ) {
            throw new Exception("Invalid left-hand side type. Expected type to be int[] or boolean[] but have <" + type + ">.");
        }

        String bracketExp = n.f2.accept(this, info);

        if (!info.isInt(bracketExp) && !info.isInt(info.getVariableType(bracketExp))) {
            throw new Exception("Intex of array <" + id + "> should be int.");
        }

        String assignmentExp = n.f5.accept(this, info);

        if (!info.isInt(assignmentExp) && !info.isInt(info.getVariableType(assignmentExp))) {
            throw new Exception("Invalid assignment. Expected type to be int but have <" + assignmentExp + ">.");
        }

        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, ClassInfo info) throws Exception {

        String exp = n.f2.accept(this, info);

        if (!info.isBoolean(exp) && !info.isBoolean(info.getVariableType(exp))) {
            throw new Exception("Invalid if expression. Expected type to be boolean but have <" + exp + ">.");
        }
        n.f4.accept(this, info);
        n.f6.accept(this, info);
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, ClassInfo info) throws Exception {

        String exp = n.f2.accept(this, info);

        if (!info.isBoolean(exp) && !info.isBoolean(info.getVariableType(exp))) {
            throw new Exception("Invalid while expression. Expected type to be boolean but have <" + exp + ">.");
        }

        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, ClassInfo info) throws Exception {

        String exp = n.f2.accept(this, info);

        if (!info.isInt(exp) && !info.isInt(info.getVariableType(exp))) {
            throw new Exception("Invalid print expression. Expected type to int but have <" + exp + ">.");
        }

        return null;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | Clause()
     */
    public String visit(Expression n, ClassInfo info) throws Exception {
        String exp = n.f0.accept(this, info);
        return exp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, ClassInfo info) throws Exception {

        String left = n.f0.accept(this, info);
        String right = n.f2.accept(this, info);

        if ( info.notEqualTypes(left, INT) || info.notEqualTypes(right, INT)) {
            throw new Exception("Invalid COMPARISON( < ) expression. Must be between integers but have: left <" + left + ">, right <" + right + ">,");
        }

        return BOOLEAN;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, ClassInfo info) throws Exception {

        String left = n.f0.accept(this, info);
        String right = n.f2.accept(this, info);

        if ( info.notEqualTypes(left, INT) || info.notEqualTypes(right, INT)) {
            throw new Exception("Invalid PLUS( + ) expression. Must be between integers but have: left <" + left + ">, right <" + right + ">,");
        }

        return INT;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, ClassInfo info) throws Exception {

        String left = n.f0.accept(this, info);
        String right = n.f2.accept(this, info);

        if ( info.notEqualTypes(left, INT) || info.notEqualTypes(right, INT)) {
            throw new Exception("Invalid MINUS( - ) expression. Must be between integers but have: left <" + left + ">, right <" + right + ">,");
        }

        return INT;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, ClassInfo info) throws Exception {

        String left = n.f0.accept(this, info);
        String right = n.f2.accept(this, info);

        if ( info.notEqualTypes(left, INT) || info.notEqualTypes(right, INT)) {
            throw new Exception("Invalid TIMES( * ) expression. Must be between integers but have: left <" + left + ">, right <" + right + ">.");
        }

        return INT;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, ClassInfo info) throws Exception {

        String id = n.f0.accept(this, info);

        if ( info.notEqualTypes(id, INT_ARRAY) && info.notEqualTypes(id, BOOLEAN_ARRAY)) {
            throw new Exception("Invalid left-hand side type. Expected type to be int[] or boolean[].");
        }

        String exp = n.f2.accept(this, info);

        if ( info.notEqualTypes(exp, INT)) {
            throw new Exception("Intex of array <" + id + "> should be int.");
        }

        return INT;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, ClassInfo info) throws Exception {

        String id = n.f0.accept(this, info);

        if ( info.notEqualTypes(id, INT_ARRAY) && info.notEqualTypes(id, BOOLEAN_ARRAY)) {
            throw new Exception("Invalid left-hand side type. Expected type to be int[] or boolean[].");
        }

        return INT;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, ClassInfo info) throws Exception {

        String name = n.f0.accept(this, info);
        String method = n.f2.accept(this, info);
        ClassInfo function;
        if ( info.isThis(name)) {
            function = info.findFunction(method);
        }
        else {
            ClassInfo outer = info.getRoot().getVariable(info.getVariableType(name));

            function = outer.findFunction(method);
        }

        if (function == null) {
            throw new Exception("Method <" + method + "> is not defined.");
        }

        if(n.f4.present()) {

            String[] callArgs = n.f4.accept(this, info).split(",");

            if(function.arguments == null || function.arguments.size() != callArgs.length) {
                throw new Exception("Wrong number of arguments for <" + function.name + ">.");
            }
            for(int i = 0 ; i < function.arguments.size() ; i++) {
                if(info.notEqualTypes(callArgs[i], function.arguments.get(i).type) && !info.inheritanceCheck(info, callArgs[i], function.arguments.get(i).type)) {
                    throw new Exception("Method overload is not allowed.");
                }
            }

        }
        else if(function.arguments.size() != 0) {
            throw new Exception("Wrong number of arguments for <" + function.name + ">. Expected " + function.arguments.size() + " but have " + 0 + ".");
        }
        return function.type;
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, ClassInfo info) throws Exception {

        String left = n.f0.accept(this, info);
        String right = n.f2.accept(this, info);

        if ( info.notEqualTypes(left, BOOLEAN) && info.notEqualTypes(right, BOOLEAN)) {
            throw new Exception("Invalid AND( && ) expression. Must be between booleans but have: left <" + left + ">, right <" + right + ">,");
        }

        return BOOLEAN;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, ClassInfo info) throws Exception {

        return n.f0.accept(this, info) + "," +  n.f1.accept(this, info);
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, ClassInfo info) throws Exception {

        StringBuffer sb = new StringBuffer();

        for ( int i=0; i< n.f0.size(); i++ ) {
            sb.append(n.f0.elementAt(i).accept(this, info));
            if ( i != n.f0.size() - 1 ) sb.append(",");
        }

        return sb.toString();
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, ClassInfo info) throws Exception {

        return n.f1.accept(this, info);
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    public String visit(Clause n, ClassInfo info) throws Exception {
        return n.f0.accept(this, info);
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, ClassInfo info) throws Exception {
        return n.f0.accept(this, info);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, ClassInfo info) throws Exception {
        return INT;
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, ClassInfo info) throws Exception {
        return BOOLEAN;
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, ClassInfo info) throws Exception {
        return BOOLEAN;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, ClassInfo info) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, ClassInfo info) throws Exception {
        return THIS;
    }

    /**
     * f0 -> BooleanArrayAllocationExpression()
     *       | IntegerArrayAllocationExpression()
     */
    public String visit(ArrayAllocationExpression n, ClassInfo info) throws Exception {
        return n.f0.accept(this, info);
    }

    /**
     * f0 -> "new"
     * f1 -> "boolean"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(BooleanArrayAllocationExpression n, ClassInfo info) throws Exception {

        String exp = n.f3.accept(this, info);

        if ( info.notEqualTypes(exp, INT)) {
            throw new Exception("Expression in array allocation should be int.");
        }
        return BOOLEAN_ARRAY;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(IntegerArrayAllocationExpression n, ClassInfo info) throws Exception {

        String exp = n.f3.accept(this, info);
        if ( info.notEqualTypes(exp, INT)) {
            throw new Exception("Expression in array allocation should be int.");
        }
        return INT_ARRAY;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, ClassInfo info) throws Exception {
        String id = n.f1.accept(this, info);
        return id;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public String visit(NotExpression n, ClassInfo info) throws Exception {
        String clause = n.f1.accept(this, info);

        if ( info.notEqualTypes(clause, BOOLEAN)) {
            throw new Exception("Invalid NOT( ! ) expression. Must have boolean but have  <" + clause + ">.");
        }

        return BOOLEAN;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, ClassInfo info) throws Exception {
        return n.f1.accept(this, info);
    }
}