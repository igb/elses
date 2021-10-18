package org.hccp.elses;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

public class CompileTest {
    @Test
    public void testCompilation() throws Exception {
        Scanner s = new Scanner(ScannerTest.SOURCE_001);
        List<Token> tokens = s.scanTokens();
        List<Expr> exprs = Elses.parse(tokens);
        Program program = Elses.compile(exprs);
        List<Expr.Literal> axiom = program.getAxiom().axiom;
        assertEquals("Expected an axiom with one literal.", 1, axiom.size());
        assertEquals("Should be a forward literal.", TokenType.FORWARD, axiom.get(0).value);

        List<Rule> rules = program.getRules();
        assertEquals("Expected a rulset with one rule.", 1, rules.size());
        Rule rule  = rules.get(0);
        assertEquals("EXxpected antecedent to be a forward.", TokenType.FORWARD, rule.getInput().value);
        List<Expr.Literal> output = rule.getOutput();
        assertEquals("Expected a consequent with two literals.", 2, output.size());
        assertEquals("Expected a FORWARD literal.", TokenType.FORWARD, output.get(0).value);
        assertEquals("Expected a FORWARD literal.", TokenType.FORWARD, output.get(1).value);

    }
}
