all:   compile

compile: 
	java -jar ../jtb132di.jar -te ../minijava.jj
	java -jar ../javacc5.jar ../minijava-jtb.jj
	javac Main.java

clean:
	rm -f *.class *~
	rm  JavaCharStream.java MiniJavaParser* minijava-jtb.jj Token* ParseException.java
	rm -rf syntaxtree/ visitor/

correct:
	java Main ./tests/correct/Add.java  ./tests/correct/ArrayTest.java  ./tests/correct/BinaryTree.java  ./tests/correct/BubbleSort.java  ./tests/correct/CallFromSuper.java  ./tests/correct/Classes.java  ./tests/correct/DerivedCall.java  ./tests/correct/Example1.java  ./tests/correct/Factorial.java  ./tests/correct/FieldAndClassConflict.java  ./tests/correct/LinearSearch.java  ./tests/correct/LinkedList.java  ./tests/correct/Main.java  ./tests/correct/ManyClasses.java  ./tests/correct/MoreThan4.java  ./tests/correct/OutOfBounds1.java  ./tests/correct/Overload2.java  ./tests/correct/QuickSort.java  ./tests/correct/ShadowBaseField.java  ./tests/correct/ShadowField.java  ./tests/correct/test06.java  ./tests/correct/test07.java  ./tests/correct/test15.java  ./tests/correct/test17.java  ./tests/correct/test20.java  ./tests/correct/test62.java  ./tests/correct/test73.java  ./tests/correct/test82.java  ./tests/correct/test93.java  ./tests/correct/test99.java  ./tests/correct/TreeVisitor.java

error:
	java Main ./tests/error/BadAssign2.java   ./tests/error/BadAssign.java   ./tests/error/BubbleSort-error.java   ./tests/error/Classes-error.java   ./tests/error/DoubleDeclaration1.java   ./tests/error/DoubleDeclaration4.java   ./tests/error/DoubleDeclaration6.java   ./tests/error/Factorial-error.java   ./tests/error/LinearSearch-error.java   ./tests/error/LinkedList-error.java   ./tests/error/MoreThan4-error.java   ./tests/error/NoMatchingMethod.java   ./tests/error/NoMethod.java   ./tests/error/Overload1.java   ./tests/error/QuickSort-error.java   ./tests/error/test18.java   ./tests/error/test21.java   ./tests/error/test35.java   ./tests/error/test52.java   ./tests/error/test68.java   ./tests/error/TreeVisitor-error.java   ./tests/error/UseArgs.java
