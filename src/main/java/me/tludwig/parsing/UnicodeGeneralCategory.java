package me.tludwig.parsing;

public enum UnicodeGeneralCategory {
	UNASSIGNED,
	UPPERCASE_LETTER,
	LOWERCASE_LETTER,
	TITLECASE_LETTER,
	MODIFIER_LETTER,
	OTHER_LETTER,
	NON_SPACING_MARK,
	ENCLOSING_MARK,
	COMBINING_SPACING_MARK,
	DECIMAL_DIGIT_NUMBER,
	LETTER_NUMBER,
	OTHER_NUMBER,
	SPACE_SEPARATOR,
	LINE_SEPARATOR,
	PARAGRAPH_SEPARATOR,
	CONTROL,
	FORMAT,
	PRIVATE_USE,
	SURROGATE,
	DASH_PUNCTUATION,
	START_PUNCTUATION,
	END_PUNCTUATION,
	CONNECTOR_PUNCTUATION,
	OTHER_PUNCTUATION,
	MATH_SYMBOL,
	CURRENCY_SYMBOL,
	MODIFIER_SYMBOL,
	OTHER_SYMBOL,
	INITIAL_QUOTE_PUNCTUATION,
	FINAL_QUOTE_PUNCTUATION;
	
	public byte id() {
		return (byte) ordinal();
	}
	
	public static UnicodeGeneralCategory getById(final int id) {
		return values()[id];
	}
	
	public static UnicodeGeneralCategory of(final char c) {
		return of((int) c);
	}
	
	public static UnicodeGeneralCategory of(final int codePoint) {
		return getById(Character.getType(codePoint));
	}
}
