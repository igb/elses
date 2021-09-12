package org.hccp.elses;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Elses {

    private static boolean hadError = false;


    public static final char FWD = 'F'; // Move forward by line length drawing a line
    public static final char PU_FWD ='f'; // Move forward by line length without drawing a line
    public static final char LT = '+'; // Turn left by turning angle
    public static final char RT = '-'; //Turn right by turning angle
    public static final char REV = '|';	// Reverse direction (ie: turn by 180 degrees)
    public static final char PUSH = '['; // Push current drawing state onto stack
    public static final char POP= ']'; //  Pop current drawing state from the stack
    public static final char INCR= '#'; // Increment the line width by line width increment
    public static final char DECR = '!'; // Decrement the line width by line width increment
    public static final char DOT = '@'; // Draw a dot with line width radius
    public static final char OPEN_POLY = '{'; // Open a polygon
    public static final char CLOSE_POLY = '}'; // Close a polygon and fill it with fill colour
    public static final char MULTIPLY_LINE_LENGTH = '>'; // Multiply the line length by the line length scale factor
    public static final char DIVIDE_LINE_LENGTH = '<'; // Divide the line length by the line length scale factor
    public static final char SWAP = '&'; // Swap the meaning of + and -
    public static final char DECR_ANGLE = '('; // Decrement turning angle by turning angle increment
    public static final char INCR_ANGLE = ')'; // Increment turning angle by turning angle increment
    public static final double DEFAULT_LINE_LENGTH = 10;
        private static final double DEFAULT_X_POSITION = 100;
        private static final double DEFAULT_Y_POSITION = 100;


    public static void main(String[]args) throws IOException {
        int angleStep=-1;
        double lineLength=-1;
        double initialXPosition=-1;
        double initialYPosition=-1;
        int iterations=-1;
        String source=null;
        String outputFile=null;
        if (args.length == 0) {
            runPrompt();
            System.exit(0);
        }


        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch(arg) {
                case "--angle":
                case "-a":
                    angleStep = Integer.parseInt(args[++i]);
                    break;
                case "--line-length":
                case "-l":
                    lineLength = Double.parseDouble(args[++i]);
                    break;
                case "--x-position":
                case "-x":
                    initialXPosition = Double.parseDouble(args[++i]);
                    break;
                case "--y-position":
                case "-y":
                    initialYPosition = Double.parseDouble(args[++i]);
                    break;
                case "--iterations":
                case "-i":
                    iterations = Integer.parseInt(args[++i]);
                    break;
                case "--help":
                case "-h":
                    printHelpMessage();
                    System.exit(0);
                default: // the only two naked or un-flagged parameters are source and outfile and specific order is set/expected
                    if (source == null) {
                        source=args[i];
                    } else {
                        outputFile=args[i];
                    }
            }
        }




        Context context = new Context();
        context.set(Context.CURRENT_DIRECTION, 90);

        context.set(Context.ANGLE_STEP, angleStep);
        context.set(Context.CURRENT_X_POS, initialXPosition != -1 ? initialXPosition : DEFAULT_X_POSITION);
        context.set(Context.CURRENT_Y_POS, initialYPosition != -1 ? initialYPosition : DEFAULT_Y_POSITION);
        context.set(Context.LINE_LENGTH, lineLength != -1 ? lineLength : DEFAULT_LINE_LENGTH);





        runFile(source);


        System.out.println("parsing...");
        FileInputStream fis = new FileInputStream(source);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
        String axiom = reader.readLine();
        List<Rule> rules = new LinkedList<Rule>();
        System.out.println("parsing...");
        while (true) {
            String rule = reader.readLine();
            if (rule == null) break;
            rules.add(parseRule(rule));
        }




        System.out.println("compiling...");
        String program = compile(axiom, rules, iterations);

        System.out.println("program = " + program);

        System.out.println("interpreting...");

        Interpeter interpreter = new Interpeter();
        interpreter.intepret(program, context);

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(interpreter.getOutput().getBytes());
        fos.flush();
        fos.close();




    }



    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes));

        if (hadError) System.exit(65);

    }


    private static void runPrompt() throws IOException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader =  new BufferedReader(reader);

        for(;;) {
            System.out.print("> ");
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError=false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);


        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            System.out.println(token);
        }
    }

    private static void printHelpMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("--angle,-a ANGLE\t\t\tthe angle to increment/decrement when interpreting '+' or '-' commands.\n");
        sb.append("--help,-h\t\t\tprints this message.\n");
        sb.append("--iterations,-i ITERATIONS\t\t\tthe number of rule-application iterations.\n");


        System.out.println(sb.toString());
    }

    public static String compile(String axiom, List<Rule> rules, int iterations) {
        String program = axiom;

        for (int i = 0; i < iterations; i++) {
            StringBuffer programBuffer = new StringBuffer();
            char[] programChars = program.toCharArray();
            for (int j = 0; j < programChars.length; j++) {
                char programChar = programChars[j];
                boolean matched = false;
                for (int k = 0; k < rules.size(); k++) {
                    Rule rule = rules.get(k);
                    char x = rule.getInput().toCharArray()[0];
                    if (rule.getInput().toCharArray()[0] == programChar) {
                        matched = true;
                        programBuffer.append(rule.getOutput());
                    }
                }
                if (!matched) {
                    programBuffer.append(programChar);
                }
            }
            program = programBuffer.toString();

        }
        return program;
    }


    public static Rule parseRule(String ruleString) {
        String[] ruleParts = ruleString.split("->");
        Rule rule = new Rule(ruleParts[0], ruleParts[1]);
        return rule;
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

}
