package me.tludwig.parsing.peg;

public class PEGParser extends PEGrammar {
	
	public PEGParser() {
		super("Grammar");
	}
	
	@Override
	protected void init() {
		//@formatter:off
		def("Grammar", seq(def("Spacing"), oneOrMore(def("Definition")), and(EOF())));
		def("Definition", seq(def("Identifier"), def("LEFTARROW"), def("Expression")));

		def("Expression", seq(def("Sequence"), zeroOrMore(seq(def("SLASH"), def("Sequence")))));
		def("Sequence", zeroOrMore(def("Prefix")));
		def("Prefix", seq(opt(choice(def("AND"), def("NOT"))), def("Suffix")));
		def("Suffix", seq(def("Primary"), opt(choice(def("QUESTION"), def("STAR"), def("PLUS")))));
		def("Primary", choice(	seq(def("Identifier"), not(def("LEFTARROW"))),
								seq(def("OPEN"), def("Expression"), def("CLOSE")),
								def("Literal"), def("Class"), def("DOT")));

		def("Identifier", seq(def("IdentStart"), zeroOrMore(def("IdentCont")), def("Spacing")));
		def("IdentStart", list("a-zA-Z_"));
		def("IdentCont", choice(def("IdentStart"), list("0-9")));

		def("Literal", choice(	seq(character('\''), zeroOrMore(seq(not(character('\'')), def("Char"))), character('\''), def("Spacing")),
								seq(character('"'), zeroOrMore(seq(not(character('"')), def("Char"))), character('"'), def("Spacing"))));
		def("Class", seq(character('['), zeroOrMore(seq(not(character(']')), def("Range"))), character(']'), def("Spacing")));
		def("Range", choice(seq(def("Char"), character('-'), def("Char")), def("Char")));
		def("Char", choice(	seq(character('\\'), list("nrt'\"[]\\")),
							seq(character('\\'), list("0-2"), list("0-7"), list("0-7")),
							seq(character('\\'), list("0-7"), opt(list("0-7"))),
							seq(not(character('\\')), any())));

		def("LEFTARROW", seq(string("<-"), def("Spacing")));
		def("SLASH", seq(character('/'), def("Spacing")));
		def("AND", seq(character('&'), def("Spacing")));
		def("NOT", seq(character('!'), def("Spacing")));
		def("QUESTION", seq(character('?'), def("Spacing")));
		def("STAR", seq(character('*'), def("Spacing")));
		def("PLUS", seq(character('+'), def("Spacing")));
		def("OPEN", seq(character('('), def("Spacing")));
		def("CLOSE", seq(character(')'), def("Spacing")));
		def("DOT", seq(character('.'), def("Spacing")));

		def("Spacing", zeroOrMore(choice(def("Space"), def("Comment"))));
		def("Comment", seq(character('#'), zeroOrMore(seq(not(def("EndOfLine")), any())), def("EndOfLine")));
		def("Space", choice(character(' '), character('\t'), def("EndOfLine")));
		def("EndOfLine", choice(string("\r\n"), character('\n'), character('\r')));
		//@formatter:on
	}
}
