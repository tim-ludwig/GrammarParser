package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.Match;
import me.tludwig.parsing.peg.expressions.Expression;

public class LiteralChar extends Expression {
	private final char c;
	
	private LiteralChar(final char c) {
		this.c = c;
	}
	
	public static LiteralChar of(final char c) {
		return new LiteralChar(c);
	}
	
	public static LiteralChar of(final int c) {
		return new LiteralChar((char) c);
	}
	
	public char getChar() {
		return c;
	}
	
	@Override
	public Match match(final String input, final int position) {
		if(position >= input.length()) return null;
		
		if(input.charAt(position) == c) return new Match(this, position, String.valueOf(c));
		
		return null;
	}
	
	private String escape() {
		switch(c) {
			case '\\':
				return "\\\\";
			case '\t':
				return "\\t";
			case '\n':
				return "\\n";
			case '\r':
				return "\\r";
			case '\f':
				return "\\f";
			case '\'':
				return "\\'";
			default:
		}
		
		return "" + c;
	}
	
	@Override
	public String toString() {
		return "'" + escape() + "'";
	}
}
