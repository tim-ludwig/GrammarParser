package me.tludwig.parsing.peg.expressions.primaries;

import java.lang.Character.UnicodeBlock;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.tludwig.parsing.UnicodeGeneralCategory;
import me.tludwig.parsing.peg.CharClassPredicate;
import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public final class LiteralCharClass extends Primary {
	private final CharClassPredicate predicate;
	
	private LiteralCharClass(final CharClassPredicate predicate) {
		this.predicate = predicate;
	}
	
	public LiteralCharClass invert() {
		return new LiteralCharClass(CharClassPredicate.negation(predicate));
	}
	
	public LiteralCharClass unite(final LiteralCharClass... classes) {
		return new LiteralCharClass(CharClassPredicate.union(CharClassPredicate.convert(this, classes)));
	}
	
	public LiteralCharClass intersect(final LiteralCharClass... classes) {
		return new LiteralCharClass(CharClassPredicate.intersection(CharClassPredicate.convert(this, classes)));
	}
	
	public static LiteralCharClass of(final CharClassPredicate predicate) {
		return new LiteralCharClass(predicate);
	}
	
	public static LiteralCharClass of(final char... chars) {
		return new LiteralCharClass(CharClassPredicate.list(chars));
	}
	
	public static LiteralCharClass of(final int... chars) {
		return new LiteralCharClass(CharClassPredicate.list(chars));
	}
	
	public static LiteralCharClass of(final char c) {
		return of((int) c);
	}
	
	public static LiteralCharClass of(final int c) {
		return new LiteralCharClass(CharClassPredicate.constant(c));
	}
	
	public static LiteralCharClass of(final String def) {
		if(!def.matches("(.-.|.)+")) return null;
		
		final Matcher m = Pattern.compile("(.-.|.)").matcher(def);
		final LinkedList<LiteralCharClass> classes = new LinkedList<>();
		
		String group;
		while(m.find()) {
			group = m.group();
			
			if(group.length() == 1) classes.add(LiteralCharClass.of(group.codePointAt(0)));
			else classes.add(LiteralCharClass.range(group.codePointAt(0), group.codePointAt(2)));
		}
		
		return LiteralCharClass.union(classes.toArray(new LiteralCharClass[classes.size()]));
	}
	
	public static LiteralCharClass of(final UnicodeGeneralCategory unicodeCategory) {
		return new LiteralCharClass(CharClassPredicate.category(unicodeCategory));
	}
	
	public static LiteralCharClass of(final UnicodeGeneralCategory... unicodeCategories) {
		return new LiteralCharClass(CharClassPredicate.categories(unicodeCategories));
	}
	
	public static LiteralCharClass of(final UnicodeBlock unicodeBlock) {
		return new LiteralCharClass(CharClassPredicate.block(unicodeBlock));
	}
	
	public CharClassPredicate getPredicate() {
		return predicate;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(position >= input.length()) return null;
		
		final int c = input.codePointAt(position);
		
		if(predicate.test(c)) return new ParseTree(this, position, String.valueOf(Character.toChars(c)));
		
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
		return new LiteralCharClass(CharClassPredicate.range(from, to));
	}
	
	public static LiteralCharClass union(final LiteralCharClass... classes) {
		return new LiteralCharClass(CharClassPredicate.union(CharClassPredicate.convert(classes)));
	}
	
	public static LiteralCharClass intersection(final LiteralCharClass... classes) {
		return new LiteralCharClass(CharClassPredicate.intersection(CharClassPredicate.convert(classes)));
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
