package me.tludwig.parsing;

import static me.tludwig.parsing.peg.ast.ASTRule.NONE;
import static me.tludwig.parsing.peg.ast.ASTRule.SKIP;
import static me.tludwig.parsing.peg.ast.ASTRule.TEXT;

import me.tludwig.parsing.peg.PEGrammar;

public class PEGParser extends PEGrammar {
	
	public PEGParser() {
		super("Grammar");
		
		//@formatter:off
		def("Grammar",		seq(def("Spacing"), oneOrMore(def("Definition")), and(EOF())));
		def("Definition",	seq(def("Identifier"), def("LEFTARROW"), def("Expression")));

		def("Expression",		seq(def("Sequence"), zeroOrMore(seq(def("SLASH"), def("Sequence")))));
		def("Sequence",			zeroOrMore(def("SequenceElement")));
		def("SequenceElement",	seq(opt(def("Prefix")), def("Primary"), opt(def("Suffix"))));
		def("Prefix",			choice(def("AND"), def("NOT"), def("Suffix")));
		def("Suffix",			choice(def("QUESTION"), def("STAR"), def("PLUS")));
		def("Primary", SKIP,	choice(seq(def("Identifier"), not(def("LEFTARROW"))),
									   seq(def("OPEN"), def("Expression"), def("CLOSE")),
									   def("Literal"), def("Class"), def("DOT")));

		def("Identifier",		seq(def("IdentStart"), zeroOrMore(def("IdentCont")), def("Spacing")));
		def("IdentStart", TEXT,	list("a-zA-Z_"));
		def("IdentCont",  TEXT,	choice(def("IdentStart"), list("0-9")));

		def("Literal",	choice(seq(character('\''), zeroOrMore(seq(not(character('\'')), def("Char"))), character('\''), def("Spacing")),
							   seq(character('"'), zeroOrMore(seq(not(character('"')), def("Char"))), character('"'), def("Spacing"))));
		def("Class",	seq(character('['), zeroOrMore(seq(not(character(']')), choice(def("Range"), def("Char")))), character(']'), def("Spacing")));
		def("Range",	seq(def("Char"), character('-'), def("Char")));
		def("Char",		choice(seq(character('\\'), list("nrt'\"[]\\")),
							   seq(character('\\'), list("0-2"), list("0-7"), list("0-7")),
							   seq(character('\\'), list("0-7"), opt(list("0-7"))),
							   seq(not(character('\\')), any())));

		def("LEFTARROW", TEXT,	seq(string("<-"), def("Spacing")));
		def("SLASH", TEXT,		seq(character('/'), def("Spacing")));
		def("AND",				seq(character('&'), def("Spacing")));
		def("NOT",				seq(character('!'), def("Spacing")));
		def("QUESTION",			seq(character('?'), def("Spacing")));
		def("STAR",				seq(character('*'), def("Spacing")));
		def("PLUS",				seq(character('+'), def("Spacing")));
		def("OPEN", TEXT,		seq(character('('), def("Spacing")));
		def("CLOSE", TEXT,		seq(character(')'), def("Spacing")));
		def("DOT",				seq(character('.'), def("Spacing")));

		def("Spacing", NONE,	zeroOrMore(choice(def("Space"), def("Comment"))));
		def("Comment",			seq(character('#'), zeroOrMore(seq(not(def("EndOfLine")), any())), def("EndOfLine")));
		def("Space",			choice(character(' '), character('\t'), def("EndOfLine")));
		def("EndOfLine",		choice(string("\r\n"), character('\n'), character('\r')));
		//@formatter:on
	}
}
