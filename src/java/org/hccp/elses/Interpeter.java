package org.hccp.elses;

public class Interpeter {

    StringBuffer outputBuffer = new StringBuffer();


    public String getOutput() {
        return outputBuffer.toString();
    }

    public void intepret(String program, Context context) {
        outputBuffer.append("<svg xmlns=\"http://www.w3.org/2000/svg\">\n");
        while (program.length() > 0) {


            char instruction = program.toCharArray()[0];

            switch (instruction) {
                case Elses.FWD:
                    System.out.println("draw line from " + context.getCurrentX() + ", " + context.getCurrentX() + " to " + calculateNewX(context) + ", " + calculateNewY(context));
                    outputBuffer.append(String.format("<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" />\n", context.getCurrentX(), context.getCurrentY(), calculateNewX(context), calculateNewY(context)));
                    context.set(Context.CURRENT_X_POS, calculateNewX(context));
                    context.set(Context.CURRENT_Y_POS, calculateNewY(context));
                    break;
                case Elses.PUSH:
                    System.out.println("push");
                    context.push();
                    break;
                case Elses.POP:
                    System.out.println("pop");
                    context.pop();
                    break;
                case Elses.LT:
                    System.out.println("turn left by " + context.get(Context.ANGLE_STEP) + " deg...new direction is " + calculateLeftTurnAngle(context));
                    break;
                case Elses.RT:
                    System.out.println("turn right by " + context.get(Context.ANGLE_STEP) + " deg...new direction is " + calculateRightTurnAngle(context));
                    break;
                default:
                    System.out.println("unsupported instruction: " + instruction);


            }

            program = program.substring(1);
        }
        outputBuffer.append("</svg>\n");

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
