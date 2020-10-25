package me.tludwig.parsing.peg.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.ParseTree;

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
	public ParseTree parseTree(final String input, final int position) {
		final List<ParseTree> subMatches = new ArrayList<>(subExpressions.length);
		
		int cPos = position;
		ParseTree parseTree = null;
		for(final Expression exp : subExpressions) {
			parseTree = exp.parseTree(input, cPos);
			
			if(exp instanceof Predicate) {
				if(!((Predicate) exp).success(parseTree)) return null;
				
				continue;
			}
			
			if(parseTree == null) return null; // fail state
			
			subMatches.add(parseTree);
			
			cPos = parseTree.getEnd();
		}
		
		return new ParseTree(this, position, input.substring(position, cPos), subMatches);
	}
	
	@Override
	public String toString() {
		return Arrays.stream(subExpressions).map(expression -> {
			String s = expression.toString();
			
			if(expression instanceof Choice || expression instanceof Sequence) {
				s = "(" + s + ")";
			}
			
			return s;
		}).collect(Collectors.joining(" "));
	}
}
