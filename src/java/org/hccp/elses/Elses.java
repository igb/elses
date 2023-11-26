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



    public static final double DEFAULT_LINE_LENGTH = 10;
    public static final double DEFAULT_X_POSITION = 150;
    public static final double DEFAULT_Y_POSITION = 25;
    public static final int DEFAULT_ANGLE_STEP = 45;
    private static final double DEFAULT_HEIGHT = 420.0;
    private static final double DEFAULT_WIDTH = 291.0;

    public static void main(String[]args) throws Exception {
        String argumentString = reconstructArgumentString(args);

        int angleStep=-1;
        int angleStepLowerLimit = -1;
        int angleStepUpperLimit = -1;
        boolean randomizeAngleStep = false;

        double height = -1;
        double width = -1;

        double lineLength=-1;
        double lineLengthLowerLimit = -1;
        double lineLengthUpperLimit = -1;
        boolean randomizeLineLength = false;


        double initialXPosition=-1;
        double initialYPosition=-1;
        double rotation=0.0;

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
                    String angleArg = args[++i];
                    if (angleArg.indexOf("-") > 0) {
                        angleStepLowerLimit = Integer.parseInt(angleArg.split("-")[0]);
                        angleStepUpperLimit = Integer.parseInt(angleArg.split("-")[1]);
                        randomizeAngleStep=true;
                    } else {
                        angleStep = Integer.parseInt(angleArg);
                    }
                    break;
                case "--line-length":
                case "-l":
                    String lineArg = args[++i];
                    if (lineArg.indexOf("-") > 0) {
                        lineLengthLowerLimit = Double.parseDouble(lineArg.split("-")[0]);
                        lineLengthUpperLimit = Double.parseDouble(lineArg.split("-")[1]);
                        randomizeLineLength=true;
                    } else {
                        lineLength = Double.parseDouble(lineArg);
                    }
                    break;
                case "--x-position":
                case "-x":
                    initialXPosition = Double.parseDouble(args[++i]);
                    break;
                case "--y-position":
                case "-y":
                    initialYPosition = Double.parseDouble(args[++i]);
                    break;
                case "--rotation":
                    rotation = Double.parseDouble(args[++i]);
                    break;
                case "--dot-radius":
                case "-r":
                    dotRadius = Double.parseDouble(args[++i]);
                    break;
                case "--width":
                case "-w":
                    width = Double.parseDouble(args[++i]);
                    break;
                case "--height":
                    height = Double.parseDouble(args[++i]);
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

        context.set(Context.HEIGHT, height != -1 ? height : DEFAULT_HEIGHT);
        context.set(Context.WIDTH, width != -1 ? width : DEFAULT_WIDTH);




        context.set(Context.ANGLE_STEP, angleStep != -1 ? angleStep : DEFAULT_ANGLE_STEP);

        if (randomizeAngleStep) {
            context.set(Context.RANDOMIZE_ANGLE_STEP, true);
            context.set(Context.ANGLE_STEP_LOWER_LIMIT, angleStepLowerLimit);
            context.set(Context.ANGLE_STEP_UPPER_LIMIT, angleStepUpperLimit);

        } else {
            context.set(Context.RANDOMIZE_ANGLE_STEP, false);
        }


        context.set(Context.LINE_LENGTH, lineLength != -1 ? lineLength : DEFAULT_LINE_LENGTH);


        if (randomizeLineLength) {
            context.set(Context.RANDOMIZE_LINE_LENGTH, true);
            context.set(Context.LINE_LENGTH_LOWER_LIMIT, lineLengthLowerLimit);
            context.set(Context.LINE_LENGTH_UPPER_LIMIT, lineLengthUpperLimit);

        } else {
            context.set(Context.RANDOMIZE_LINE_LENGTH, false);
        }


        context.set(Context.CURRENT_X_POS, initialXPosition != -1 ? initialXPosition : DEFAULT_X_POSITION);
        context.set(Context.CURRENT_Y_POS, initialYPosition != -1 ? initialYPosition : DEFAULT_Y_POSITION);
        context.set(Context.DOT_RADIUS, dotRadius != -1 ? dotRadius : lineLength != -1 ? lineLength : DEFAULT_LINE_LENGTH / 2);
        context.set(Context.ROTATION, rotation);

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
        sb.append(formatHelpMessage("--angle,-a ANGLE", "The angle to increment/decrement when interpreting '+' or '-' commands." +
                " A range of angle values can be given for the interpreter to randomly chooose from at each step by using the form of 'n-m'." +
                " For example the argument -a 20-35 would allow the interpreter to choose values between 20 degrees and 35 degrees at each step. The default value, if no angle or range is passed via this argument, is 45 degrees."));
        sb.append(formatHelpMessage("--help,-h", "Prints this message."));
        sb.append(formatHelpMessage("--iterations,-i ITERATIONS", "The number of rule-application iterations."));
        sb.append(formatHelpMessage("--line-length,-l LINE_LENGTH", "The length of each pen move 'F'." +
                " A range of line length values can be given for the interpreter to randomly chooose from at each step by using the form of 'n-m'." +
                " For example the argument -l 2-10 would allow the interpreter to choose values between 2 and 10 at each step. If no line length is specified, a default line length of 10 will be used."));
        sb.append(formatHelpMessage("--x-position,-x INITIAL_X_POSITION", "The initial x position from which to begin drawing. The default is 150 (the center point for a landscape-oriented A3 paper."));
        sb.append(formatHelpMessage("--y-position,-y INITIAL_Y_POSITION", "The initial y position from which to begin drawing. The default is 25."));
        sb.append(formatHelpMessage("--rotation ROTATION", "The number of degrees to rotate the drawing after initial rendering."));
        sb.append(formatHelpMessage("--dot-radius,-r DOT_RADIUS", "The radius of the circle drawn by a '@' command. If no radius is selected, a default radius of 1/2 of the current line length will be used."));
        sb.append(formatHelpMessage("--width,-w WIDTH", "Sets the width of the draw-able page to the provided value. The default is 291 as this matches the width of an A3 paper in portrait mode."));
        sb.append(formatHelpMessage("--height HEIGHT", "Sets the height of the draw-able page to the provided value. The default is 420 as this matches the height of an A3 paper in portrait mode."));



        System.out.println(sb.toString());
    }

    private static String formatHelpMessage(String command, String message) {
        return formatHelpMessage(command, message, 80);
    }

    private static String formatHelpMessage(String command, String message, int lineLength) {
        StringBuffer sb = new StringBuffer();
        String padding = "     ";

        int indentLength = command.length() + padding.length();
        String[] words = message.split(" ");


        StringBuffer currentLine = new StringBuffer(lineLength);
        currentLine.append(command + padding);

        for (int i = 0; i < words.length; i++) {


            if (currentLine.length() + words[i].length() + 1 > lineLength) {
                currentLine.append("\n");
                sb.append(currentLine);
                currentLine = new StringBuffer(lineLength);
                for (int j = 0; j < indentLength; j++) {
                    currentLine.append(" ");
                }
            }
            currentLine.append(" " + words[i]);
        }

        sb.append(currentLine + "\n");
        return sb.toString() + "\n";
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
