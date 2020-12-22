package me.tludwig.parsing.peg.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.tludwig.parsing.peg.ParseTree;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.NonTerminal;
import me.tludwig.parsing.peg.expressions.primaries.Primary;

public class AbstractSyntaxTree {
	private final List<AbstractSyntaxTree> children;
	
	private final String name, text;
	
	public AbstractSyntaxTree(final List<AbstractSyntaxTree> children, final String name, final String text) {
		this.children = children;
		this.name = name;
		this.text = text;
	}
	
	public static AbstractSyntaxTree createAST(final ParseTree parseTree, final Map<String, ASTRule> conversionRules) {
		final LinkedList<AbstractSyntaxTree> children = new LinkedList<>();
		String text = "";
		String name = "";
		
		if(parseTree.getExpression() instanceof NonTerminal) {
			name = ((NonTerminal) parseTree.getExpression()).getName();
		}
		
		Expression expression;
		ASTRule rule;
		AbstractSyntaxTree child;
		
		for(final ParseTree ptChild : parseTree.getChildren()) {
			expression = ptChild.getExpression();
			
			if(expression instanceof NonTerminal) {
				rule = conversionRules.getOrDefault(((NonTerminal) expression).getName(), ASTRule.FULL);
			} else if(expression instanceof Primary) {
				rule = ASTRule.TEXT;
				
				text += ptChild.getMatchedText();
			} else {
				rule = ASTRule.SKIP;
			}
			
			if(rule == ASTRule.NONE) {
				continue;
			}
			
			child = createAST(ptChild, conversionRules);
			
			text += child.getText();
			
			if(rule == ASTRule.FULL) {
				children.add(child);
			} else if(rule == ASTRule.SKIP) {
				children.addAll(child.getChildren());
			}
		}
		
		return new AbstractSyntaxTree(children, name, text);
	}
	
	public AbstractSyntaxTree getChildren(final int index) {
		return children.get(index);
	}
	
	public int getChildrenCount() {
		return children.size();
	}
	
	public List<AbstractSyntaxTree> getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}
	
	private void string(final StringBuilder sb, final String indent) {
		sb.append(indent);
		sb.append(name);
		sb.append(": [\n");
		
		sb.append(indent);
		sb.append("\tText: \"");
		sb.append(text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")
				.replace("\f", "\\f").replace("\"", "\\\""));
		sb.append("\"\n");
		
		for(final AbstractSyntaxTree subTree : children) {
			subTree.string(sb, indent + "\t");
			
			sb.append("\n");
		}
		
		sb.append(indent);
		sb.append("]");
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		string(sb, "");
		
		return sb.toString();
	}
	
	public String getText() {
		return text;
	}
}
