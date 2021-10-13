package org.hccp.elses;

import org.junit.Test;


import java.util.List;

import static org.junit.Assert.*;


public class ParserTest {

    @Test
    public void testParsing() {

        Scanner s = new Scanner(ScannerTest.SOURCE_001);
        List<Token> tokens = s.scanTokens();
        List<Expr> exprs = Elses.parse(tokens);
        assertEquals("Expected 2 expressions.", 2, exprs.size());

        Expr expr1 = exprs.get(0);
        Elses.print(expr1);

        assertTrue("Expected binary expression.", expr1 instanceof Expr.Binary);

        Expr.Binary binaryExpr1 = (Expr.Binary) expr1;
        assertEquals("Left-side should be of type axiom.", ((Expr.Literal)((Expr.Binary) expr1).left).value, TokenType.AXIOM);
        Expr.LiteralList right1 = (Expr.LiteralList) ((Expr.Binary) expr1).right;
        assertTrue("Right-side should be of type literal list.", right1 instanceof Expr.LiteralList);

        List<Expr.Literal> list1 = Elses.literalListToList(right1);
        assertEquals("Lists should only containe one literal.", 1, list1.size());
        assertEquals("Should be a forward literal.", TokenType.FORWARD, list1.get(0).value);


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
}
