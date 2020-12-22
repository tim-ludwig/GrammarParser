package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public final class Predicate extends Expression {
	private final Expression    expression;
	private final PredicateType type;
	
	private Predicate(final Expression expression, final PredicateType type) {
		if(expression instanceof Predicate) throw new IllegalArgumentException("No predicates!");
		
		this.expression = expression;
		this.type = type;
	}
	
	public static Predicate of(final Expression expression, final PredicateType type) {
		return new Predicate(expression, type);
	}
	
	public static Predicate and(final Expression expression) {
		return new Predicate(expression, PredicateType.AND);
	}
	
	public static Predicate not(final Expression expression) {
		return new Predicate(expression, PredicateType.NOT);
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public PredicateType getType() {
		return type;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		return expression.parseTree(input, position);
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.PREDICATE;
	}
	
	@Override
	public String toString() {
		String s = expression.toString();
		
		if(expression instanceof Choice || expression instanceof Sequence) {
			s = "(" + s + ")";
		}
		
		if(type == PredicateType.AND) return '&' + s;
		else if(type == PredicateType.NOT) return '!' + s;
		
		return s;
	}
	
	public boolean success(final ParseTree parseTree) {
		return type.success(parseTree);
	}
	
	public enum PredicateType {
		AND(0),
		NOT(1);
		
		private final byte id;
		
		private PredicateType(final int id) {
			this.id = (byte) id;
		}
		
		public boolean success(final ParseTree parseTree) {
			switch(this) {
				case AND:
					return parseTree != null;
				case NOT:
					return parseTree == null;
			}
			
			return false;
		}
		
		public byte getId() {
			return id;
		}
		
		public static PredicateType getById(final int id) {
			return id == 0 ? AND : id == 1 ? NOT : null;
		}
	}
}
