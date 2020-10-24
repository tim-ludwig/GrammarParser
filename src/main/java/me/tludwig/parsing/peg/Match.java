package me.tludwig.parsing.peg;

import java.util.List;

import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.primaries.NonTerminal;

public final class Match {
	private final Expression expression;
	private final int        pos;
	private final String     matchedText;
	private final Match[]    subMatches;
	
	public Match(final Expression expression, final int pos, final String matchedText, final Match[] subMatches) {
		this.expression = expression;
		this.pos = pos;
		this.matchedText = matchedText;
		this.subMatches = subMatches;
	}
	
	public Match(final Expression expression, final int pos, final String matchedText, final List<Match> subMatches) {
		this(expression, pos, matchedText, subMatches.toArray(new Match[subMatches.size()]));
	}
	
	public Match(final Expression expression, final int pos, final String matchedText) {
		this(expression, pos, matchedText, new Match[0]);
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
	
	public Match[] getSubMatches() {
		return subMatches;
	}
	
	@Override
	public String toString() {
		//		return "Match[pos=" + pos + ", matchedText=\"" + matchedText + "\", subMatches=" + subMatches + "]";
		final StringBuilder sb = new StringBuilder();
		
		string(sb, "");
		
		return sb.toString();
	}
	
	private void string(final StringBuilder sb, final String indent) {
		sb.append(indent);
		sb.append("{\n");
		
		if(expression instanceof NonTerminal) {
			sb.append(indent);
			sb.append('\t');
			sb.append(((NonTerminal) expression).getName());
			sb.append("\n");
		}
		
		sb.append(indent);
		sb.append("\tText: '");
		sb.append(matchedText);
		sb.append("'\n");
		
		sb.append(indent);
		sb.append("\tPos: ");
		sb.append(pos);
		sb.append("\n");
		
		if(subMatches != null) for(final Match subMatch : subMatches) {
			subMatch.string(sb, indent + "\t");
			
			sb.append("\n");
		}
		
		sb.append(indent);
		sb.append("}");
	}
}
