package org.sep3tools;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.sep3tools.gen.PetroGrammarLexer;
import org.sep3tools.gen.PetroGrammarParser;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:friebe@lat-lon.de">Torsten Friebe</a>
 * @author <a href="mailto:kontakt@jeronimowanhoff.de">Jeronimo Wanhoff</a>
 */
public class ParserTest {

	@Test
	public void verifyThatRockCodeIsParsed() {
		CharStream input = CharStreams.fromString("^ms(r2,r3(tw),gs(lw,r2-r3)),^u(t,lw),^gs(r3,bei(113),nf?)");
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();
		assertThat(tree, is(notNullValue()));
	}

	/**
	 * Fails if input string does not equal parsed string. To verify quotes in attributes
	 * are parsed.
	 */
	@Test
	public void verifyThatAttributesWithQuotesAreParsed() {
		CharStream input = CharStreams.fromString("^kal(Rfl(\"ca\"))");
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		assertThat(tree.getText(), is("^kal(Rfl(\"ca\"))"));

	}

	/**
	 * Fails if input string does not equal parsed string. To verify attributes without
	 * quotes are parsed
	 */
	@Test
	public void verifyThatAttributesWithoutQuotesAreParsed() {
		CharStream input = CharStreams.fromString("^kal(Rfl(ca))");
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		assertThat(tree.getText(), is("^kal(Rfl(ca))"));

	}

	/**
	 * Fails, if input string matches parsed string. Tests if %-character (not in grammar)
	 * in input is parsed
	 */
	@Test
	public void verifyThatCharacterNotInGrammarIsNotParsed() {
		CharStream input = CharStreams.fromString("^kal({Rfl(\"ca\"))");
		PetroGrammarLexer lexer = new PetroGrammarLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PetroGrammarParser parser = new PetroGrammarParser(tokens);
		ParseTree tree = parser.schichtbeschreibung();

		assertThat(tree.getText(), not("^kal({Rfl(\"ca\"))"));
	}

}
