package org.hccp.elses;

import java.util.HashSet;
import java.util.List;

import static org.hccp.elses.TokenType.*;

public class SvgBackend extends Backend {

    HashSet<String> record = new HashSet<>();

    StringBuffer outputBuffer = new StringBuffer();


    @Override
    public void intepret(List<Expr.Literal> ilr, Context context) {
        outputBuffer.append("<svg xmlns=\"http://www.w3.org/2000/svg\"" +
                "   height=\"297.0mm\"\n" +
                "   width=\"420.0mm\"\n" +
                "   viewBox=\"0 0 420.0 297.0 \"\n" +
                "   version=\"1.1\"" +
                ">\n");
        for (int i = 0; i < ilr.size(); i++) {
            Expr.Literal instruction = ilr.get(i);
            if (instruction.value instanceof TokenType) {
                TokenType value = (TokenType) instruction.value;
                switch (value) {
                    case FORWARD:
                        System.out.println("draw line from " + context.getCurrentX() + ", " + context.getCurrentX() + " to " + calculateNewX(context) + ", " + calculateNewY(context));
                        String line = String.format("<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" />\n", context.getCurrentX(), context.getCurrentY(), calculateNewX(context), calculateNewY(context));

                        //optimize output by eliminating duplicate paths
                        if (!record.contains(line)) {
                            outputBuffer.append(line);
                            record.add(line);
                        }
                        context.set(Context.CURRENT_X_POS, calculateNewX(context));
                        context.set(Context.CURRENT_Y_POS, calculateNewY(context));
                        break;
                    case PEN_UP_FORWARD:
                        System.out.println("move without drawing from " + context.getCurrentX() + ", " + context.getCurrentX() + " to " + calculateNewX(context) + ", " + calculateNewY(context));
                        context.set(Context.CURRENT_X_POS, calculateNewX(context));
                        context.set(Context.CURRENT_Y_POS, calculateNewY(context));
                        break;
                    case DOT:
                        System.out.println("draw a dot at " + context.getCurrentX() + ", " + context.getCurrentX());
                        String dot = String.format("<circle cx=\"%f\" cy=\"%f\" r=\"%f\"/>\n", context.getCurrentX(), context.getCurrentY(), context.getDotRadius());

                        //optimize output by eliminating duplicate paths
                        if (!record.contains(dot)) {
                           outputBuffer.append(dot);
                           record.add(dot);
                        }
                        context.set(Context.CURRENT_X_POS, calculateNewX(context));
                        context.set(Context.CURRENT_Y_POS, calculateNewY(context));
                        break;
                    case PUSH:
                        System.out.println("push");
                        context.push();
                        break;
                    case POP:
                        System.out.println("pop");
                        context.pop();
                        break;
                    case LEFT_TURN:
                        System.out.println("turn left by " + context.get(Context.ANGLE_STEP) + " deg...new direction is " + calculateLeftTurnAngle(context));
                        break;
                    case RIGHT_TURN:
                        System.out.println("turn right by " + context.get(Context.ANGLE_STEP) + " deg...new direction is " + calculateRightTurnAngle(context));
                        break;
                    default:
                        System.out.println("unsupported instruction: " + value);

                }
            }






        }

        outputBuffer.append("</svg>\n");



    }

    @Override
    public byte[] getOutput() {
        return outputBuffer.toString().getBytes();
    }



    private double toRadians(double angle) {
        return (angle * (Math.PI / 180));
    }
    private double calculateNewX(Context context) {
        return (context.getCurrentX() + (context.getCurrentLineLength() * Math.cos(toRadians(context.getCurrentDirection()))));

    }

    private double calculateNewY(Context context) {
        return (context.getCurrentY() + (context.getCurrentLineLength() * Math.sin(toRadians(context.getCurrentDirection()))));

    }

    private int calculateRightTurnAngle(Context context) {

        int newCurrentDirection = (context.getCurrentDirection() - context.getAngleStep()) % 360;
        context.set(Context.CURRENT_DIRECTION, newCurrentDirection);
        return newCurrentDirection;

    }

    private int calculateLeftTurnAngle(Context context) {
        int newCurrentDirection = (context.getCurrentDirection() + context.getAngleStep()) % 360;
        context.set(Context.CURRENT_DIRECTION, newCurrentDirection);
        return newCurrentDirection;
    }
}
