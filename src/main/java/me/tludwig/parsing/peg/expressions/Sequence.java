package me.tludwig.parsing.peg.expressions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.Match;

public final class Sequence extends Expression {
	private final Expression[] subExpressions;
	
	private Sequence(final Expression[] subExpressions) {
		this.subExpressions = subExpressions;
	}
	
	public static Sequence of(final Expression... subExpressions) {
		return new Sequence(subExpressions);
	}
	
	public static Sequence of(final List<Expression> subExpressions) {
		return new Sequence(subExpressions.toArray(new Expression[subExpressions.size()]));
	}
	
	public Expression[] getSubExpressions() {
		return subExpressions;
	}
	
	@Override
	public Match match(final String input, final int position) {
		final Match[] subMatches = new Match[subExpressions.length];
		
		int cPos = position;
		Match match;
		Expression exp;
		for(int i = 0; i < subExpressions.length; i++) {
			exp = subExpressions[i];
			match = exp.match(input, cPos);
			
			if(exp instanceof Predicate) {
				if(!((Predicate) exp).success(match)) return null;
				
				continue;
			}
			
			if(match == null) return null; // fail state
			
			subMatches[i] = match;
			
			cPos = match.getEnd();
		}
		
		return new Match(this, position, input.substring(position, cPos), subMatches);
	}
	
	@Override
	public String toString() {
		return Arrays.stream(subExpressions).map(expression -> {
			String s = expression.toString();
			
			if(expression instanceof Choice || expression instanceof Sequence) s = "(" + s + ")";
			
			return s;
		}).collect(Collectors.joining(" "));
	}
}
