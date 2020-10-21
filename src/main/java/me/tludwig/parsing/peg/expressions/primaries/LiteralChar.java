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
	
	@Override
	public Match match(final String input, final int position) {
		if(position >= input.length()) return null;
		
		if(input.charAt(position) == c) return new Match(this, position, String.valueOf(c));
		
		return null;
	}
	
	@Override
	public String toString() {
		return "'" + c + "'";
	}
}
