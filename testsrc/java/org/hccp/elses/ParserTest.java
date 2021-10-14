package org.hccp.elses;

import org.junit.Test;


import java.util.List;

import static org.junit.Assert.*;


public class ParserTest {

    @Test
    public void testParsing001() {

        Scanner s = new Scanner(ScannerTest.SOURCE_001);
        List<Token> tokens = s.scanTokens();
        List<Expr> exprs = Elses.parse(tokens);
        assertEquals("Expected 2 expressions.", 2, exprs.size());

        Expr expr1 = exprs.get(0);

        assertAxiom(expr1, TokenType.FORWARD);


        Expr expr2 = exprs.get(1);

        assertTrue("Expected binary expression.", expr2 instanceof Expr.Binary);
        assertEquals("Should be a forward literal.", TokenType.FORWARD, ((Expr.Literal)((Expr.Binary) expr2).left).value);
        assertTrue("Should be a literal list.", ((Expr.Binary) expr2).right instanceof Expr.LiteralList);
        Expr.LiteralList literalList = (Expr.LiteralList)((Expr.Binary) expr2).right;
        List<Expr.Literal> consequent = Elses.literalListToList(literalList);

        assertEquals("Expected 2 literals.", 2, consequent.size());
        assertEquals("Should be a forward literal.", TokenType.FORWARD, consequent.get(0).value);
        assertEquals("Should be a forward literal.", TokenType.FORWARD, consequent.get(1).value);


    }

    private void assertAxiom(Expr expectedAxiom, Object axiomValue) {
        assertTrue("Expected binary expression.", expectedAxiom instanceof Expr.Binary);

        Expr.Binary binaryExpr1 = (Expr.Binary) expectedAxiom;
        assertEquals("Left-side should be of type axiom.", ((Expr.Literal)((Expr.Binary) expectedAxiom).left).value, TokenType.AXIOM);
        Expr.LiteralList right1 = (Expr.LiteralList) ((Expr.Binary) expectedAxiom).right;
        assertTrue("Right-side should be of type literal list.", right1 instanceof Expr.LiteralList);

        List<Expr.Literal> list1 = Elses.literalListToList(right1);
        assertEquals("Lists should only containe one literal.", 1, list1.size());
        assertEquals("Should be a literal with value " + axiomValue + " literal.", axiomValue, list1.get(0).value);
    }


    @Test
    public void testParsing002() {

        Scanner s = new Scanner(ScannerTest.SOURCE_002);
        List<Token> tokens = s.scanTokens();
        List<Expr> exprs = Elses.parse(tokens);

        assertEquals("Expected 3 expressions.", 3, exprs.size());
        assertAxiom(exprs.get(0), "X");

    }
}
