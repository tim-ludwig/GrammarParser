package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.ParseTree;

public final class Predicate extends Expression {
	private final Expression    expression;
	private final PredicateType type;
	
	private Predicate(final Expression expression, final PredicateType type) {
		if(expression instanceof Predicate) throw new IllegalArgumentException("No predicates!");
		
		this.expression = expression;
		this.type = type;
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
		AND,
		NOT;
		
		public boolean success(final ParseTree parseTree) {
			switch(this) {
				case AND:
					return parseTree != null;
				case NOT:
					return parseTree == null;
			}
			return false;
		}
	}
}
