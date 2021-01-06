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
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Predicate.PredicateType;
import me.tludwig.parsing.peg.expressions.primaries.EndOfFile;

public class PEGrammarDeserializer {
	public static final byte[] MAGIC_BYTES = PEGrammarSerializer.MAGIC_BYTES;
	
	private final ByteArrayInputStream bais;
	
	private PEGrammar grammar;
	
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
			grammar = new PEGrammar(readString()) {
				@Override
				protected void init() {
					final int defCount = readInt();
					for(int i = 0; i < defCount; i++)
						def(readString(), deserializeExpression());
				}
				
				private Expression deserializeExpression() {
					final ExpressionType type = ExpressionType.getById(read());
					
					switch(type) {
						case ANY_CHAR:
							return any();
						case CHAR:
							return character(readChar());
						case CHAR_CLASS:
							final char[] chars = new char[readInt()];
							
							for(int i = 0; i < chars.length; i++)
								chars[i] = readChar();
							
							return list(chars);
						case CHOICE:
							final Expression[] sub = new Expression[readInt()];
							
							for(int i = 0; i < sub.length; i++)
								sub[i] = deserializeExpression();
							
							return choice(sub);
						case EOF:
							return new EndOfFile();
						case NON_TERMINAL:
							return def(readString());
						case OPTIONAL:
							return opt(deserializeExpression());
						case PREDICATE:
							byte predicateType = read();
							return Predicate.of(deserializeExpression(), PredicateType.getById(predicateType));
						case REPETITION:
							int min = readInt(), max = readInt();
							return between(deserializeExpression(), min, max);
						case SEQUENCE:
							final Expression[] sub2 = new Expression[readInt()];
							
							for(int i = 0; i < sub2.length; i++)
								sub2[i] = deserializeExpression();
							
							return seq(sub2);
						case STRING:
							return string(readString());
					}
					
					return null;
				}
			};
		} catch(final Exception e) {
			grammar = null;
		}
		
		return this;
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
