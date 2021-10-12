package org.hccp.elses;

import java.util.List;

import static org.hccp.elses.TokenType.*;

public class Parser {

    private static class ParserError extends RuntimeException {};

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    Expr parse() {
        try {
            return expression();
        } catch (ParserError error) {
            return null;
        }
    }


// control and postion methods....

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }


    // recursve descent parsing...


    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = primary();
        while (match(TokenType.THEN, COLON)) {
            Token operator = previous();
                Expr right = list();
                expr = new Expr.Binary(expr, operator, right);
                if (match(NEWLINE)) {
                    // do nothing...
                    System.out.println("wut");
                    //todo: this is a bug in our grammar or something
                }


        }
        return expr;
    }

    private Expr list() {
        return new Expr.LiteralList((Expr.Literal) primary(), match(NEWLINE,COLON, THEN) ? null : (Expr.LiteralList) list());
    }

    private Expr primary() {
        if (match(AXIOM)) return new Expr.Literal(AXIOM);
        if (match(ANGLE)) return new Expr.Literal(ANGLE);
        //commands
        if (match(FORWARD)) return new Expr.Literal(FORWARD);
        if (match(PEN_UP_FORWARD)) return new Expr.Literal(PEN_UP_FORWARD);
        if (match(INCREMENT_ANGLE)) return new Expr.Literal(INCREMENT_ANGLE);
        if (match(DECREMENT_ANGLE)) return new Expr.Literal(DECREMENT_ANGLE);
        if (match(DOT)) return new Expr.Literal(DOT);
        if (match(POP)) return new Expr.Literal(POP);
        if (match(PUSH)) return new Expr.Literal(PUSH);





        if (match(NUMBER, IDENTIFIER)) {
            return new Expr.Literal(previous().literal);
        }



        throw error(peek(), "Expect expression.");

    }

    private ParserError error(Token token, String errorMessage) {
        Elses.error(token, errorMessage);
        return new ParserError();
    }


}
