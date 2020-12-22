package me.tludwig.parsing.peg;

import java.util.List;

import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.NonTerminal;

public final class ParseTree {
	private final Expression  expression;
	private final int         pos;
	private final String      matchedText;
	private final ParseTree[] subMatches;
	
	public ParseTree(final Expression expression, final int pos, final String matchedText,
			final ParseTree... subMatches) {
		this.expression = expression;
		this.pos = pos;
		this.matchedText = matchedText;
		this.subMatches = subMatches;
	}
	
	public ParseTree(final Expression expression, final int pos, final String matchedText,
			final List<ParseTree> subMatches) {
		this(expression, pos, matchedText, subMatches.toArray(new ParseTree[subMatches.size()]));
	}
	
	public ParseTree(final Expression expression, final int pos, final String matchedText) {
		this(expression, pos, matchedText, new ParseTree[0]);
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public int getPos() {
		return pos;
	}
	
	public String getMatchedText() {
		return matchedText;
	}
	
	public int getEnd() {
		return pos + matchedText.length();
	}
	
	public ParseTree[] getChildren() {
		return subMatches;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		string(sb, "");
		
		return sb.toString();
	}
	
	private void string(final StringBuilder sb, final String indent) {
		sb.append(indent);
		if(expression instanceof NonTerminal) {
			sb.append(((NonTerminal) expression).getName());
			sb.append(": {\n");
		} else {
			sb.append("{[");
			sb.append(expression.getClass().getSimpleName());
			sb.append("]\n");
		}
		
		sb.append(indent);
		sb.append("\tText: \"");
		sb.append(matchedText.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")
				.replace("\f", "\\f").replace("\"", "\\\""));
		sb.append("\"\n");
		
		sb.append(indent);
		sb.append("\tPos: ");
		sb.append(pos);
		sb.append("\n");
		
		if(subMatches != null) {
			for(final ParseTree subParseTree : subMatches) {
				subParseTree.string(sb, indent + "\t");
				
				sb.append("\n");
			}
		}
		
		sb.append(indent);
		sb.append("}");
	}
}
