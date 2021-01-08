package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public class LiteralString extends Primary {
	private final String s;
	
	private LiteralString(final String s) {
		this.s = s;
	}
	
	public static LiteralString of(final String s) {
		return new LiteralString(s);
	}
	
	public String getString() {
		return s;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(input.regionMatches(position, s, 0, s.length())) return new ParseTree(this, position, s);
		
		return null;
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.STRING;
	}
	
	@Override
	public String toString() {
		return '"' + escape() + '"';
	}
	
	private String escape() {
		return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f").replace("\"", "\\\"");
	}
}
