package org.hccp.lang;

import org.hccp.elses.Expr;


public class AstPrinter implements Expr.Visitor<String> {



    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }



    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitLiteralListExpr(Expr.LiteralList expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(expr.head.value);
        if (expr.tail != null) {
            builder.append(" ");
            builder.append(expr.tail.accept(this));

        }
        return builder.toString();
    }


    private String parenthesize(String name, Expr ... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr: exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
