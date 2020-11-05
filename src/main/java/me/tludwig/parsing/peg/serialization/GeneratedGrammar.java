package me.tludwig.parsing.peg.serialization;

import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.NonTerminal;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Predicate.PredicateType;
import me.tludwig.parsing.peg.expressions.Repetition;

public final class GeneratedGrammar extends PEGrammar {
	
	public GeneratedGrammar(final String startSymbol) {
		super(startSymbol);
	}
	
	@Override
	protected void init() {}
	
	NonTerminal definition(final String name, final Expression expression) {
		return def(name, expression);
	}
	
	NonTerminal definition(final String name) {
		return def(name);
	}
	
	Predicate predicate(final PredicateType type, final Expression expression) {
		return Predicate.of(expression, type);
	}
	
	Repetition repetition(final int min, final int max, final Expression expression) {
		return Repetition.between(expression, min, max);
	}
}
