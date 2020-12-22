package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.ParseTree;

public class NonTerminal extends Expression {
	private final String    name;
	private final PEGrammar grammar;
	
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
	public ParseTree parseTree(final String input, final int position) {
		final ParseTree parseTree = getDefinition().parseTree(input, position);
		
		if(parseTree == null) return null;
		
		return new ParseTree(this, parseTree.getPos(), parseTree.getMatchedText(), parseTree);
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.NON_TERMINAL;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
