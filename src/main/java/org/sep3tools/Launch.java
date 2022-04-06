package org.sep3tools;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.postgresql.pljava.annotation.Function;
import org.sep3tools.gen.*;

public class Launch {

	public static void main(String[] args) {
		String sep3String;
		if (args.length == 6) {
			JavaConnector.setUrl(args[0]);
			JavaConnector.setUser(args[1]);
			JavaConnector.setPass(args[2]);
			JavaConnector.setWb(args[3]);
			JavaConnector.setSt(args[4]);
			sep3String = args[5];
		}
		else if (args.length == 0 || args[0].isEmpty()) {
			sep3String = "^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)";
		}
		else if (args.length == 1) {
			sep3String = args[0];
		}
		else {
			System.out.println("Aufruf mit folgenden Parametern:\n"
					+ "[JDBC-URL] [DB-User] [DB-Passwort] [Woerterbuch-Tabelle] [Schlüsseltypen-Tabelle] <SEP3-String>\n\n"
					+ "Beispiel für Parameter:\n" + "\"jdbc:postgresql://localhost/petroparser\" " + "\"petroDB\" "
					+ "\"PetroPass\" " + "\"woerterbuch.\\\"\"Woerterbuch\\\"\" "
					+ "\"woerterbuch.\\\"\"Schluesseltypen\\\"\" "
					+ "\"G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg2,mx(voe))\"");
			return;
		}
		String visit = parseS3(sep3String);
		System.out.println(visit);
	}

	@Function
	public static String parseS3(String s3String) {
		CharStream input = CharStreams.fromString(s3String);
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		PetroVisitor visitor = new PetroVisitor();
		visitor.setInDb(false);
		return (visitor.visit(tree));
	}

}