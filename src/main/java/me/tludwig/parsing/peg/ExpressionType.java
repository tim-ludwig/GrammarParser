package me.tludwig.parsing.peg;

import java.util.Arrays;

public enum ExpressionType {
	EOF(1),
	ANY_CHAR(2),
	CHAR(3),
	CHAR_CLASS(4),
	STRING(5),
	CHOICE(6),
	NON_TERMINAL(7),
	OPTIONAL(8),
	PREDICATE(9),
	REPETITION(10),
	SEQUENCE(11);
	
	private final byte id;
	
	private ExpressionType(final int id) {
		this.id = (byte) id;
	}
	
	public byte getId() {
		return id;
	}
	
	public static ExpressionType getById(final int id) {
		return Arrays.stream(ExpressionType.values()).filter(type -> type.id == id).findFirst().orElse(null);
	}
}
