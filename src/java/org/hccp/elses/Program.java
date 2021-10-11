package org.hccp.elses;

import java.util.LinkedList;
import java.util.List;

public class Program {

    private Axiom axiom;
    private List<Rule> rules;

    public Axiom getAxiom() {
        return axiom;
    }

    public void setAxiom(Axiom axiom) {
        this.axiom = axiom;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public List<Expr.Literal> execute() {
        return execute(1);
    }

    public List<Expr.Literal> execute(int iterations) {
        List<Expr.Literal> output = new LinkedList<>();
        output.add(this.axiom.axiom);
        for (int i=0; i < iterations; i++ ) {
            LinkedList<Expr.Literal> newOutput = new LinkedList<>();
            for (int j = 0; j < output.size(); j++) {
                Expr.Literal expr = output.get(j);
                boolean ruleFired = false;
                for (int k = 0; k < rules.size(); k++) {
                    Rule rule = rules.get(k);
                    if (expr.value.equals(rule.getInput().value)){
                        newOutput.addAll(rule.getOutput());
                        ruleFired = true;
                        break;
                    }
                }
                if (!ruleFired) {
                        newOutput.add(expr);
                }
            }

            output = newOutput;
        }
        return output;
    }


}
