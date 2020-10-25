package me.tludwig.parsing.peg.expressions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.ParseTree;

public final class Choice extends Expression {
	private final Expression[] subExpressions;
	
	private Choice(final Expression[] subExpressions) {
		this.subExpressions = subExpressions;
	}
	
	public static Choice of(final Expression... subExpressions) {
		return new Choice(subExpressions);
	}
	
	public static Choice of(final List<Expression> subExpressions) {
		return new Choice(subExpressions.toArray(new Expression[subExpressions.size()]));
	}
	
	public Expression[] getSubExpressions() {
		return subExpressions;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		ParseTree parseTree;
		
		for(final Expression sExp : subExpressions) {
			parseTree = sExp.parseTree(input, position);
			
			if(sExp instanceof Predicate) {
				if(!((Predicate) sExp).success(parseTree)) {
					continue;
				}
				
				return new ParseTree(this, position, "");
			}
			
			if(parseTree != null) return new ParseTree(this, position, parseTree.getMatchedText(), parseTree);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return Arrays.stream(subExpressions).map(expression -> {
			String s = expression.toString();
			
			if(expression instanceof Choice || expression instanceof Sequence) {
				s = "(" + s + ")";
			}
			
			return s;
		}).collect(Collectors.joining(" / "));
	}
}
