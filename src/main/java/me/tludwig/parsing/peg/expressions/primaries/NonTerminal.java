package me.tludwig.parsing.peg.expressions.primaries;

import me.tludwig.parsing.peg.Match;
import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.expressions.Expression;

public class NonTerminal extends Expression {
	private final String	name;
	private final PEGrammar	grammar;
	
	private NonTerminal(final PEGrammar grammar, final String name) {
		this.name = name;
		this.grammar = grammar;
	}
	
	public static NonTerminal of(final PEGrammar grammar, final String name) {
		return new NonTerminal(grammar, name);
	}
	
	public String getName() {
		return name;
	}
	
	public Expression getDefinition() {
		return grammar.getDefinitions().get(name);
	}
	
	@Override
	public Match match(final String input, final int position) {
		final Match match = getDefinition().match(input, position);
		
		if(match == null) return null;
		
		return new Match(this, match.getPos(), match.getMatchedText(), match.getSubMatches());
	}
	
	@Override
	public String toString() {
		return name;
	}
}
