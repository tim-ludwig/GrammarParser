package me.tludwig.parsing.peg.ast;

public enum ASTRule {
	FULL, // don't ignore this node (default for nonterminal expressions)
	SKIP, // Ignore this node, replace it with all its children (default for non-primary Expressions?)
	TEXT, // Igonre this nodes children (default for primary Expressions)
	NONE // Ignore this node, its text and all its children
}
