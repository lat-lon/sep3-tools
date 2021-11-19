package org.sep3tools;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sep3tools.gen.*;

public class Launch {

	public static void main(String[] args) {
		String sep3String = (args.length == 0 || args[0].isEmpty())
				? "^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)" : args[0];
		String visit = parseS3(sep3String);
		System.out.println(visit);
	}

	public static String parseS3(String s3String) {
		CharStream input = CharStreams.fromString(s3String);
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		PetroVisitor visitor = new PetroVisitor();
		return (visitor.visit(tree));
	}

	public static String parseS3(String wb, String st, String s3String) {
		PLJavaConnector.setWb(wb);
		PLJavaConnector.setSt(st);
		return parseS3(s3String);
	}

}