package org.hccp.elses;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ElsesTest {

    public static final String RULE_STRING_001 = "1->11";
    public static final String RULE_STRING_002 = "0->1[0]0";

    @Test
    public void testCompileRule() {

        Scanner s = new Scanner(RULE_STRING_001);
        List<Token> tokens = s.scanTokens();
        Parser p = new Parser(tokens);
        Expr expr = p.parse();

        Rule rule001  = Elses.compileRule(p.parse());
        assertEquals("Incorrect parsing of rule.", "1", rule001.getInput());
        assertEquals("Incorrect parsing of rule.", "11", rule001.getOutput());

        Rule rule002  = Elses.parseRule(RULE_STRING_002);
        assertEquals("Incorrect parsing of rule.", "0", rule002.getInput());
        assertEquals("Incorrect parsing of rule.", "1[0]0", rule002.getOutput());

    }





    }