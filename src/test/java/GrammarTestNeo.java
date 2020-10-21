import me.tludwig.parsing.ABNFParser;

public class GrammarTestNeo {
	
	public static void main(final String[] args) throws Exception {
		final ABNFParser parser = new ABNFParser();
		
		//		System.out.println(parser.match(Files.readAllLines(Paths.get("bin/toml.abnf")).stream().collect(Collectors.joining("\r\n"))));
		System.out.println(parser);
	}
}
