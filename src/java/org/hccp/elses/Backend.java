package org.hccp.elses;

import java.util.List;

public abstract class Backend {

    public abstract void intepret(List<Expr.Literal> ilr, Context context);
    public abstract byte[] getOutput();



}
