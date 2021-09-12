package org.hccp.elses;

import java.util.List;

abstract class Expr{
	interface Visitor<R> {
		R visitBinaryExpr(Binary expr);
		R visitLiteralExpr(Literal expr);
	}
    static class Binary extends Expr {
        Binary(Expr left,Token operator,Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

            final Expr left;
            final Token operator;
            final Expr right;
    }
    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

            final Object value;
    }

 abstract <R> R accept(Visitor<R> visitor);
}
