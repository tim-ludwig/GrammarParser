package me.tludwig.parsing.peg.expressions.primaries;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public final class LiteralCharClass extends Primary {
	private final char[] chars;
	
	private LiteralCharClass(final char... chars) {
		this.chars = chars;
	}
	
	public static LiteralCharClass of(final char... chars) {
		return new LiteralCharClass(chars);
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
	
	public char[] getChars() {
		return chars;
	}
	
	@Override
	public ParseTree parseTree(final String input, final int position) {
		if(position >= input.length()) return null;
		
		for(final char c : chars)
			if(input.charAt(position) == c) return new ParseTree(this, position, String.valueOf(c));
		
		return null;
	}
	
	@Override
	public ExpressionType type() {
		return ExpressionType.CHAR_CLASS;
	}
	
	public static LiteralCharClass range(final char from, final char to) {
		final char[] chars = new char[to - from + 1];
		
		for(int i = 0; i < chars.length; i++)
			chars[i] = (char) (from + i);
		
		return new LiteralCharClass(chars);
	}
	
	public static LiteralCharClass range(final int from, final int to) {
		return range((char) from, (char) to);
	}
	
	public static LiteralCharClass union(final LiteralCharClass... classes) {
		final List<Character> chars = new LinkedList<>();
		
		for(final LiteralCharClass clazz : classes)
			for(final char c : clazz.chars)
				if(!chars.contains(c)) chars.add(c);
			
		final char[] cArray = new char[chars.size()];
		
		for(int i = 0; i < cArray.length; i++)
			cArray[i] = chars.get(i);
		
		return new LiteralCharClass(cArray);
	}
	
	public static LiteralCharClass digits() {
		return range('0', '9');
	}
	
	public static LiteralCharClass hexDigits() {
		return union(digits(), range('a', 'f'), range('A', 'F'));
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
		return union(range(0, 0x1F), of((char) 0x7F));
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
