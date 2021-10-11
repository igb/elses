package org.hccp.elses;

import java.util.List;

public abstract class Expr{
	public interface Visitor<R> {
		R visitBinaryExpr(Binary expr);
		R visitLiteralExpr(Literal expr);
		R visitLiteralListExpr(LiteralList expr);
	}
   public static class Binary extends Expr {
        Binary(Expr left,Token operator,Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

            public final Expr left;
            public final Token operator;
            public final Expr right;
    }
   public static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

            public final Object value;
    }
   public static class LiteralList extends Expr {
        LiteralList(Literal head,LiteralList tail) {
            this.head = head;
            this.tail = tail;
        }

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralListExpr(this);
		}

            public final Literal head;
            public final LiteralList tail;
    }

 public abstract <R> R accept(Visitor<R> visitor);
}
