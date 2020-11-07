package me.tludwig.parsing.peg;

public enum ExpressionType {
	EOF,
	ANY_CHAR,
	CHAR,
	CHAR_CLASS,
	STRING,
	CHOICE,
	NON_TERMINAL,
	OPTIONAL,
	PREDICATE,
	REPETITION,
	SEQUENCE;
	
	public byte getId() {
		return (byte) ordinal();
	}
	
	public static ExpressionType getById(final int id) {
		return values()[id];
	}
}
