package me.tludwig.parsing.peg.serialization;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.expressions.Choice;
import me.tludwig.parsing.peg.expressions.Expression;
import me.tludwig.parsing.peg.expressions.NonTerminal;
import me.tludwig.parsing.peg.expressions.Optional;
import me.tludwig.parsing.peg.expressions.Predicate;
import me.tludwig.parsing.peg.expressions.Repetition;
import me.tludwig.parsing.peg.expressions.Sequence;
import me.tludwig.parsing.peg.expressions.primaries.LiteralChar;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;
import me.tludwig.parsing.peg.expressions.primaries.LiteralString;

public final class PEGrammarSerializer {
	public static final byte[]          MAGIC_BYTES = { 0x0, 'P', 'E', 'G', 0x0, 0xA };
	
	private final PEGrammar             grammar;
	private final ByteArrayOutputStream baos;
	
	private byte[]                      bytes;
	
	public PEGrammarSerializer(final PEGrammar grammar) {
		this.grammar = grammar;
		
		baos = new ByteArrayOutputStream(MAGIC_BYTES.length + 4 + grammar.getDefinitions().size() * 30);
	}
	
	public PEGrammarSerializer serialize() {
		write(MAGIC_BYTES);
		writeString(grammar.getStartSymbol().getName());
		
		final Map<String, Expression> defs = grammar.getDefinitions();
		
		writeInt(defs.size());
		defs.forEach((name, exp) -> {
			writeString(name);
			serializeExpression(exp);
		});
		
		bytes = baos.toByteArray();
		
		return this;
	}
	
	private void serializeExpression(final Expression exp) {
		final ExpressionType type = exp.type();
		
		write(type.getId());
		
		switch(type) {
			case CHAR:
				writeChar(((LiteralChar) exp).getChar());
				break;
			case CHAR_CLASS:
				final char[] chars = ((LiteralCharClass) exp).getChars();
				
				writeInt(chars.length);
				for(final char c : chars)
					writeChar(c);
				
				break;
			case CHOICE:
				final Expression[] sub = ((Choice) exp).getSubExpressions();
				
				writeInt(sub.length);
				for(final Expression e : sub)
					serializeExpression(e);
				
				break;
			case NON_TERMINAL:
				writeString(((NonTerminal) exp).getName());
				break;
			case OPTIONAL:
				serializeExpression(((Optional) exp).getExpression());
				break;
			case PREDICATE:
				final Predicate pre = (Predicate) exp;
				
				write(pre.getType().getId());
				serializeExpression(pre.getExpression());
				break;
			case REPETITION:
				final Repetition rep = (Repetition) exp;
				
				writeInt(rep.getMin());
				writeInt(rep.getMax());
				
				serializeExpression(rep.getExpression());
				break;
			case SEQUENCE:
				final Expression[] sub1 = ((Sequence) exp).getSubExpressions();
				
				writeInt(sub1.length);
				for(final Expression e : sub1)
					serializeExpression(e);
				
				break;
			case STRING:
				writeString(((LiteralString) exp).getString());
				break;
			case ANY_CHAR:
			case EOF:
				break;
		}
	}
	
	public PEGrammarSerializer write(final File out) throws IOException {
		return write(out.toPath());
	}
	
	public PEGrammarSerializer write(final Path out) throws IOException {
		return write(Files.newOutputStream(out), true);
	}
	
	public PEGrammarSerializer write(final OutputStream out) throws IOException {
		return write(out, false);
	}
	
	public PEGrammarSerializer write(final OutputStream out, final boolean closeStream) throws IOException {
		if(bytes == null) serialize();
		
		out.write(bytes);
		out.flush();
		
		if(closeStream) out.close();
		
		return this;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	private void write(final byte b) {
		baos.write(b);
	}
	
	private void write(final byte[] b) {
		baos.write(b, 0, b.length);
	}
	
	private void writeInt(final int i) {
		write((byte) (i >>> 24));
		write((byte) (i >>> 16));
		write((byte) (i >>> 8));
		write((byte) i);
	}
	
	private void writeChar(final char c) {
		write((byte) (c >>> 8));
		write((byte) c);
	}
	
	private void writeString(final String s) {
		final byte[] bytes = s.getBytes();
		
		writeInt(bytes.length);
		write(bytes);
	}
}
