package org.hccp.elses;

import java.util.*;

public class Context {
    //CONTEXT CONSTANTS
    public static final String ANGLE_STEP = "angle-step";
    public static final String CURRENT_DIRECTION = "current-angle";
    public static final String LINE_LENGTH = "line-length";
    public static final String CURRENT_X_POS = "current-x-position";
    public static final String CURRENT_Y_POS = "current-y-position";
    public static final String ITERATIONS = "iterations";
    public static final String DOT_RADIUS = "dot-radius";
    public static final String ARGUMENT_STRING = "argument-string";


    private LinkedList<Map> contexts = new LinkedList<Map>();




    public Context() {
        contexts.add(new HashMap());
    }


    public Object get(String key) {
        return contexts.getLast().get(key);
    }

    public void set(String key, Object value) {
         contexts.getLast().put(key, value);
    }

    public void push() {
        HashMap newContext = new HashMap();
        HashMap lastContext = (HashMap) contexts.getLast();

        Iterator<String> lastKeysItr =  lastContext.keySet().iterator();
        while (lastKeysItr.hasNext()) {
            String key = lastKeysItr.next();
            newContext.put(key, lastContext.get(key));
        }

        contexts.add(newContext);

    }

    public void pop() {
        if (contexts.size() == 1) {
            throw new IllegalStateException("Context stack size (1) is to small to allow a pop operation!");
        }
        contexts.removeLast();
    }

    public int getCurrentDirection() {
        return (int)this.get(CURRENT_DIRECTION);
    }

    public int getAngleStep() {
        return (int)this.get(ANGLE_STEP);
    }

    public double getCurrentLineLength() {
        return (double)this.get(LINE_LENGTH);
    }

    public double getCurrentX() {
        return (double) this.get(CURRENT_X_POS);
    }

    public double getCurrentY() {
        return (double) this.get(CURRENT_Y_POS);
    }

    public double getDotRadius() {
        return (double) this.get(DOT_RADIUS);
    }
}
