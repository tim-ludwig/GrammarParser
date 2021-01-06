package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public final class Optional extends Expression {
	private final Expression expression;
	
	private Optional(final Expression expression) {
		if(expression instanceof Predicate) throw new IllegalArgumentException("No predicates!");
		
		this.expression = expression;
	}
	
	public static Optional of(final Expression expression) {
		return new Optional(expression);
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		final ParseTree parseTree = expression.parseTree(input, position);
		
		if(parseTree == null) return new ParseTree(this, position, "");
		
		return new ParseTree(this, position, parseTree.getMatchedText(), parseTree);
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.OPTIONAL;
	}
	
	@Override
	public String toString() {
		String s = expression.toString();
		
		if(expression instanceof Choice || expression instanceof Sequence) s = "(" + s + ")";
		
		return s + "?";
	}
}
