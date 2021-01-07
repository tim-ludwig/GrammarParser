package me.tludwig.parsing.peg;

import java.lang.Character.UnicodeBlock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import me.tludwig.parsing.UnicodeGeneralCategory;
import me.tludwig.parsing.peg.ast.ASTRule;
import me.tludwig.parsing.peg.ast.AbstractSyntaxTree;
import me.tludwig.parsing.peg.expressions.Choice;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.NonTerminal;
import me.tludwig.parsing.peg.expressions.Optional;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Repetition;
import me.tludwig.parsing.peg.expressions.Sequence;
import me.tludwig.parsing.peg.expressions.primaries.EndOfFile;
import me.tludwig.parsing.peg.expressions.primaries.LiteralAnyChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;
import me.tludwig.parsing.peg.expressions.primaries.LiteralString;

public abstract class PEGrammar {
	private final HashMap<String, Expression>  definitions        = new HashMap<>();
	private final HashMap<String, NonTerminal> nonTerminals       = new HashMap<>();
	private final HashMap<String, ASTRule>     astConversionRules = new HashMap<>();
	private final NonTerminal                  startSymbol;
	
	public PEGrammar(final String startSymbol) {
		this.startSymbol = def(startSymbol);
		
		init();
	}
	
	protected void init() {}
	
	public final AbstractSyntaxTree abstractSyntaxTree(final String input) {
		return AbstractSyntaxTree.createAST(parseTree(input), astConversionRules);
	}
	
	public final ParseTree parseTree(final String input) {
		return startSymbol.parseTree(input, 0);
	}
	
	protected final void def(final String name, final ASTRule rule, final Expression expression) {
		astConversionRules.put(name, rule);
		
		def(name, expression);
	}
	
	protected final void def(final String name, final Expression expression) {
		definitions.put(name, expression == null ? NullExpression.INSTANCE : expression);
	}
	
	protected final NonTerminal def(final String name) {
		return nonTerminals.computeIfAbsent(name, __ -> NonTerminal.of(this, name));
	}
	
	protected final Sequence seq(final Expression... expressions) {
		return Sequence.of(expressions);
	}
	
	protected final Choice choice(final Expression... expressions) {
		return Choice.of(expressions);
	}
	
	protected final Repetition times(final Expression expression, final int n) {
		return Repetition.times(expression, n);
	}
	
	protected final Repetition between(final Expression expression, final int min, final int max) {
		return Repetition.between(expression, min, max);
	}
	
	protected final Repetition atLeast(final Expression expression, final int min) {
		return Repetition.atLeast(expression, min);
	}
	
	protected final Repetition atMost(final Expression expression, final int max) {
		return Repetition.atMost(expression, max);
	}
	
	protected final Repetition zeroOrMore(final Expression expression) {
		return Repetition.zeroOrMore(expression);
	}
	
	protected final Repetition zeroOrMore(final Expression... expressions) {
		return zeroOrMore(seq(expressions));
	}
	
	protected final Repetition oneOrMore(final Expression expression) {
		return Repetition.oneOrMore(expression);
	}
	
	protected final Repetition oneOrMore(final Expression... expressions) {
		return oneOrMore(seq(expressions));
	}
	
	protected final Optional opt(final Expression expression) {
		return Optional.of(expression);
	}
	
	protected final Optional opt(final Expression... expressions) {
		return opt(seq(expressions));
	}
	
	protected final Predicate and(final Expression expression) {
		return Predicate.and(expression);
	}
	
	protected final Predicate and(final Expression... expressions) {
		return and(seq(expressions));
	}
	
	protected final Predicate not(final Expression expression) {
		return Predicate.not(expression);
	}
	
	protected final Predicate not(final Expression... expressions) {
		return not(seq(expressions));
	}
	
	protected final LiteralChar character(final char c) {
		return LiteralChar.of(c);
	}
	
	protected final LiteralChar character(final int c) {
		return LiteralChar.of(c);
	}
	
	protected final LiteralString string(final String s) {
		return LiteralString.of(s);
	}
	
	protected final LiteralString CRLF() {
		return string("\r\n");
	}
	
	protected final LiteralCharClass list(final char... chars) {
		return LiteralCharClass.of(chars);
	}
	
	protected final LiteralCharClass list(final int... chars) {
		return LiteralCharClass.of(chars);
	}
	
	protected final LiteralCharClass list(final String def) {
		return LiteralCharClass.of(def);
	}
	
	protected final LiteralCharClass list(final UnicodeGeneralCategory unicodeCategory) {
		return LiteralCharClass.of(unicodeCategory);
	}
	
	protected final LiteralCharClass list(final UnicodeGeneralCategory... unicodeCategories) {
		return LiteralCharClass.of(unicodeCategories);
	}
	
	protected final LiteralCharClass list(final UnicodeBlock unicodeBlock) {
		return LiteralCharClass.of(unicodeBlock);
	}
	
	protected final LiteralCharClass range(final char from, final char to) {
		return LiteralCharClass.range(from, to);
	}
	
	protected final LiteralCharClass range(final int from, final int to) {
		return LiteralCharClass.range(from, to);
	}
	
	/**
	 * [0-9]
	 */
	protected final LiteralCharClass digits() {
		return LiteralCharClass.digits();
	}
	
	/**
	 * [0-9a-fA-F]
	 */
	protected final LiteralCharClass hexdigits() {
		return LiteralCharClass.hexDigits();
	}
	
	/**
	 * [0-7]
	 */
	protected final LiteralCharClass octaldigits() {
		return LiteralCharClass.octalDigits();
	}
	
	/**
	 * [a-z]
	 */
	protected final LiteralCharClass lower() {
		return LiteralCharClass.lower();
	}
	
	/**
	 * [A-Z]
	 */
	protected final LiteralCharClass upper() {
		return LiteralCharClass.upper();
	}
	
	/**
	 * [a-zA-Z]
	 */
	protected final LiteralCharClass letters() {
		return LiteralCharClass.letters();
	}
	
	/**
	 * [a-zA-Z0-9]
	 */
	protected final LiteralCharClass alnum() {
		return LiteralCharClass.alnum();
	}
	
	/**
	 * [\x00-\x7F]
	 */
	protected final LiteralCharClass ascii() {
		return LiteralCharClass.ascii();
	}
	
	protected final LiteralCharClass blank() {
		return LiteralCharClass.blank();
	}
	
	/**
	 * [\x00-\x1F\x7F]
	 */
	protected final LiteralCharClass control() {
		return LiteralCharClass.control();
	}
	
	protected final LiteralCharClass whitespace() {
		return LiteralCharClass.whitespace();
	}
	
	/**
	 * [a-zA-Z0-9_]
	 */
	protected final LiteralCharClass wchars() {
		return LiteralCharClass.wordCharacters();
	}
	
	/**
	 * [!-/:-@\[-`{-~]
	 */
	protected final LiteralCharClass punct() {
		return LiteralCharClass.punctuation();
	}
	
	/**
	 * [\x21-\x7E]
	 */
	protected final LiteralCharClass graphical() {
		return LiteralCharClass.graphical();
	}
	
	/**
	 * [\x21-\x7E]
	 */
	protected final LiteralCharClass visible() {
		return LiteralCharClass.graphical();
	}
	
	/**
	 * [\x20-\x7E]
	 */
	protected final LiteralCharClass printable() {
		return LiteralCharClass.printable();
	}
	
	protected final LiteralAnyChar any() {
		return new LiteralAnyChar();
	}
	
	protected final EndOfFile EOF() {
		return new EndOfFile();
	}
	
	public final Map<String, Expression> getDefinitions() {
		return Collections.unmodifiableMap(definitions);
	}
	
	public final Map<String, NonTerminal> getNonTerminals() {
		return Collections.unmodifiableMap(nonTerminals);
	}
	
	public final Map<String, ASTRule> getASTConversionRules() {
		return Collections.unmodifiableMap(astConversionRules);
	}
	
	public final NonTerminal getStartSymbol() {
		return startSymbol;
	}
	
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(buildRule(startSymbol));
		builder.append("\n");
		builder.append(nonTerminals.values().stream().filter(nonTerminal -> !nonTerminal.equals(startSymbol)).map(this::buildRule).collect(Collectors.joining("\n")));
		
		return builder.toString();
	}
	
	private String buildRule(final NonTerminal nonTerminal) {
		return nonTerminal + " <- " + nonTerminal.getDefinition();
	}
	
	private static class NullExpression extends Expression {
		private static final NullExpression INSTANCE = new NullExpression();
		
		@Override
		public ParseTree parseTree(final String input, final int position) {
			return null;
		}
		
		@Override
		public ExpressionType type() {
			return null;
		}
	}
}
