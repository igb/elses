package org.hccp.elses;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScannerTest {

    public static final String SOURCE_001 = "AXIOM: F\nF -> FF\n";

    @Test
    public void testScanTokens() {

        List<Token> testTokens = new LinkedList<>();
        TokenType tokenTypes [] = {
                TokenType.AXIOM,
                TokenType.COLON,
                TokenType.FORWARD,
                TokenType.NEWLINE,
                TokenType.FORWARD,
                TokenType.THEN,
                TokenType.FORWARD,
                TokenType.FORWARD,
                TokenType.NEWLINE,
                TokenType.EOF
        };



        for (int i = 0; i < tokenTypes.length; i++) {
            TokenType tokenType = tokenTypes[i];
            testTokens.add(new Token(tokenType,"", null, -1));
        }


        Scanner s = new Scanner(SOURCE_001);
        List<Token> tokens = s.scanTokens();
        assertEquals("Expected same number of tokens.", testTokens.size(), tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            assertEquals(token.type, testTokens.get(i).type);
        }


    }
}
