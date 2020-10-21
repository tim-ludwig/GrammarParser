package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.Match;

public final class Optional extends Expression {
	private final Expression expression;
	
	private Optional(final Expression expression) {
		if(expression instanceof Predicate) throw new IllegalArgumentException("No predicates!");
		
		this.expression = expression;
	}
	
	public static Optional of(final Expression expression) {
		return new Optional(expression);
	}
	
	@Override
	public Match match(final String input, final int position) {
		final Match match = expression.match(input, position);
		
		if(match == null) return new Match(this, position, "");
		
		return match;
	}
	
	@Override
	public String toString() {
		return "(" + expression + ")?";
	}
}
