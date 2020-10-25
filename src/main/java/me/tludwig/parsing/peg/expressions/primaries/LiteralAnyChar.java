package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.ParseTree;

public class LiteralAnyChar extends Primary {
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(position >= input.length()) return null;
		
		return new ParseTree(this, position, input.charAt(position) + "");
	}
	
	@Override
	public String toString() {
		return ".";
	}
}
