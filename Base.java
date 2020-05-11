import java.util.*;

class Base {

    int variableOffset;
    int functionOffset;
    int offset;
    boolean overriden;

    ArrayList<ClassInfo> declarationsOffset;

    Base() {
        this.declarationsOffset = new ArrayList<>();
    }


    final String INT = "int";
    final String BOOLEAN = "boolean";
    final String INT_ARRAY = "int[]";
    final String BOOLEAN_ARRAY = "boolean[]";
    final String THIS = "this";

    public ArrayList<String> TYPES = new ArrayList<String>(Arrays.asList(INT, INT_ARRAY, BOOLEAN, BOOLEAN_ARRAY));

    public boolean areEqual(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    public boolean isPrimitiveType(String type) {

        return TYPES.contains(type);
    }

    public boolean isThis(String item) {
        return areEqual(item, THIS);
    }

    public boolean isInt(String item) {
        return areEqual(item, INT);
    }

    public boolean isBoolean(String item) {
        return areEqual(item, BOOLEAN);
    }

    public boolean isIntArray(String item) {
        return areEqual(item, INT_ARRAY);
    }

    public boolean isBooleanArray(String item) {
        return areEqual(item, BOOLEAN_ARRAY);
    }

}