package me.tludwig.parsing.peg;

import java.util.HashMap;

import me.tludwig.parsing.peg.expressions.Choice;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.Optional;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Repetition;
import me.tludwig.parsing.peg.expressions.Sequence;
import me.tludwig.parsing.peg.expressions.primaries.EndOfFile;
import me.tludwig.parsing.peg.expressions.primaries.LiteralAnyChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;
import me.tludwig.parsing.peg.expressions.primaries.LiteralString;
import me.tludwig.parsing.peg.expressions.primaries.NonTerminal;

public abstract class PEGrammar {
	public final HashMap<String, Expression> definitions = new HashMap<>();
	private final NonTerminal                startSymbol;
	
	public PEGrammar(final String startSymbol) {
		this.startSymbol = def(startSymbol);
		
		init();
	}
	
	public abstract void init();
	
	public Match match(final String input) {
		return startSymbol.match(input, 0);
	}
	
	public final void def(final String name, final Expression expression) {
		definitions.put(name, expression);
	}
	
	public final NonTerminal def(final String name) {
		return NonTerminal.of(this, name);
	}
	
	public final Sequence seq(final Expression... expressions) {
		return Sequence.of(expressions);
	}
	
	public final Choice choice(final Expression... expressions) {
		return Choice.of(expressions);
	}
	
	public final Repetition times(final Expression expression, final int n) {
		return Repetition.times(expression, n);
	}
	
	public final Repetition between(final Expression expression, final int min, final int max) {
		return Repetition.between(expression, min, max);
	}
	
	public final Repetition atLeast(final Expression expression, final int min) {
		return Repetition.atLeast(expression, min);
	}
	
	public final Repetition atMost(final Expression expression, final int max) {
		return Repetition.atMost(expression, max);
	}
	
	public final Repetition zeroOrMore(final Expression expression) {
		return Repetition.zeroOrMore(expression);
	}
	
	public final Repetition oneOrMore(final Expression expression) {
		return Repetition.oneOrMore(expression);
	}
	
	public final Optional opt(final Expression expression) {
		return Optional.of(expression);
	}
	
	public final Predicate and(final Expression expression) {
		return Predicate.and(expression);
	}
	
	public final Predicate not(final Expression expression) {
		return Predicate.not(expression);
	}
	
	public final LiteralChar character(final char c) {
		return LiteralChar.of(c);
	}
	
	public final LiteralChar character(final int c) {
		return LiteralChar.of(c);
	}
	
	public final LiteralString string(final String s) {
		return LiteralString.of(s);
	}
	
	public final LiteralCharClass list(final String def) {
		return LiteralCharClass.of(def);
	}
	
	public final LiteralCharClass range(final char from, final char to) {
		return LiteralCharClass.range(from, to);
	}
	
	public final LiteralCharClass range(final int from, final int to) {
		return LiteralCharClass.range((char) from, (char) to);
	}
	
	public final LiteralAnyChar any() {
		return new LiteralAnyChar();
	}
	
	public final EndOfFile EOF() {
		return new EndOfFile();
	}
}
