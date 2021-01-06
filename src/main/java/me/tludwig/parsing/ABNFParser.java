package me.tludwig.parsing;

import me.tludwig.parsing.peg.PEGrammar;

public class ABNFParser extends PEGrammar {
	
	public ABNFParser() {
		super("rulelist");
		
		//@formatter:off
		def("rulelist",			oneOrMore(choice(def("rule"), seq(zeroOrMore(def("c-wsp")), def("c-nl")))));
		def("rule",				seq(def("rulename"), def("defined-as"), def("elements"), def("c-nl")));
		def("rulename",			seq(def("ALPHA"), zeroOrMore(choice(def("ALPHA"), def("DIGIT"), character('-')))));
		def("defined-as",		seq(zeroOrMore(def("c-wsp")), choice(string("=/"), character('=')), zeroOrMore(def("c-wsp"))));
		def("elements",			seq(def("alternation"), zeroOrMore(def("c-wsp"))));
		def("c-wsp",			choice(def("WSP"), seq(def("c-nl"), def("WSP"))));
		def("c-nl",				choice(def("comment"), def("CRLF")));
		def("comment",			seq(character(';'), zeroOrMore(choice(def("WSP"), def("VCHAR"))), def("CRLF")));
		def("alternation",		seq(def("concatenation"), zeroOrMore(zeroOrMore(def("c-wsp")), character('/'), zeroOrMore(def("c-wsp")), def("concatenation"))));
		def("concatenation",	seq(def("repetition"), zeroOrMore(oneOrMore(def("c-wsp")), def("repetition"))));
		def("repetition",		seq(opt(def("repeat")), def("element")));
		def("repeat",			choice(seq(zeroOrMore(def("DIGIT")), character('*'), zeroOrMore(def("DIGIT"))), oneOrMore(def("DIGIT"))));
		def("element",			choice(def("rulename"), def("group"), def("option"), def("char-val"), def("num-val"), def("prose-val")));
		def("group",			seq(character('('), zeroOrMore(def("c-wsp")), def("alternation"), zeroOrMore(def("c-wsp")), character(')')));
		def("option",			seq(character('['), zeroOrMore(def("c-wsp")), def("alternation"), zeroOrMore(def("c-wsp")), character(']')));
		
		def("char-val",		seq(def("DQUOTE"), zeroOrMore(choice(range(0x20, 0x21), range(0x23, 0x7E))), def("DQUOTE")));
		def("num-val",		seq(character('%'), choice(def("bin-val"),def("dec-val"), def("hex-val"))));
		def("bin-val",		seq(character('b'), oneOrMore(def("BIT")), opt(choice(oneOrMore(character('.'), oneOrMore(def("BIT"))), seq(character('-'), oneOrMore(def("BIT")))))));
		def("dec-val",		seq(character('d'), oneOrMore(def("DIGIT")), opt(choice(oneOrMore(character('.'), oneOrMore(def("DIGIT"))), seq(character('-'), oneOrMore(def("DIGIT")))))));
		def("hex-val",		seq(character('x'), oneOrMore(def("HEXDIG")), opt(choice(oneOrMore(character('.'), oneOrMore(def("HEXDIG"))), seq(character('-'), oneOrMore(def("HEXDIG")))))));
		def("prose-val",	seq(character('<'), zeroOrMore(choice(range(0x20, 0x3D), range(0x3F, 0x7E))), character('>')));
		
		def("ALPHA",	list("A-Za-z"));
		def("BIT",		list("01"));
		def("CHAR",		range(0x01, 0x7F));
		def("CR",		character('\r'));
		def("CRLF",		CRLF());
		def("CTL",		control());
		def("DIGIT",	digits());
		def("DQUOTE",	character('"'));
		def("HEXDIG",	hexdigits());
		def("HTAB",		character('\t'));
		def("LF",		character('\n'));
		def("LWSP",		zeroOrMore(choice(def("WSP"), seq(def("CRLF"), def("WSP")))));
		def("OCTET",	range(0x0, 0xFF));
		def("SP",		character(' '));
		def("VCHAR",	visible());
		def("WSP",		choice(def("SP"), def("HTAB")));
		//@formatter:on
	}
}
