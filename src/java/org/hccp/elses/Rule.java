package org.hccp.elses;

import java.util.List;

public class Rule {
    private Expr.Literal input;
    private List<Expr.Literal> output;

    public Rule(Expr.Literal input, List<Expr.Literal> output) {
        this.input = input;
        this.output = output;
    }

    public Expr.Literal getInput() {
        return input;
    }

    public void setInput(Expr.Literal input) {
        this.input = input;
    }

    public List<Expr.Literal> getOutput() {
        return output;
    }

    public void setOutput(List<Expr.Literal> output) {
        this.output = output;
    }
}
