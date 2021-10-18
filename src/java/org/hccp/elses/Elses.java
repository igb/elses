package org.hccp.elses;

import org.hccp.lang.AstPrinter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
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


    public static void main(String[]args) throws Exception {
        String argumentString = reconstructArgumentString(args);

        int angleStep=-1;
        double lineLength=-1;
        double initialXPosition=-1;
        double initialYPosition=-1;
        double dotRadius=-1;
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
                case "--dot-radius":
                case "-r":
                    dotRadius = Double.parseDouble(args[++i]);
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
        context.set(Context.DOT_RADIUS, dotRadius != -1 ? dotRadius : DEFAULT_LINE_LENGTH / 2);


        context.set(Context.ITERATIONS, iterations);
        context.set(Context.ARGUMENT_STRING, argumentString);





        byte[] output = runFile(source, context);

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(output);
        fos.flush();
        fos.close();






    }

    private static String reconstructArgumentString(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            sb.append(arg);
            sb.append(" ");
        }
        return sb.toString().trim();
    }


    private static byte[] runFile(String path, Context context) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        if (hadError) System.exit(65);
        return run(new String(bytes), context);
    }


    private static void runPrompt() throws Exception {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader =  new BufferedReader(reader);

        for(;;) {
            System.out.print("> ");
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            run(line, new Context());
            hadError=false;
        }
    }

    private static byte[] run(String source, Context context) throws Exception {

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        List<Expr> expressions = parse(tokens);


        Program program = compile(expressions);
        List<Expr.Literal> ilr = program.execute((Integer) context.get(Context.ITERATIONS));

        Backend backend = new SvgBackend();
        backend.intepret(ilr, context);
        return backend.getOutput();







    }

    public static List<Expr> parse(List<Token> tokens) {
        Parser parser = new Parser(tokens);

        List<Expr> expressions = new LinkedList<Expr>();

        boolean x = true;

        while(x) {
            Expr ast = parser.parse();

            if (ast != null) {
                expressions.add(ast);
            } else {
                break;
            }

        }
        return expressions;
    }

    public static Program compile(List<Expr> expressions) throws Exception {
        Iterator<Expr> iterator = expressions.iterator();
        Axiom axiom = compileAxiom(iterator.next());
        List<Rule> rules = compileRules(iterator);

        Program program = new Program();
        program.setAxiom(axiom);
        program.setRules(rules);

        return program;


    }

    private static List<Rule> compileRules(Iterator<Expr> iterator) {
        List<Rule> rules = new LinkedList<>();
        while (iterator.hasNext()) {
            Expr next = iterator.next();
            if (next instanceof Expr.Binary) {
                Rule rule = compileRule((Expr.Binary) next);
                rules.add(rule);
            }
        }
        return rules;
    }

    protected static Rule compileRule(Expr.Binary expr) {
        Expr.Binary ruleExpression = expr;
        Expr.Literal input = (Expr.Literal)ruleExpression.left;
        List<Expr.Literal> output = literalListToList((Expr.LiteralList)ruleExpression.right);
        return new Rule(input, output);
    }

    protected static List literalListToList(Expr.LiteralList literalList) {
        return literalListToList(literalList, new LinkedList<Expr.Literal>());
    }

    private static List literalListToList(Expr.LiteralList literalList, LinkedList<Expr.Literal> list) {
        list.add(literalList.head);
        if (literalList.tail == null) {
            return list;
        }
        return literalListToList(literalList.tail, list);
    }

    private static Axiom compileAxiom(Expr expr) throws Exception {
        if (expr instanceof Expr.Binary) {
            Expr.Binary binExpr = (Expr.Binary) expr;
            Expr.Literal left = (Expr.Literal) binExpr.left;
            Expr.LiteralList right = (Expr.LiteralList) binExpr.right;

            print(left);

            if (TokenType.AXIOM.equals(left.value)) {
                Axiom axiom = new Axiom(literalListToList(right));
                return axiom;
            }

        }

        throw new Exception("Invalid axiom.");

    }

    public static void print(Expr ast) {
        AstPrinter astPrinter = new AstPrinter();
        System.out.println(astPrinter.print(ast));
    }

    private static void printHelpMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("--angle,-a ANGLE\t\t\tthe angle to increment/decrement when interpreting '+' or '-' commands.\n\n");
        sb.append("--help,-h\t\t\tprints this message.\n\n");
        sb.append("--iterations,-i ITERATIONS\t\t\tthe number of rule-application iterations.\n\n");
        sb.append("--line-length,-l LINE LENGTH\t\t\tthe length of each pen move 'F'.\n\n");
        sb.append("--x-position,-x INITIAL X POSITION\t\t\tthe initial x position from which to begin drawing\n\n");
        sb.append("--y-position,-y INITIAL Y POSITION\t\t\tthe initial y position from which to begin drawing\n\n");
        sb.append("--dot-radius,-r DOT RADIUS\t\t\tthe radius of the circle drawn by a '@' command\n\n");


        System.out.println(sb.toString());
    }





    static void error(Token token, String message) {
       if (token.type == TokenType.EOF) {
           report(token.line, " at end", message);
       } else {
           report(token.line, " at '" + token.lexeme + "'", message);
       }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

}
