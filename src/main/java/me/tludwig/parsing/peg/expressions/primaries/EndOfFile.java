package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public class EndOfFile extends Primary {
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(position < input.length()) return null;
		
		return new ParseTree(this, position, "");
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.EOF;
	}
	
	@Override
	public String toString() {
		return "EOF";
	}
}
