package org.hccp.elses;

import org.junit.Test;


import java.util.List;

import static org.junit.Assert.*;

public class ProgramTest {
    @Test
    public void testProgram() throws Exception {
        Scanner s = new Scanner(ScannerTest.SOURCE_001);
        List<Token> tokens = s.scanTokens();
        List<Expr> exprs = Elses.parse(tokens);
        Program program = Elses.compile(exprs);
        List<Expr.Literal> output1 = program.execute(1);
        List<Expr.Literal> output3 =program.execute(3);
        List<Expr.Literal> output5 =program.execute(5);

        assertEquals("Output size should be 2^1", 2, output1.size());
        assertEquals("Output size should be 2^3", 8, output3.size());
        assertEquals("Output size should be 2^5", 32, output5.size());

        for (int i = 0; i < output5.size(); i++) {
            Expr.Literal literal = output5.get(i);
            assertEquals("Expected a FORWARD literal.", TokenType.FORWARD, literal.value);
        }


    }
}


