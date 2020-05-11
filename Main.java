import syntaxtree.*;
import visitor.*;
import java.io.*;

class Main {
    public static void main (String [] args){
		if(args.length == 0) {
			System.err.println("Usage: Main <file1> <file2> ... <fileN>");
			System.exit(1);
		}
		for(int i = 0 ; i < args.length ; i++) {

			FileInputStream fis = null;
			ClassInfo main = new ClassInfo();

			try {
				fis = new FileInputStream(args[i]);
				MiniJavaParser parser = new MiniJavaParser(fis);
				FirstVisitor firtstVisitor = new FirstVisitor();
				Goal root = parser.Goal();
				root.accept(firtstVisitor, main);

				SecondVisitor secondVisitor = new SecondVisitor();
				root.accept(secondVisitor, main);

				main.printOffsets();

			} catch (ParseException ex) {
				System.out.println(ex.getMessage());
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
			} catch(Exception ex){
				System.err.println(ex.getMessage());
			} finally {
				try {
					if (fis != null) fis.close();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		}
    }
}
