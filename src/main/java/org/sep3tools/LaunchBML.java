package org.sep3tools;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.postgresql.pljava.annotation.Function;
import org.sep3tools.gen.PetroGrammarLexer;
import org.sep3tools.gen.PetroGrammarParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command line launcher for SEP3 to BML tool
 *
 * @author Jeronimo Wanhoff <kontakt@jeronimowanhoff.de>
 */
public class LaunchBML {

	/**
	 * Launches SEP3 tool with arguments:
	 * @param args DB URL, DB user, DB password, Schluesselmapping table name SEP3 string
	 * to process
	 */
	public static void main(String[] args) {
		String sep3String;
		if (args.length == 5) {
			JavaConnector.setUrl(args[0]);
			JavaConnector.setUser(args[1]);
			JavaConnector.setPass(args[2]);
			JavaConnector.setSm(args[3]);
			sep3String = args[4];
		}
		else if (args.length == 0 || args[0].isEmpty()) {
			sep3String = "^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)";
		}
		else if (args.length == 1) {
			sep3String = args[0];
		}
		else {
			System.out.println("Aufruf mit folgenden Parametern:\n"
					+ "[JDBC-URL] [DB-User] [DB-Passwort] [Schüsselmapping-Tabelle] <SEP3-String>\n\n"
					+ "Beispiel für Parameter:\n" + "\"jdbc:postgresql://localhost/petroparser\" " + "\"petroDB\" "
					+ "\"PetroPass\" " + "\"bml.schlusselmapping.\" "
					+ "\"G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg2,mx(voe))\"");
			return;
		}
		String visit = parseS3BML(sep3String);
		System.out.println(visit);
	}

	/**
	 * translates a coded SEP3 String to BML code
	 * @param s3String coded SEP3 string parsing
	 * @return BML format of SEP3 input
	 */
	@Function
	public static String parseS3BML(String s3String) {
		CharStream input = CharStreams.fromString(s3String);
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		BmlVisitor visitor = new BmlVisitor();

		List<String> compList = new ArrayList();
		compList.add(visitor.visit(tree));

		List<String> result = compList.stream().flatMap(str -> Arrays.stream(str.split(",\\s*"))).distinct().toList();
		String resultString = result.toString();
		resultString = resultString.substring(1, resultString.length() - 1);
		resultString = resultString.replaceAll(" , ", " ");
		if (resultString.startsWith(", ")) {
			resultString = resultString.substring(2);
		}

		return (resultString);
	}

}