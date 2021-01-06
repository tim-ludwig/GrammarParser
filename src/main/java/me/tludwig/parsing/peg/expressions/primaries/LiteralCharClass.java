package me.tludwig.parsing.peg.expressions.primaries;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public final class LiteralCharClass extends Primary {
	private static final IntPredicate UNION_IDENTITY = c -> false, INTERSECTION_IDENTITY = c -> true;
	
	private final IntPredicate predicate;
	
	private LiteralCharClass(final IntPredicate predicate) {
		this.predicate = predicate;
	}
	
	public LiteralCharClass invert() {
		return new LiteralCharClass(predicate.negate());
	}
	
	public static LiteralCharClass of(final IntPredicate charPredicate) {
		return new LiteralCharClass(charPredicate);
	}
	
	public static LiteralCharClass of(final char... chars) {
		Arrays.sort(chars);
		
		return new LiteralCharClass(toTest -> Arrays.binarySearch(chars, (char) toTest) >= 0);
	}
	
	public static LiteralCharClass of(final char c) {
		return new LiteralCharClass(toTest -> toTest == c);
	}
	
	public static LiteralCharClass of(final int c) {
		return of((char) c);
	}
	
	public static LiteralCharClass of(final String def) {
		if(!def.matches("(.-.|.)+")) return null;
		
		final Matcher m = Pattern.compile("(.-.|.)").matcher(def);
		final LinkedList<LiteralCharClass> classes = new LinkedList<>();
		
		String group;
		while(m.find()) {
			group = m.group();
			
			if(group.length() == 1) classes.add(LiteralCharClass.of(group.charAt(0)));
			else classes.add(LiteralCharClass.range(group.charAt(0), group.charAt(2)));
		}
		
		return LiteralCharClass.union(classes.toArray(new LiteralCharClass[classes.size()]));
	}
	
	public IntPredicate getPredicate() {
		return predicate;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(position >= input.length()) return null;
		
		final char c = input.charAt(position);
		
		if(predicate.test(c)) return new ParseTree(this, position, String.copyValueOf(Character.toChars(c)));
		
		return null;
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.CHAR_CLASS;
	}
	
	public static LiteralCharClass range(final char from, final char to) {
		return range((int) from, (int) to);
	}
	
	public static LiteralCharClass range(final int from, final int to) {
		return new LiteralCharClass(c -> from <= c && c <= to);
	}
	
	public static LiteralCharClass union(final LiteralCharClass... classes) {
		return new LiteralCharClass(
				Arrays.stream(classes).map(clazz -> clazz.predicate).reduce(UNION_IDENTITY, IntPredicate::or));
	}
	
	public static LiteralCharClass intersection(final LiteralCharClass... classes) {
		return new LiteralCharClass(
				Arrays.stream(classes).map(clazz -> clazz.predicate).reduce(INTERSECTION_IDENTITY, IntPredicate::and));
	}
	
	public static LiteralCharClass digits() {
		return range('0', '9');
	}
	
	public static LiteralCharClass hexDigits() {
		return union(digits(), range('a', 'f'), range('A', 'F'));
	}
	
	public static LiteralCharClass octalDigits() {
		return range('0', '7');
	}
	
	public static LiteralCharClass lower() {
		return range('a', 'z');
	}
	
	public static LiteralCharClass upper() {
		return range('A', 'Z');
	}
	
	public static LiteralCharClass letters() {
		return union(lower(), upper());
	}
	
	public static LiteralCharClass alnum() {
		return union(lower(), upper(), digits());
	}
	
	public static LiteralCharClass ascii() {
		return range(0, 0x7F);
	}
	
	public static LiteralCharClass blank() {
		return of(' ', '\t');
	}
	
	public static LiteralCharClass control() {
		return union(range(0, 0x1F), of(0x7F));
	}
	
	public static LiteralCharClass whitespace() {
		return of(' ', '\n', '\t', '\r', '\f');
	}
	
	public static LiteralCharClass wordCharacters() {
		return union(lower(), upper(), digits(), of('_'));
	}
	
	public static LiteralCharClass punctuation() {
		return union(range('!', '/'), range(':', '@'), range('[', '`'), range('{', '~'));
	}
	
	public static LiteralCharClass graphical() {
		return range(0x21, 0x7E);
	}
	
	public static LiteralCharClass printable() {
		return union(graphical(), of(' '));
	}
	
	@Override
	public String toString() {
		return "[...]";
	}
}
