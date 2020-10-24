package me.tludwig.parsing.peg.expressions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.tludwig.parsing.peg.Match;

public final class Repetition extends Expression {
	private final int min, max;
	Expression        expression;
	
	private Repetition(final Expression expression, final int min, final int max) {
		if(expression instanceof Predicate) throw new IllegalArgumentException("No predicates!");
		
		this.expression = expression;
		this.min = min;
		this.max = max;
	}
	
	public static Repetition times(final Expression expression, final int n) {
		return new Repetition(expression, n, n);
	}
	
	public static Repetition between(final Expression expression, final int min, final int max) {
		return new Repetition(expression, min, max);
	}
	
	public static Repetition atLeast(final Expression expression, final int n) {
		return new Repetition(expression, n, -1);
	}
	
	public static Repetition atMost(final Expression expression, final int n) {
		return new Repetition(expression, 0, n);
	}
	
	public static Repetition zeroOrMore(final Expression expression) {
		return atLeast(expression, 0);
	}
	
	public static Repetition oneOrMore(final Expression expression) {
		return atLeast(expression, 1);
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public Match match(final String input, final int position) {
		final List<Match> subMatches = max > 0 ? new ArrayList<>(max) : new LinkedList<>();
		
		Match match;
		int cPos = position;
		
		int i = 0;
		for(; i < max || max < 0; i++) {
			match = expression.match(input, cPos);
			
			if(match == null) break;
			
			subMatches.add(match);
			
			cPos = match.getEnd();
		}
		
		if(i < min) return null;
		
		return new Match(this, position, input.substring(position, cPos), subMatches);
	}
	
	@Override
	public String toString() {
		String s = expression.toString();
		
		if(expression instanceof Choice || expression instanceof Sequence) s = "(" + s + ")";
		
		if(max == -1) {
			if(min == 0) return s + '*';
			if(min == 1) return s + '+';
			
			return s + "{" + min + ",}";
		}
		
		if(min == 0) return s + "{, " + max + "}";
		
		return s + "{" + min + ", " + max + "}";
	}
	
}
