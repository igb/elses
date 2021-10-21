package org.hccp.elses;

import java.util.HashSet;
import java.util.List;

public class SvgBackend extends Backend {

    HashSet<String> record = new HashSet<>();

    StringBuffer outputBuffer = new StringBuffer();


    @Override
    public void intepret(List<Expr.Literal> ilr, Context context) {
        outputBuffer.append("<!-- GENERATED BY:\n\n elses " + context.get(Context.ARGUMENT_STRING) + "\n\n -->\n\n");

        outputBuffer.append("<svg xmlns=\"http://www.w3.org/2000/svg\"" +
                "   width=\"" + context.get(Context.WIDTH) + "mm\"\n" +
                "   height=\"" + context.get(Context.HEIGHT) + "mm\"\n" +
                "   viewBox=\"0 0 " + context.get(Context.WIDTH) + " " + context.get(Context.HEIGHT) + "\"\n" +
                "   version=\"1.1\"" +
                ">\n");
        for (int i = 0; i < ilr.size(); i++) {
            Expr.Literal instruction = ilr.get(i);
            if (instruction.value instanceof TokenType) {
                TokenType value = (TokenType) instruction.value;
                switch (value) {
                    case FORWARD:
                        Coordinates newCoordinates = getNewCoordinates(context);

                        System.out.println("draw line from " + context.getCurrentX() + ", " + context.getCurrentX() + " to " + newCoordinates.getX() + ", " + newCoordinates.getY());
                        String line = String.format("<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" />\n", context.getCurrentX(), context.getCurrentY(), newCoordinates.getX(), newCoordinates.getY());

                        //optimize output by eliminating duplicate paths
                        if (!record.contains(line)) {
                            outputBuffer.append(line);
                            record.add(line);
                        }

                        context.set(Context.CURRENT_X_POS, newCoordinates.getX());
                        context.set(Context.CURRENT_Y_POS, newCoordinates.getY());
                        break;
                    case PEN_UP_FORWARD:

                        Coordinates newPenUpCoordinates = getNewCoordinates(context);

                        System.out.println("move without drawing from " + context.getCurrentX() + ", " + context.getCurrentX() + " to " + newPenUpCoordinates.getX() + ", " + newPenUpCoordinates.getY());
                        context.set(Context.CURRENT_X_POS, newPenUpCoordinates.getX());
                        context.set(Context.CURRENT_Y_POS, newPenUpCoordinates.getY());
                        break;
                    case DOT:
                        System.out.println("draw a dot at " + context.getCurrentX() + ", " + context.getCurrentX());
                        String dot = String.format("<circle cx=\"%f\" cy=\"%f\" r=\"%f\"/>\n", context.getCurrentX(), context.getCurrentY(), context.getDotRadius());

                        //optimize output by eliminating duplicate paths
                        if (!record.contains(dot)) {
                           outputBuffer.append(dot);
                           record.add(dot);
                        }
                        //context.set(Context.CURRENT_X_POS, calculateNewX(context.getCurrentX(), context.getCurrentLineLength(), context.getCurrentDirection()));
                        //context.set(Context.CURRENT_Y_POS, calculateNewY(context.getCurrentLineLength(), context.getCurrentDirection()));
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
                        System.out.println("turn left by " + context.getAngleStep() + " deg...new direction is " + calculateLeftTurnAngle(context));
                        break;
                    case RIGHT_TURN:
                        System.out.println("turn right by " + context.getAngleStep() + " deg...new direction is " + calculateRightTurnAngle(context));
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
    private double calculateNewX(double currentX, double currentLineLength, int currentDirection) {
        return (currentX + (currentLineLength * Math.cos(toRadians(currentDirection))));

    }

    private double calculateNewY(double currentY, double currentLineLength, int currentDirection) {
        return (currentY + (currentLineLength * Math.sin(toRadians(currentDirection))));

    }

    private Coordinates getNewCoordinates(Context context) {
        Coordinates coordinates = new Coordinates();
        double currentLineLength = context.getCurrentLineLength();
        context.set(Context.LINE_LENGTH, currentLineLength); // setting this in case dot operator is using line length for default radius
        coordinates.setX(calculateNewX(context.getCurrentX(), currentLineLength, context.getCurrentDirection()));
        coordinates.setY(calculateNewY(context.getCurrentY(), currentLineLength, context.getCurrentDirection()));
        return coordinates;

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

    private class Coordinates {

        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
