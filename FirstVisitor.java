import visitor.GJDepthFirst;
import syntaxtree.*;


public class FirstVisitor extends GJDepthFirst<String, ClassInfo> {

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, ClassInfo info) throws Exception {
        info.name = "GOAL";
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

        ClassInfo Class = info.addVariables(info, n.f1.accept(this, info), "class", null);
        ClassInfo Main = Class.addFunctions(Class, "main", "void", null, null);
        ClassInfo NameInfo = Main.addVariables(Main, n.f11.accept(this, info), "String[]", null);

        for (int i=0; i<n.f14.size(); i++) {
            n.f14.elementAt(i).accept(this, Main);
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

        String name = n.f1.accept(this, info);

        if ( info.hasVariable(name) ) {
            throw new Exception("Class <" + name + "> has already been defined!");
        }
        ClassInfo newClass = info.addVariables(info, name, name, null);

        for ( int i=0; i<n.f3.size(); i++ ) {
            n.f3.elementAt(i).accept(this, newClass);
        }
        for ( int i=0; i<n.f4.size(); i++ ) {
            n.f4.elementAt(i).accept(this, newClass);
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

        String subClass = n.f1.accept(this, info);
        String superClass = n.f3.accept(this, info);
        ClassInfo superClassInfo = info.getVariable(superClass);

        if ( !(info.hasVariable(superClass)) || superClassInfo == null ) {
            throw new Exception("Class <" + superClass + "> is not defined!\nSuperclass must be defined before subclass.");
        }
        ClassInfo subClassNode = superClassInfo.addVariables(info, subClass, subClass, null);

        if ( n.f5.size() != 0 ) {
            subClassNode.variableOffset = subClassNode.parent.variableOffset;
            for ( int i=0; i<n.f5.size(); i++ ) {
                n.f5.elementAt(i).accept(this, subClassNode);
            }
        }

        if ( n.f6.size() != 0 ) {
            subClassNode.functionOffset = subClassNode.parent.functionOffset;
            for ( int i=0; i<n.f6.size(); i++ ) {
                n.f6.elementAt(i).accept(this, subClassNode);
            }
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
        String name = n.f1.accept(this, info);

        if( info.hasVariable(name) ) {
            throw new Exception("Variable <" + name +"> has already been deifned.");
        }

        ClassInfo var = info.addVariables(info, name, type, info.variableOffset);

        info.addVariableOffset(info, type);
        info.declarationsOffset.add(var);

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

        if ( info.hasFunction(name) ) {
            throw new Exception("Method <" + name + "> has already been defined!");
        }
        ClassInfo temp = info.findFunction(name);

        ClassInfo method;

        if(temp == null) {
            method = info.addFunctions(info, name, type, info.functionOffset, null);
            info.addFunctionOffset();
            info.declarationsOffset.add(method);
        }
        else {
            method = info.addFunctions(info, name, type, temp.functionOffset, true);
        }

        if (n.f4.present()) {
            n.f4.accept(this, method);
        }

        if(info.parent.isMethodOverloaded(name, method)) {
            throw new Exception("Method overload is not allowed. Check arguments of method <" + name + ">");
        }


        for ( int i=0; i<n.f7.size(); i++ ) {
            n.f7.elementAt(i).accept(this, method);
        }

        n.f10.accept(this, method);

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

        if ( info.hasVariable(name) ) {
            throw new Exception("Variable <" + name + "> has already been defined!");
        }
        ClassInfo arg = info.addVariables(info, name, type, null);
        info.addArguments(arg);

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

        return "boolean[]";
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, ClassInfo info) throws Exception {

        return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, ClassInfo info) throws Exception {

        return "boolean";
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, ClassInfo info) throws Exception {

        return "int";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, ClassInfo info) throws Exception {

        return n.f0.toString();
    }
}