package me.tludwig.parsing.peg.expressions;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.Match;

public final class Sequence extends Expression {
	private final List<Expression> subExpressions;
	
	private Sequence(final List<Expression> subExpressions) {
		this.subExpressions = subExpressions;
	}
	
	public static Sequence of(final Expression... subExpressions) {
		return new Sequence(Arrays.asList(subExpressions));
	}
	
	public static Sequence of(final List<Expression> subExpressions) {
		return new Sequence(subExpressions);
	}
	
	public List<Expression> getSubExpressions() {
		return Collections.unmodifiableList(subExpressions);
	}
	
	@Override
	public Match match(final String input, final int position) {
		final LinkedList<Match> subMatches = new LinkedList<>();
		Match match;
		int cPos = position;
		
		for(final Expression sExp : subExpressions) {
			match = sExp.match(input, cPos);
			
			if(sExp instanceof Predicate) {
				if(!((Predicate) sExp).success(match)) return null;
				
				continue;
			}
			
			if(match == null) return null; // fail state
			
			subMatches.add(match);
			
			cPos = match.getEnd();
		}
		
		return new Match(this, position, input.substring(position, cPos), subMatches);
	}
	
	@Override
	public String toString() {
		return subExpressions.stream().map(expression -> {
			String s = expression.toString();
			
			if(expression instanceof Choice || expression instanceof Sequence) {
				s = "(" + s + ")";
			}
			
			return s;
		}).collect(Collectors.joining(" "));
	}
}
