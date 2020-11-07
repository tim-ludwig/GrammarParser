package me.tludwig.parsing.peg.serialization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.expressions.Choice;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.Optional;
import me.tludwig.parsing.peg.expressions.Predicate.PredicateType;
import me.tludwig.parsing.peg.expressions.Sequence;
import me.tludwig.parsing.peg.expressions.primaries.EndOfFile;
import me.tludwig.parsing.peg.expressions.primaries.LiteralAnyChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;
import me.tludwig.parsing.peg.expressions.primaries.LiteralString;

public class PEGrammarDeserializer {
	public static final byte[]         MAGIC_BYTES = PEGrammarSerializer.MAGIC_BYTES;
	
	private final ByteArrayInputStream bais;
	
	private GeneratedGrammar           grammar;
	
	public PEGrammarDeserializer(final InputStream in, final boolean closeStream) throws IOException {
		bais = new ByteArrayInputStream(in.readAllBytes());
		
		if(closeStream) in.close();
	}
	
	public PEGrammarDeserializer(final InputStream in) throws IOException {
		this(in, false);
	}
	
	public PEGrammarDeserializer(final Path in) throws IOException {
		this(Files.newInputStream(in), true);
	}
	
	public PEGrammarDeserializer(final File in) throws IOException {
		this(in.toPath());
	}
	
	public PEGrammarDeserializer deserialize() {
		if(Arrays.compare(read(MAGIC_BYTES.length), MAGIC_BYTES) != 0) throw new IllegalStateException("");
		
		try {
			grammar = new GeneratedGrammar(readString());
			
			final int defCount = readInt();
			for(int i = 0; i < defCount; i++)
				grammar.definition(readString(), deserializeExpression());
		}catch(final Exception e) {
			grammar = null;
		}
		
		return this;
	}
	
	private Expression deserializeExpression() {
		final ExpressionType type = ExpressionType.getById(read());
		
		switch(type) {
			case ANY_CHAR:
				return new LiteralAnyChar();
			case CHAR:
				return LiteralChar.of(readChar());
			case CHAR_CLASS:
				final char[] chars = new char[readInt()];
				
				for(int i = 0; i < chars.length; i++)
					chars[i] = readChar();
				
				return LiteralCharClass.of(chars);
			case CHOICE:
				final Expression[] sub = new Expression[readInt()];
				
				for(int i = 0; i < sub.length; i++)
					sub[i] = deserializeExpression();
				
				return Choice.of(sub);
			case EOF:
				return new EndOfFile();
			case NON_TERMINAL:
				return grammar.definition(readString());
			case OPTIONAL:
				return Optional.of(deserializeExpression());
			case PREDICATE:
				return grammar.predicate(PredicateType.getById(read()), deserializeExpression());
			case REPETITION:
				return grammar.repetition(readInt(), readInt(), deserializeExpression());
			case SEQUENCE:
				final Expression[] sub2 = new Expression[readInt()];
				
				for(int i = 0; i < sub2.length; i++)
					sub2[i] = deserializeExpression();
				
				return Sequence.of(sub2);
			case STRING:
				return LiteralString.of(readString());
		}
		
		return null;
	}
	
	public PEGrammar getGrammar() {
		if(grammar == null) deserialize();
		
		return grammar;
	}
	
	private byte read() {
		return (byte) bais.read();
	}
	
	private byte[] read(final int n) {
		final byte[] bytes = new byte[n];
		
		bais.read(bytes, 0, n);
		
		return bytes;
	}
	
	private int readInt() {
		return (read() & 0xFF) << 24 | (read() & 0xFF) << 16 | (read() & 0xFF) << 8 | read() & 0xFF;
	}
	
	private char readChar() {
		return (char) ((read() & 0xFF) << 8 | read() & 0xFF);
	}
	
	private String readString() {
		return new String(read(readInt()));
	}
}
