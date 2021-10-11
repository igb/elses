package org.hccp.lang;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        if (args.length != 1) {
            System.err.println("Usage generate_ast <output directory>");
            System.exit(64);

        }

        String outputDir = args[0];
        defineAst(outputDir, "org.hccp.elses", "Expr", Arrays.asList(
                "Binary: Expr left,Token operator,Expr right",
                "Literal: Object value",
                "LiteralList: Literal head,LiteralList tail"
        ));
    }

    private static void defineAst(String outputDir, String packageName, String baseName, List<String> types) throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package " + packageName + ";");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + "{");

        defineVisitor(writer, baseName, types);


        // the Ast classes

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // the base accept() method

        writer.println();
        writer.println(" public abstract <R> R accept(Visitor<R> visitor);");


        writer.println("}");
        writer.close();

    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tpublic interface Visitor<R> {");

        for (String type: types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() +");" );
        }
        writer.println("\t}");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("   public static class " + className + " extends " + baseName + " {");

       // constructor

        writer.println("        " + className  + "(" + fieldList + ") {");

       // Store params in fields
        String[] fields = fieldList.split(",");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");


        //Visitor pattern
        writer.println();
        writer.println("\t\t@Override");
        writer.println("\t\tpublic <R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
        writer.println("\t\t}");


        // FIelds

        writer.println();
        for (String field : fields) {
            writer.println("            public final " + field + ";");
        }

        writer.println("    }");


    }
}
