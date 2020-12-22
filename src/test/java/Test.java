import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.tludwig.parsing.PEGParser;
import me.tludwig.parsing.peg.PEGrammar;
import me.tludwig.parsing.peg.ParseTree;

public class Test {
	public static void main(final String[] args) throws IOException {
		final PEGrammar parser = new PEGParser();
		
		String text = "";
		
		for(final String line : Files.readAllLines(Paths.get("target/test-classes/selfdefinitionPEG.txt"))) {
			text += line + "\r\n";
		}
		
		final ParseTree parseTree = parser.parseTree(text);
		System.out.println(text);
		System.out.println(parseTree);
		System.out.println(text);
		System.out.println(parser.abstractSyntaxTree(text));
	}
	
//	public static void main(final String[] args) {
//		final PEGrammar parser = new PEGParser();
//
//		System.out.println(parser.toString());
//	}
	
	// public static void main(final String[] args) {
	// final ANBN anbn = new ANBN();
	//
	// test("", anbn);
	// test("ab", anbn);
	// test("aabb", anbn);
	// test("aaabbb", anbn);
	// test("aaaabbbb", anbn);
	//
	// System.out.println("S <- " + anbn.definitions.get("S"));
	// }
	
	private static void test(final String input, final PEGrammar parser) {
		System.out.println(input);
		System.out.println(parser.parseTree(input));
	}
	
	public static class ANBN extends PEGrammar {
		
		public ANBN() {
			super("S");
		}
		
		@Override
		public void init() {
			def("S", choice(seq(character('a'), def("S"), character('b')), string("")));
		}
	}
}
