package org.hccp.elses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private static final Map<String, TokenType> keywords;

    private int line=1;
    private int start=0;
    private int current=0;

    static {
        keywords = new HashMap<String, TokenType>();
        keywords.put("AXIOM", TokenType.AXIOM);
        keywords.put("ANGLE", TokenType.ANGLE);

    }

    public Scanner(String source) {

        this.source = source;
    }

    public List<Token> scanTokens() {
        while(!isAtEnd()) {
            start=current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));



        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '-':addToken(match('>') ? TokenType.THEN : TokenType.RIGHT_TURN); break;
            case ':': addToken(TokenType.COLON); break;
            case '+': addToken(TokenType.LEFT_TURN); break;
            case '[': addToken(TokenType.PUSH); break;
            case ']': addToken(TokenType.POP); break;
            case 'F': addToken(TokenType.FORWARD); break;
            case 'f': addToken(TokenType.PEN_UP_FORWARD); break;
            case '@': addToken(TokenType.DOT); break;
            case '=': addToken(TokenType.EQUALS); break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                addToken(TokenType.NEWLINE);
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Elses.error(line, "Unexpected character.");
                }

                break;
        }

    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        // because we use f & F as instructions we need to
        // disallow them as alpha tokens
        if (c == 'f' || c == 'F') {
            return false;
        }

        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type, text);
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }


    private void number() {
        while (isDigit(peek())) advance();

        // look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            //consume the '.'
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));

    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private void addToken(TokenType tokenType) {
       addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        tokens.add(new Token(tokenType, source.substring(start, current), literal, line));
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return (current >= source.length());
    }
}
