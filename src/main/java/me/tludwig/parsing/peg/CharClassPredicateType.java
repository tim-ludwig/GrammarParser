package me.tludwig.parsing.peg;

public enum CharClassPredicateType {
	CONSTANT,
	RANGE,
	LIST,
	CATEGORY,
	CATEGORIES,
	BLOCK,
	UNION,
	INTERSECTION,
	NEGATION;
	
	public int getId() {
		return ordinal();
	}
	
	public static CharClassPredicateType getById(final int id) {
		return values()[id];
	}
}
