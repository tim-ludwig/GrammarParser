package me.tludwig.parsing.peg.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.ast.AbstractSyntaxTree;
import me.tludwig.parsing.peg.expressions.Choice;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.Optional;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Repetition;
import me.tludwig.parsing.peg.expressions.Sequence;
import me.tludwig.parsing.peg.expressions.primaries.LiteralAnyChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;
import me.tludwig.parsing.peg.expressions.primaries.LiteralString;

public class PEGrammarGenerator {
	private final String     text;
	
	private GeneratedGrammar grammar;
	
	public PEGrammarGenerator(final String text) {
		this.text = text;
	}
	
	public PEGrammarGenerator(final Path in) throws IOException {
		this(Files.readAllLines(in).stream().collect(Collectors.joining("\n")));
	}
	
	public PEGrammarGenerator(final File in) throws IOException {
		this(in.toPath());
	}
	
	public PEGrammarGenerator generate() {
		try {
			final PEGrammarParser parser = new PEGrammarParser();
			final AbstractSyntaxTree grammarAST = parser.abstractSyntaxTree(text);
			
			String defName;
			for(final AbstractSyntaxTree ast : grammarAST.getChildren()) {
				defName = ast.getChildren(0).getText();
				
				if(grammar == null) grammar = new GeneratedGrammar(defName);
				
				grammar.definition(defName, expression(ast.getChildren(1)));
			}
		}catch(final Exception e) {
			grammar = null;
		}
		
		return this;
	}
	
	private Expression expression(final AbstractSyntaxTree exp) {
		if(exp.getChildrenCount() > 1) return Choice.of(exp.getChildren().stream().map(this::sequence).collect(Collectors.toList()));
		
		return sequence(exp.getChildren(0));
	}
	
	private Expression sequence(final AbstractSyntaxTree seq) {
		if(seq.getChildrenCount() > 1) return Sequence.of(seq.getChildren().stream().map(this::sequenceElement).collect(Collectors.toList()));
		
		return sequenceElement(seq.getChildren(0));
	}
	
	private Expression sequenceElement(final AbstractSyntaxTree elem) {
		if(elem.getChildrenCount() == 3) {
			System.out.println(elem.getChildren(2));
			
			return null;
		}else if(elem.getChildrenCount() == 2) {
			final AbstractSyntaxTree child0 = elem.getChildren(0), child1 = elem.getChildren(1);
			
			if(child0.getName().equals("Prefix")) {
				if(child0.getText().equals("&")) return Predicate.and(primary(child1));
				
				return Predicate.not(primary(child1));
			}
			
			switch(child1.getText()) {
				case "?":
					return Optional.of(primary(child0));
				case "*":
					return Repetition.zeroOrMore(primary(child0));
				case "+":
					return Repetition.oneOrMore(primary(child0));
			}
		}
		
		return primary(elem.getChildren(0));
	}
	
	private Expression primary(final AbstractSyntaxTree prim) {
		final String type = prim.getName(), text = prim.getText();
		
		switch(type) {
			case "Identifier":
				return grammar.definition(text);
			case "Expression":
				return expression(prim);
			case "Literal":
				final String value = text.substring(1, text.length() - 1);
				
				if(value.length() == 1) return LiteralChar.of(value.charAt(0));
				
				return LiteralString.of(value);
			case "Class":
				return prim.getChildren().stream().map(ast -> {
					if(ast.getChildrenCount() == 0) return LiteralCharClass.of(ast.getText().charAt(0));
					
					return LiteralCharClass.range(ast.getChildren(0).getText().charAt(0), ast.getChildren(1).getText().charAt(0));
				}).reduce(LiteralCharClass::union).get();
			case "DOT":
				return new LiteralAnyChar();
		}
		
		return null;
	}
	
	public PEGrammar getGrammar() {
		if(grammar == null) generate();
		
		return grammar;
	}
}
