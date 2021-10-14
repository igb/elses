package org.hccp.elses;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScannerTest {

    public static final String SOURCE_001 = "AXIOM: F\nF -> FF\n";
    public static final String SOURCE_002 = "AXIOM: X\n" +
            "F->FF\n" +
            "X->F-[[X]+X]+F[+FX]-X\n";

    @Test
    public void testScanTokens001() {

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



        List<Token> testTokens =  createTokens(tokenTypes);



        Scanner s = new Scanner(SOURCE_001);
        List<Token> tokens = s.scanTokens();
        testScannedTokens(testTokens, tokens);


    }

    private void testScannedTokens(List<Token> testTokens, List<Token> tokens) {
        assertEquals("Expected same number of tokens.", testTokens.size(), tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            assertEquals(token.type, testTokens.get(i).type);
        }
    }

    @Test
    public void testScanTokens002() {
        TokenType tokenTypes [] = {
                TokenType.AXIOM,
                TokenType.COLON,
                TokenType.IDENTIFIER,
                TokenType.NEWLINE,
                TokenType.FORWARD,
                TokenType.THEN,
                TokenType.FORWARD,
                TokenType.FORWARD,
                TokenType.NEWLINE,
                TokenType.IDENTIFIER,
                TokenType.THEN,
                TokenType.FORWARD,
                TokenType.RIGHT_TURN,
                TokenType.PUSH,
                TokenType.PUSH,
                TokenType.IDENTIFIER,
                TokenType.POP,
                TokenType.LEFT_TURN,
                TokenType.IDENTIFIER,
                TokenType.POP,
                TokenType.LEFT_TURN,
                TokenType.FORWARD,
                TokenType.PUSH,
                TokenType.LEFT_TURN,
                TokenType.FORWARD,
                TokenType.IDENTIFIER,
                TokenType.POP,
                TokenType.RIGHT_TURN,
                TokenType.IDENTIFIER,
                TokenType.NEWLINE,
                TokenType.EOF
        };

       List<Token> testTokens =  createTokens(tokenTypes);

        Scanner s = new Scanner(SOURCE_002);
        List<Token> tokens = s.scanTokens();
        testScannedTokens(testTokens, tokens);

    }

    private List<Token> createTokens(TokenType[] tokenTypes) {
        List<Token> testTokens = new LinkedList<>();

        for (int i = 0; i < tokenTypes.length; i++) {
            TokenType tokenType = tokenTypes[i];
            testTokens.add(new Token(tokenType,"", null, -1));
        }

        return testTokens;
    }
}
