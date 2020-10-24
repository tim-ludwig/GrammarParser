package me.tludwig.parsing.peg.expressions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.Match;

public final class Choice extends Expression {
	private final List<Expression> subExpressions;
	
	private Choice(final List<Expression> subExpressions) {
		this.subExpressions = subExpressions;
	}
	
	public static Choice of(final Expression... subExpressions) {
		return new Choice(Arrays.asList(subExpressions));
	}
	
	public static Choice of(final List<Expression> subExpressions) {
		return new Choice(subExpressions);
	}
	
	public List<Expression> getSubExpressions() {
		return Collections.unmodifiableList(subExpressions);
	}
	
	@Override
	public Match match(final String input, final int position) {
		Match match;
		
		for(final Expression sExp : subExpressions) {
			match = sExp.match(input, position);
			
			if(sExp instanceof Predicate) {
				if(!((Predicate) sExp).success(match)) {
					continue;
				}
				
				return new Match(this, position, "");
			}
			
			if(match != null) return match;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return subExpressions.stream().map(expression -> {
			String s = expression.toString();
			
			if(expression instanceof Choice || expression instanceof Sequence) {
				s = "(" + s + ")";
			}
			
			return s;
		}).collect(Collectors.joining(" / "));
	}
}
