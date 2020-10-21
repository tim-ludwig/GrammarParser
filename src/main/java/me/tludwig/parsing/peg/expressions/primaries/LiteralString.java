package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.Match;
import me.tludwig.parsing.peg.expressions.Expression;

public class LiteralString extends Expression {
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
	public Match match(final String input, final int position) {
		if(input.regionMatches(position, s, 0, s.length())) return new Match(this, position, s);
		
		return null;
	}
	
	@Override
	public String toString() {
		return '"' + s + '"';
	}
}
