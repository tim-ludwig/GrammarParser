package me.tludwig.parsing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.ast.AbstractSyntaxTree;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.primaries.LiteralAnyChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;

public class ParserGenerator {
	private final String text;
	
	private PEGrammar grammar;
	
	public ParserGenerator(final String text) {
		this.text = text;
	}
	
	public ParserGenerator(final Path in) throws IOException {
		this(Files.readAllLines(in).stream().collect(Collectors.joining("\n")));
	}
	
	public ParserGenerator(final File in) throws IOException {
		this(in.toPath());
	}
	
	public ParserGenerator generate() {
		try {
			final PEGParser parser = new PEGParser();
			final AbstractSyntaxTree grammarAST = parser.abstractSyntaxTree(text);
			
			grammar = new PEGrammar(grammarAST.getChildren(0).getChildren(0).getText()) {
				
				@Override
				protected void init() {
					for(final AbstractSyntaxTree ast : grammarAST.getChildren())
						def(ast.getChildren(0).getText(), expression(ast.getChildren(1)));
				}
				
				private Expression expression(final AbstractSyntaxTree exp) {
					if(exp.getChildrenCount() > 1) return choice(exp.getChildren().stream().map(this::sequence).toArray(Expression[]::new));
					
					return sequence(exp.getChildren(0));
				}
				
				private Expression sequence(final AbstractSyntaxTree seq) {
					if(seq.getChildrenCount() > 1) return seq(seq.getChildren().stream().map(this::sequenceElement).toArray(Expression[]::new));
					
					return sequenceElement(seq.getChildren(0));
				}
				
				private Expression sequenceElement(final AbstractSyntaxTree elem) {
					if(elem.getChildrenCount() == 3) {
						System.err.println(elem.getChildren(2)); // XXX
						
						return null;
					}else if(elem.getChildrenCount() == 2) {
						final AbstractSyntaxTree child0 = elem.getChildren(0), child1 = elem.getChildren(1);
						
						if(child0.getName().equals("Prefix")) {
							if(child0.getText().equals("&")) return and(primary(child1));
							
							return not(primary(child1));
						}
						
						switch(child1.getText()) {
							case "?":
								return opt(primary(child0));
							case "*":
								return zeroOrMore(primary(child0));
							case "+":
								return oneOrMore(primary(child0));
						}
					}
					
					return primary(elem.getChildren(0));
				}
				
				private Expression primary(final AbstractSyntaxTree prim) {
					final String type = prim.getName(), text = prim.getText();
					
					switch(type) {
						case "Identifier":
							return def(text);
						case "Expression":
							return expression(prim);
						case "Literal":
							final String value = text.substring(1, text.length() - 1);
							
							if(value.length() == 1) return character(value.charAt(0));
							
							return string(value);
						case "Class":
							return prim.getChildren().stream().map(ast -> {
								if(ast.getChildrenCount() == 0) return list(ast.getText().charAt(0));
								
								return range(ast.getChildren(0).getText().charAt(0), ast.getChildren(1).getText().charAt(0));
							}).reduce(LiteralCharClass::union).get();
						case "DOT":
							return new LiteralAnyChar();
					}
					
					return null;
				}
				
			};
		}catch(final Exception e) {
			grammar = null;
		}
		
		return this;
	}
	
	public PEGrammar getGrammar() {
		if(grammar == null) generate();
		
		return grammar;
	}
}
